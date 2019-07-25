package net.ocsoft.mswp.ui

import kotlin.math.*
import org.w3c.dom.*
import org.khronos.webgl.*
import jQuery
import net.ocsoft.mswp.*
import kotlin.browser.*
import net.ocsoft.mswp.ui.grid.*

typealias GLRctx = WebGLRenderingContext

/**
 * game play ground grid
 */
class Grid(rowCount: Int = 4,
    columnCount: Int = 4,
    val buttons: Buttons = Buttons(MineButton(), rowCount, columnCount,
    floatArrayOf(0.02f, 0.02f), floatArrayOf(0.01f, 0.005f)),
    board: Board = Board(),
    val borderGap: FloatArray = floatArrayOf(0.02f, 0.02f),
    val boardEdge: FloatArray = floatArrayOf(0.03f, 0.03f),
    val renderingCtx: RenderingCtx = RenderingCtx()) {
    var model : Model? = null
    /**
     * open gl shader programs
     */
    var shaderPrograms : ShaderPrograms? = null

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
     * buttons row count
     */
    val rowCount: Int
        get() {
            return buttons.rowCount
        }
    /**
     * buttons column count
     */
    val columnCount: Int
        get() {
            return buttons.columnCount
        }

    /**
     * game board
     */
    var board = board 

    var display = Display(renderingCtx, buttons, board)
    /**
     * total button size
     */
    val totalButtonSize : FloatArray
        get() {
            return buttons.totalButtonSize
        }
    /**
     * total gap size
     */
    val totalButtonsGapSize : FloatArray
        get() {
            return buttons.totalGapSize
        }
    /**
     * gap for drawing
     */
    val gapForDrawing : FloatArray
        get() {
            val buttonsTotalGap = buttons.gapForDrawing
            return buttonsTotalGap
        } 
    /**
     * button z gap for drawing
     */ 
    val buttonZGapForDrawing : FloatArray
        get() {
           return buttons.zGapForDrawing
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
            val totalGapSize = this.totalButtonsGapSize
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
        pointLight: PointLight,
        shaderPrograms: ShaderPrograms) {
        this.model = model
        this.camera = camera
        this.pointLight = pointLight
        this.shaderPrograms
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
                buttons.mineButton.verticesAsFloat32, 
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
            buttons.mineButton.normalVectorsAsFloat32,
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
                buttons.mineButton.verticesColorAsFloat32,
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
            display.drawScene(gl)
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
        val buttonSize = this.buttons.mineButton.buttonSize
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
