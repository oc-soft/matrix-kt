package net.ocsoft.mswp.ui

import kotlin.math.*
import org.w3c.dom.*
import org.khronos.webgl.*
import jQuery
import net.ocsoft.mswp.*
import kotlin.browser.*
import net.ocsoft.mswp.ui.grid.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

typealias GLRctx = WebGLRenderingContext

/**
 * game play ground grid
 */
class Grid(rowCount: Int = 6,
    columnCount: Int = 6,
    colorMap: ColorMap = ColorMap(),
    val buttons: Buttons = Buttons(MineButton(), rowCount, columnCount,
        floatArrayOf(0.02f, 0.02f), floatArrayOf(0.01f, 0.005f), colorMap),
    board: Board = Board(),
    val borderGap: FloatArray = floatArrayOf(0.02f, 0.02f),
    val boardEdge: FloatArray = floatArrayOf(0.03f, 0.03f),
    val glyph: Glyph = Glyph(),
    val textures: Textures = Textures(),
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
     * web gl rendering context
     */
    var canvasId : String? = null

    /**
     * event handler
     */
    var onClickHandler : ((Event) -> Unit) ? = null
    /**
     * back color for drawing
     */
    val backColorForDrawing = floatArrayOf(0f, 0f, 0f, 1f)
 
    /**
     * back color for picking
     */
    val backColorForPicking = floatArrayOf(0f, 0f, 0f, 0f)
    /**
     * back ground color
     */
    var backColor = backColorForDrawing 
    
    /**
     * lighting context for drawing
     */
    val lightingContextEnabledForDrawing = true
 
    /**
     * lighting context
     */
    var lightingContextEnabled = lightingContextEnabledForDrawing 
    
 
    /**
     * initialize gl context
     */
    fun setupEnv(gl :WebGLRenderingContext) {
        gl.clearColor(backColor[0], backColor[1], backColor[2], backColor[3])
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
        drawSceneI(gl)
    }


    fun drawSceneI(gl: WebGLRenderingContext) {
    
        val renderingOperations: Array<Array<(WebGLRenderingContext) -> Unit>>
            = arrayOf(
                arrayOf(
                    { glctx -> beginDrawingForPicking(glctx) },
                    { glctx -> endDrawingForPicking(glctx) }),
                arrayOf(
                    { glctx -> beginDrawing(glctx) },
                    { glctx -> endDrawing(glctx) })
           )
        renderingOperations.forEach({ elem ->
            elem[0](gl)
            setupEnv(gl)
            updateView(gl)
            elem[1](gl)
        })
    }

    fun beginDrawingForPicking(gl: WebGLRenderingContext) {
        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
            renderingCtx.workableFramebuffer)
        display.buttonColorBufferBind = display.buttonColorForPickingBind 
        display.buttonColorDataForDraw = display.buttonColorDataForPicking  
        display.boardColorBufferBind = display.boardColorForPickingBind
        display.buttonTextureBind = display.buttonTextureBindForPicking 
        backColor = backColorForPicking
        lightingContextEnabled = false
    } 
    fun endDrawingForPicking(gl: WebGLRenderingContext) {
        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
            null)
        display.buttonColorBufferBind = display.buttonColorForDisplayBind 
        display.buttonColorDataForDraw = display.buttonColorDataForDisplay 
        display.buttonTextureBind = display.buttonTextureBindForDisplay 
        display.boardColorBufferBind = display.boardColorForDisplayBind
        backColor = backColorForDrawing
        lightingContextEnabled = lightingContextEnabledForDrawing
    } 

    fun beginDrawing(gl: WebGLRenderingContext) {
    } 
    fun endDrawing(gl: WebGLRenderingContext) {
    } 
     
    fun setup(gl: WebGLRenderingContext) {
        setupSceneBuffer(gl)
        setupShaderProgram(gl)
        setupBuffer(gl)
        setupCamera(gl)
        setupWorkingFrameBuffer(gl)
        setupTextures(gl)
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

    fun setupWorkingFrameBuffer(gl: WebGLRenderingContext) {
        renderingCtx.workableFramebuffer = gl.createFramebuffer() 
        val savedFramebuffer = gl.getParameter(
            WebGLRenderingContext.FRAMEBUFFER_BINDING) as
                WebGLFramebuffer?
  
        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
            renderingCtx.workableFramebuffer) 
        setupRenderbufferForPicking(gl)
        renderingCtx.buttonPickingColorBuffer = 
            createButtonColorBufferForPicking(gl)
        renderingCtx.boardPickingColorBuffer =
            createBoardColorBufferForPicking(gl) 
 
        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
            savedFramebuffer) 
    }
    fun setupSceneBuffer(gl: WebGLRenderingContext) {
        renderingCtx.sceneBuffer = gl.getParameter(
            WebGLRenderingContext.RENDERBUFFER_BINDING)
                as WebGLRenderbuffer?
    }
    /**
     * setup textrues
     */
    fun setupTextures(gl: WebGLRenderingContext) {
        renderingCtx.buttonTexture = gl.createTexture()
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
    
    fun onClick(event: Event) {
        if (event is MouseEvent) {
            val mouseEvent = event as MouseEvent
            val canvas = document.querySelector(
                canvasId!!) as HTMLCanvasElement
            val y = (canvas.height
                - mouseEvent.offsetY)   
            val x = mouseEvent.offsetX
            postHandleUserInput(x, y)
        }

    }
    
    fun postHandleUserInput(x : Double, y: Double) {
        window.setTimeout({
           handleUserInput(x, y)  
        }, 100)
    }
    fun handleUserInput(x : Double, y: Double) {
        val canvas = document.querySelector(
            canvasId!!) as HTMLCanvasElement
        val gl = canvas.getContext("webgl") as WebGLRenderingContext


        handleUserInput(gl, round(x).toInt(), round(y).toInt())
    } 
    fun handleUserInput(gl: WebGLRenderingContext,
        x: Int, y: Int) {
        val model = this.model!!
        beginDrawingForPicking(gl)
        val buffer = Uint8Array(4)
        gl.readPixels(x, y, 1, 1,
            WebGLRenderingContext.RGBA, 
            WebGLRenderingContext.UNSIGNED_BYTE, 
            buffer)
        val colorVal = ByteArray(buffer.length) { buffer[it] } 
        val location = buttons.findPositionByPickingColor(colorVal)
        if (location != null) {
            if (!model.logic.isOpened(location[0], location[1])) { 
                model.logic.startIfNot(location[0], location[1])
                if (!model.logic.status!!.inAnimating) {
                    
                    val cells = model.logic.getOpenableCells(
                        location[0], location[1])

                    startAnimation()
                    val buttonIndices = Array<IntArray>(cells.size) {
                        cells.elementAt(it).toIntArray()
                    }

                    cells.forEachIndexed({
                        idx, cell ->
                        buttonIndices[idx] = cell.toIntArray() 
                    })

                    Animation.setupButtons(buttons, 
                        model.logic.status!!.getOpenedIndices(),
                        buttonIndices, renderingCtx)
                    postStartAnimation(gl, { finishAnimation(location) })
                } 
            }
        }
        endDrawingForPicking(gl)
    }
    
    /**
     * start animation
     */
    fun startAnimation() {
        val model = this.model!!
        model.logic.status!!.inAnimating = true
    }
    /**
     * finish animation
     */
    fun finishAnimation(buttonIndices: IntArray) {
        val model = this.model!!
        model.logic.status!!.inAnimating = false 
        model.logic.registerOpened(buttonIndices[0], buttonIndices[1])
    }
    
    fun tapButton(gl: WebGLRenderingContext,
        rowIndex: Int, colIndex: Int) {
        
    } 
    
    /**
     * connect a canvas and create grid
     */
    fun bind(nodeQuery: String, 
        glyphCanvasId: String,
        model: Model,
        camera: Camera,
        pointLight: PointLight,
        shaderPrograms: ShaderPrograms) {
        
        model.logic.rowSize = rowCount
        model.logic.columnSize = columnCount
        this.model = model
        this.buttons.logic = model.logic
        this.buttons.glyph = this.glyph
        this.camera = camera
        this.pointLight = pointLight
        this.shaderPrograms = shaderPrograms
        this.canvasId = nodeQuery
        jQuery({
            val canvasNode = jQuery(nodeQuery)
            val canvas = canvasNode[0] as HTMLCanvasElement
            var gl = canvas.getContext("webgl") as WebGLRenderingContext
            onClickHandler = { event -> this.onClick(event) }
            canvas.addEventListener("click", onClickHandler)
            glyph.bind(glyphCanvasId)
            setup(gl)
            drawScene(gl)
        }) 
    }
    fun unbind() {
        val canvasNode = jQuery(this.canvasId!!)
        val canvas = canvasNode[0] as HTMLCanvasElement
        canvas.removeEventListener("click", onClickHandler) 
        this.onClickHandler = null
    }
    private fun postStartAnimation(gl: WebGLRenderingContext,
        animationFinishedListener: (()->Unit)) {
        window.setTimeout({ 
            startAnimation(gl, animationFinishedListener) 
        }, 100)
    }
    private fun startAnimation(gl: WebGLRenderingContext,
        animationFinishedListener: (()->Unit)) {
        var then = .0
        fun render(now : Double): Unit {
            Animation.doAnimate(renderingCtx)
            drawScene(gl)
 
            if (Animation.hasNextFrame(renderingCtx)) {
                window.requestAnimationFrame { render(it) } 
            } else {
                animationFinishedListener()
            }
        } 
        window.requestAnimationFrame { render(it) }
    }
        
    private fun setupBuffer(gl: WebGLRenderingContext) {
        renderingCtx.buttonBuffer = createButtonBuffer(gl)
        renderingCtx.buttonNormalVecBuffer = createButtonNormalVecBuffer(gl)
        renderingCtx.buttonColorBuffer = createButtonColorBuffer(gl)
        renderingCtx.buttonTextureCoordinatesBuffer =
            createButtonTextureCoordinateBuffer(gl)
        renderingCtx.boardBuffer = createBoardBuffer(gl)
        renderingCtx.boardNormalVecBuffer = createBoardNormalVecBuffer(gl)
        renderingCtx.boardColorBuffer = createBoardColorBuffer(gl)
    }
    private fun setupBufferForWorking(gl: WebGLRenderingContext) {
        renderingCtx.buttonPickingColorBuffer = 
            createButtonColorBufferForPicking(gl)
        renderingCtx.boardPickingColorBuffer =
            createBoardColorBufferForPicking(gl) 
    } 
    private fun setupRenderbufferForPicking(gl: WebGLRenderingContext) {
        renderingCtx.pickingBuffer = createPickingBuffer(gl)
        renderingCtx.depthBufferForWorking = 
            createDepthBufferForWorking(gl) 
    }

    /**
     * setup game element matrix
     */
    private fun setupMatrices() {
        renderingCtx.buttonMatrices = createButtonMatrices() 
        renderingCtx.buttonNormalVecMatrices = createButtonNormalVecMatrices()
        renderingCtx.buttonMatricesForDrawing = renderingCtx.buttonMatrices 
        renderingCtx.buttonNormalVecMatricesForDrawing =
            renderingCtx.buttonNormalVecMatrices

        renderingCtx.boardMatrix = createBoardMatrix() 
        renderingCtx.boardNormalVecMatrix = createBoardNormalVecMatrix()
        renderingCtx.spinAndVMotionMatrices = createSpinAndVMotionMatrices()
        renderingCtx.spinMotionMatrices = createSpinMatrices()
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
     * button texture coordinate buffer
     */
    private fun createButtonTextureCoordinateBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, result)
        gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
            buttons.mineButton.textureCoordinatesAsFloat32,
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
     * button color buffer for picking
     */
    private fun createButtonColorBufferForPicking(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
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
    /**
     * boad color buffer for picking
     */
    private fun createBoardColorBufferForPicking(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, result)
            
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                board.createVerticesColor(floatArrayOf(0f,0f, 0f, 0f)),
                WebGLRenderingContext.STATIC_DRAW)
        }
        return result
    }


      
    /**
     * create picking buffer
     */
    private fun createPickingBuffer(
        gl: WebGLRenderingContext): WebGLRenderbuffer? {
        val result = gl.createRenderbuffer()
        if (result != null) {
            val savedBuffer = gl.getParameter(
                WebGLRenderingContext.RENDERBUFFER_BINDING) 
                    as WebGLRenderbuffer?
 
            gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
                result)
            gl.renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
                WebGLRenderingContext.RGB5_A1,
                gl.canvas.width, gl.canvas.height)

            gl.framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
                WebGLRenderingContext.COLOR_ATTACHMENT0, 
                WebGLRenderingContext.RENDERBUFFER, result)
 
            gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
                savedBuffer) 
        }  
        return result
    }

    /**
     * create depth buffer for working
     */
    private fun createDepthBufferForWorking(
        gl: WebGLRenderingContext): WebGLRenderbuffer? {
        val result = gl.createRenderbuffer()
        if (result != null) {
            gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
                result) 
            gl.renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
                WebGLRenderingContext.DEPTH_COMPONENT16,
                gl.canvas.width, gl.canvas.height);
  
            gl.framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
                WebGLRenderingContext.DEPTH_ATTACHMENT, 
                WebGLRenderingContext.RENDERBUFFER, result)
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
            enableLightingContext(gl, lightingContextEnabled)
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
     * enable lighting
     */
    private fun enableLightingContext(
        gl: WebGLRenderingContext,
        enabled: Boolean) {
        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null) {
            val enableLoc = gl.getUniformLocation(shaderProg,
                "uEnableLighting")
            fun Boolean.toInt() = if (this) 1 else 0 
            gl.uniform1i(enableLoc as WebGLUniformLocation, 
                enabled.toInt());
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
        val shaderProg = this.shaderPrograms!!
        val prog = shaderProg.vertexShader
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
        val shaderProg = this.shaderPrograms!!
        val prog = shaderProg.fragmentShader
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
     * create buttons normal vector matrices
     */
    private fun createButtonNormalVecMatrices(): Array<FloatArray> {
        val unitMatrix = floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f) 
        return Array<FloatArray>(rowCount * columnCount) { unitMatrix }
    } 
    
    /**
     * create spin and vertical movement matrices.
     */
    fun createSpinAndVMotionMatrices(): Array<FloatArray>? {
        
        val t = 1f
        val rotationCount = 0.5f
        val axis = floatArrayOf(1f, 0f, 0f)
        var result : Array<FloatArray>? = null

        result = model!!.physicsEng.calcSpinAndVerticalMotion1(t, axis, 
            rotationCount)
        return result 
    }
    
    /**
     * create spin movement  matrices.
     */
    fun createSpinMatrices(): Array<FloatArray>? {
        val t = 1f
        val rotationCount = 0.5f
        val axis = floatArrayOf(1f, 0f, 0f)
        var result : Array<FloatArray>? = null

        result = model!!.physicsEng.calcSpinMotion(t, axis, 
            rotationCount)
        return result 
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
    /**
     * create board normal vector matrix
     */
    fun createBoardNormalVecMatrix() : FloatArray {
        return floatArrayOf(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
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
