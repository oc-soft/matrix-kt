package net.ocsoft.mswp.ui

import kotlin.math.*
import org.w3c.dom.*
import org.khronos.webgl.*
import jQuery
import net.ocsoft.mswp.*
import kotlin.browser.*

typealias GLRctx = WebGLRenderingContext

/**
 * game play ground grid
 */
class Grid(val rowCount: Int = 4,
    val columnCount: Int = 4,
    mineButton: MineButton = MineButton(),
    board: Board = Board(),
    val buttonGap: FloatArray = floatArrayOf(0.02f, 0.02f),
    val borderGap: FloatArray = floatArrayOf(0.02f, 0.02f),
    val boardEdge: FloatArray = floatArrayOf(0.03f, 0.03f),
    val buttonZGap: FloatArray = floatArrayOf(0.01f, 0.005f),
    val renderingCtx: RenderingCtx = RenderingCtx()) {
    var model : Model? = null

    var camera : Camera? = null
        set(value) {
            if (field != value) {
                if (field != null) {
                    detachCameraListener()
                }
                field = value
                if (field != null) {
                    attachCameraListener()
                }
            }
        }
  
    var pointLight: PointLight? = null
        set(value) {
            if (field != value) {
                field = value 
            }
        }
    /**
     * button element
     */
    var mineButton = mineButton

    /**
     * game board
     */
    var board = board 
    /**
     * total button size
     */
    val totalButtonSize : FloatArray
        get() {
            val buttonSize = mineButton.buttonSize
            return floatArrayOf(
                buttonSize[0] * columnCount,
                buttonSize[1] * rowCount)
        }
    /**
     * total gap size
     */
    val totalGapSize : FloatArray
        get() {
            val totalButtonSize = this.totalButtonSize
            val columnRows = arrayOf(columnCount, rowCount)
            return totalButtonSize.mapIndexed({
                i, value ->
                    value * buttonGap[i] * (columnRows[i] - 1)
            }).toFloatArray()
        }
    /**
     * gap for drawing
     */
    val gapForDrawing : FloatArray
        get() {
            val totalButtonSize = this.totalButtonSize
            return totalButtonSize.mapIndexed(
                { i, value -> buttonGap[i] * value }).toFloatArray()
        } 
    /**
     * button z gap for drawing
     */ 
    val buttonZGapForDrawing : FloatArray
        get() {
            val totalButtonSize = this.totalButtonSize
            val maxSize = totalButtonSize.max() as Float
            return buttonZGap.map({ it * maxSize }).toFloatArray()
        }
    /**
     * border size
     */
    val borderSize : FloatArray
        get() {
            return this.totalButtonSize.mapIndexed({
                i, value -> boardEdge[i] * value }).toFloatArray()
       } 
    /**
     * board edge size
     */
    val boardEdgeSize : FloatArray
        get() {
            return totalButtonSize.mapIndexed({
                i, value -> boardEdge[i] * value }).toFloatArray()
        }
    /**
     * board size
     */
    val boardSize : FloatArray
        get() {
            val totalButtonSize = this.totalButtonSize
            val totalGapSize = this.totalGapSize
            val borderSize = this.borderSize
            val boardEdgeSize = this.boardEdgeSize 
            
            return totalButtonSize.mapIndexed({ index, size -> 
                var boardSize = size
                boardSize += totalGapSize[index]
                boardSize += borderSize[index]
                boardSize += boardEdgeSize[index]
                boardSize
            }).toFloatArray()
        }
    
    /**
     * initialize gl context
     */
    fun setupEnv(gl :WebGLRenderingContext) {
        gl.clearColor(0.0f, 0.0f, 0.0f, 1.0f)
        gl.clear(WebGLRenderingContext.COLOR_BUFFER_BIT)
        gl.enable(GLRctx.DEPTH_TEST)
        gl.frontFace(WebGLRenderingContext.CW)
        gl.depthFunc(GLRctx.LEQUAL)
        gl.clear(GLRctx.COLOR_BUFFER_BIT or GLRctx.DEPTH_BUFFER_BIT)
    }
    /**
     * draw scene
     */
    fun drawScene(gl: WebGLRenderingContext) {
       setupEnv(gl)
       updateView(gl)
    }
    fun setup(gl: WebGLRenderingContext) {
        setupShaderProgram(gl)
        setupBuffer(gl)
        setupCamera(gl)
        setupMatrices()
    }
    fun teardown(gl: WebGLRenderingContext) {
        renderingCtx.tearDown(gl)
    }
    /**
     * set up camera
     */
    fun setupCamera(gl: WebGLRenderingContext) {
        val cam = this.camera
        if (cam != null) {
            detachCameraListener()
            var aspect = gl.canvas.clientWidth.toFloat()
            aspect /= gl.canvas.clientHeight
            cam.aspect = aspect
            attachCameraListener() 
        } 
    }
    fun setupShaderProgram(gl: WebGLRenderingContext) {
        val vertexShader : WebGLShader? = createVertexShader(gl)
        var fragmentShader : WebGLShader? = createFragmentShader(gl)
        if (this.renderingCtx.shaderProgram != null) {
            gl.deleteProgram(this.renderingCtx.shaderProgram)
            this.renderingCtx.shaderProgram = null
        }
        if (vertexShader != null 
            && fragmentShader != null) { 
            val shaderProg = gl.createProgram()  
            if (shaderProg != null) {
                gl.attachShader(shaderProg, vertexShader)
                gl.attachShader(shaderProg, fragmentShader)
                gl.linkProgram(shaderProg)
 
                renderingCtx.shaderProgram = 
                    assertLinkError(gl, shaderProg)
            }
        }
    }

    /**
     * connect a canvas and create grid
     */
    fun bind(nodeQuery: String, 
        model: Model,
        camera: Camera,
        pointLight: PointLight) {
        this.model = model
        this.camera = camera
        this.pointLight = pointLight
        jQuery({
            val canvasNode = jQuery(nodeQuery)
            val canvas = canvasNode[0] as HTMLCanvasElement
            var gl = canvas.getContext("webgl") as WebGLRenderingContext
            setup(gl)
            drawScene(gl)
        }) 
    }
    private fun startAnimation(gl: WebGLRenderingContext) {
        var then = .0
        fun render(now : Double): Unit {

            val now1 = now * 0.001
            var deltaTime = now1 - then
            // matrixRotation += deltaTime.toFloat()
            then = now1
            drawScene(gl)
            window.requestAnimationFrame { render(it) } 
        } 
        window.requestAnimationFrame { render(it) }
    }
        
    private fun setupBuffer(gl: WebGLRenderingContext) {
        renderingCtx.buttonBuffer = createButtonBuffer(gl)
        renderingCtx.buttonNormalVecBuffer = createButtonNormalVecBuffer(gl)
        renderingCtx.buttonColorBuffer = createButtonColorBuffer(gl)

        renderingCtx.boardBuffer = createBoardBuffer(gl)
        renderingCtx.boardNormalVecBuffer = createBoardNormalVecBuffer(gl)
        renderingCtx.boardColorBuffer = createBoardColorBuffer(gl)
    }

    /**
     * setup game element matrix
     */
    private fun setupMatrices() {
        renderingCtx.buttonMatrices = createButtonMatrices() 
        renderingCtx.boardMatrix = createBoardMatrix() 
    }
    /**
     * button buffer
     */
    private fun createButtonBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,
                result)
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                mineButton.verticesAsFloat32, 
                WebGLRenderingContext.STATIC_DRAW) 
        }
        return result
    }
    /**
     * button index buffer
     */
    private fun createButtonNormalVecBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, result)
        gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
            mineButton.normalVectorsAsFloat32,
            WebGLRenderingContext.STATIC_DRAW)
        return result
    }
    /**
     * button color buffer
     */
    private fun createButtonColorBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, result)
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                mineButton.verticesColorAsFloat32,
                WebGLRenderingContext.STATIC_DRAW)
        }
        return result
    }

    /**
     * board buffer
     */
    private fun createBoardBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,
                result)
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                board.verticesAsFloat32, 
                WebGLRenderingContext.STATIC_DRAW) 
        }
        return result
    }
    /**
     * board normal vector buffer
     */
    private fun createBoardNormalVecBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, result)
        gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
            board.normalVectorsAsFloat32,
            WebGLRenderingContext.STATIC_DRAW)
        return result
    }
    /**
     * board color buffer
     */
    private fun createBoardColorBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, result)
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                board.verticesColorAsFloat32,
                WebGLRenderingContext.STATIC_DRAW)
        }
        return result
    }

    private fun updateView(gl: WebGLRenderingContext) {
        val cam = this.camera
        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null) {
            gl.useProgram(shaderProg)
            enableLightingAttrib(gl)
            enableCameraAttrib(gl) 
            updateCamera(gl)
            updateButtons(gl)
            updateBoard(gl)
        }
    }
   
    private fun updateCamera(gl: WebGLRenderingContext) {
        val cam = this.camera
        val shaderProg = this.renderingCtx.shaderProgram

        if (cam != null && shaderProg != null) {
            gl.useProgram(shaderProg)
            val projMat = mat4.create()
            val viewMat = mat4.create()
            val uProjMat = gl.getUniformLocation(shaderProg, 
                "uProjectionMatrix")    
            
            
            val camCenter = cam.center
            val camEye = cam.eye
            val camUp = cam.up
            val camCenterForGl = vec3.create()
            val camEyeForGl = vec3.create()
            val camUpForGl = vec3.create()

            vec3.set(camCenterForGl, camCenter[0], camCenter[1], camCenter[2])
            vec3.set(camEyeForGl, camEye[0], camEye[1], camEye[2])
            vec3.set(camUpForGl, camUp[0], camUp[1], camUp[2])
              
            mat4.lookAt(viewMat, 
                camEyeForGl,
                camCenterForGl,
                camUpForGl)
            
            mat4.perspective(projMat, cam.fieldOfView,
                cam.aspect, cam.zNear, cam.zFar)

            mat4.multiply(projMat, projMat, viewMat)  
            
            gl.uniformMatrix4fv(uProjMat, false,
                projMat as Float32Array)
        }
    }
    private fun updateButtons(gl: WebGLRenderingContext) {
        val shaderProg = this.renderingCtx.shaderProgram
        val cam = this.camera
        if (shaderProg != null && cam != null) {
            gl.useProgram(shaderProg)

            val verLoc = gl.getAttribLocation(shaderProg, 
                "aVertexPosition")
            val verColor = gl.getAttribLocation(shaderProg,
                "aVertexColor")
            val normalVecLoc = gl.getAttribLocation(shaderProg,
                "aNormalVector")
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                renderingCtx.buttonBuffer)
            gl.vertexAttribPointer(
                verLoc,
                3,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            gl.enableVertexAttribArray(verLoc)
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,  
                renderingCtx.buttonColorBuffer)
            gl.vertexAttribPointer( 
                verColor,
                4,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            gl.enableVertexAttribArray(verColor)

            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                renderingCtx.buttonNormalVecBuffer)
            gl.vertexAttribPointer(
                normalVecLoc, 3,
                WebGLRenderingContext.FLOAT,
                false, 0, 0)
            gl.enableVertexAttribArray(normalVecLoc)


            gl.bindBuffer(
                WebGLRenderingContext.ARRAY_BUFFER,
                renderingCtx.buttonBuffer)

            for (rowIndex in 0 until rowCount) {
                for (colIndex in 0 until columnCount) { 
                    updateButtonViewMatrix(gl, rowIndex, colIndex)
                    gl.drawArrays(
                        mineButton.drawingMode, 0,
                        mineButton.vertices.size / 3) 
                }
            }
        }
        
    }
    private fun updateButtonViewMatrix(
        gl: WebGLRenderingContext,
        rowIndex : Int,
        columnIndex : Int) {
        val cam = this.camera
        val shaderProg = this.renderingCtx.shaderProgram


        if (cam != null && shaderProg != null) {
            val uModelMat = gl.getUniformLocation(shaderProg,
                "uModelViewMatrix")
            renderingCtx.buttonMatrices!![
                rowIndex * columnCount + columnIndex]
            val mat = renderingCtx.buttonMatrices!![
                    rowIndex * columnCount + columnIndex]
            gl.uniformMatrix4fv(uModelMat, false, 
                Float32Array(Array<Float>(mat.size) { i -> mat[i] }))
        }
    }
    private fun updateBoard(gl: WebGLRenderingContext) {
        val shaderProg = this.renderingCtx.shaderProgram
        val cam = this.camera
        if (shaderProg != null && cam != null) {
            gl.useProgram(shaderProg)
            val uModelMat = gl.getUniformLocation(shaderProg,
                "uModelViewMatrix")
            val verLoc = gl.getAttribLocation(shaderProg, 
                "aVertexPosition")
            val verColor = gl.getAttribLocation(shaderProg,
                "aVertexColor")
            
            val normalVecLoc = gl.getAttribLocation(shaderProg,
                "aNormalVector")
            
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                renderingCtx.boardBuffer)
            gl.vertexAttribPointer(
                verLoc,
                3,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            gl.enableVertexAttribArray(verLoc)
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,  
                renderingCtx.boardColorBuffer)
            gl.vertexAttribPointer( 
                verColor,
                4,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            gl.enableVertexAttribArray(verColor)
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                renderingCtx.boardNormalVecBuffer)
            gl.vertexAttribPointer(
                normalVecLoc, 3,
                WebGLRenderingContext.FLOAT,
                false, 0, 0)
            gl.enableVertexAttribArray(normalVecLoc)

            gl.bindBuffer(
                WebGLRenderingContext.ARRAY_BUFFER,
                renderingCtx.boardBuffer)
            val mat =  renderingCtx.boardMatrix!!
            gl.uniformMatrix4fv(uModelMat, false, 
                Float32Array(Array<Float>(mat.size) { i -> mat[i] }))
            gl.drawArrays(
                board.drawingMode, 
                0, 
                board.vertices.size / 3) 
            
        }
        
    }
    /**
     * enable lighting related Attribute
     */
    private fun enableLightingAttrib(gl: WebGLRenderingContext) {
        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null) {
            val lightPosLoc = gl.getUniformLocation(shaderProg,
                "uLightPosition")
            
            gl.uniform3f(lightPosLoc as WebGLUniformLocation, 
                pointLight!!.point[0], 
                pointLight!!.point[1],
                pointLight!!.point[2])
        }
    } 

    /**
     * enable camera related Attribute
     */
    private fun enableCameraAttrib(gl: WebGLRenderingContext) {
        val cam = this.camera
        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null && cam != null) {
            val eyeLoc = gl.getUniformLocation(shaderProg,
                "uEyePosition")
            
            

            gl.uniform3f(eyeLoc as WebGLUniformLocation, 
                cam.eye[0], 
                cam.eye[1],
                cam.eye[2])
        }
    } 
     private fun assertLinkError(
        gl: WebGLRenderingContext,
        prog: WebGLProgram): WebGLProgram? {
        var result: WebGLProgram? = null
        if (gl.getProgramParameter(prog,
            WebGLRenderingContext.LINK_STATUS)!! as Boolean) {
            result = prog
        }
        return result 
    }  
    private fun createVertexShader(gl: WebGLRenderingContext): WebGLShader? {
        val prog = """
    attribute vec4 aVertexPosition;
    attribute vec4 aNormalVector;
    attribute vec4 aVertexColor;
    uniform mat4 uModelViewMatrix;
    uniform mat4 uProjectionMatrix;
    varying vec4 vPosition;
    varying vec4 vNormal;
    varying vec4 vColor;
    void main() {
        vNormal = aNormalVector;
        vPosition = uModelViewMatrix * aVertexPosition;
        gl_Position = uProjectionMatrix * vPosition;
        vColor = aVertexColor;
    }
"""
        var result : WebGLShader? = null
        val shader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER)
        if (shader != null) {
            gl.shaderSource(shader, prog)
            gl.compileShader(shader)
            result = assertCompileError(gl, shader)
        }
        return result
    } 
    private fun createFragmentShader(gl: WebGLRenderingContext): WebGLShader? {
        val prog = """
    precision mediump float;
    uniform vec3 uLightPosition;
    uniform vec3 uEyePosition;
    varying vec4 vNormal;
    varying vec4 vPosition;
    varying vec4 vColor;
    void main() {
        vec3 lightVec = uLightPosition - vPosition.xyz;
        vec3 viewDir = normalize(uEyePosition - vPosition.xyz);
        vec3 reflectDir = reflect(- lightVec, vNormal.xyz);
        float specular = pow(max(dot(viewDir, reflectDir), 0.0), 1.0) * 0.8;
        gl_FragColor = vColor * vec4(vec3(specular), 1.0);
    }
"""

        var result : WebGLShader? = null
        val shader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER)
        if (shader != null) {
            gl.shaderSource(shader, prog)
            gl.compileShader(shader)
            result = assertCompileError(gl, shader)
        }
        return result
    } 
    private fun assertCompileError(
        gl: WebGLRenderingContext,
        shader: WebGLShader): WebGLShader? {
        var result : WebGLShader? = null 
        if (gl.getShaderParameter(shader,
            WebGLRenderingContext.COMPILE_STATUS)!! as Boolean) {
            result = shader 
        }
        return result
    }
   
    /**
     * create buttons matrices
     */
    private fun createButtonMatrices(): Array<FloatArray> {
        val boardSize = this.boardSize
        val buttonSize = this.mineButton.buttonSize
        val borderSize = this.borderSize 
        val boardEdgeSize = this.boardEdgeSize
        val boardLeftBottom = arrayOf(- boardSize[0] / 2, - boardSize[1] / 2)
        val gaps = gapForDrawing
        val locations = Array<FloatArray> (rowCount * columnCount) {
            i ->
            val indices = arrayOf(i % columnCount, i / columnCount)
            FloatArray(2) {
                j ->
                var result = boardLeftBottom[j]
                result += borderSize[j] / 2
                result += boardEdgeSize[j] / 2
                result += indices[j] * gaps[j]
                result += indices[j] * buttonSize[j] 
                result += buttonSize[j] / 2
                result
            }
        }
        val zGap = buttonZGapForDrawing
        return Array<FloatArray>(locations.size){ i ->
            floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                locations[i][0], locations[i][1], zGap[0], 1f); 
        }
    } 
    
    /**
     * create board matrix
     */
    fun createBoardMatrix(): FloatArray {
        val boardSize = this.boardSize
        return floatArrayOf(
            boardSize[0], 0f, 0f, 0f,
            0f, boardSize[1], 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f)
    }
    private fun attachCameraListener() {
        camera?.on(null, this::onCameraChanged)
    }
    private fun detachCameraListener() {
        camera?.off(null, this::onCameraChanged)
    }
    private fun onCameraChanged(eventName : String, camera : Camera) {
    }
}
