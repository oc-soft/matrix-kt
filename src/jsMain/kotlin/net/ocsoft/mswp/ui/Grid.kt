package net.ocsoft.mswp.ui

import kotlin.math.*
import org.w3c.dom.*
import org.khronos.webgl.*
import jQuery
import JQueryEventObject
import net.ocsoft.mswp.*
import kotlin.collections.Set
import kotlinx.browser.document
import kotlinx.browser.window
import net.ocsoft.mswp.ui.grid.*
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.Image

/**
 * game play ground grid
 */
class Grid(val pointLightSettingOption: PointLightSetting.Option,
    rowCount: Int = 6,
    columnCount: Int = 6,
    colorScheme: ColorScheme = ColorScheme(),
    colorMap: ColorMap = ColorMap(),
    val buttons: Buttons = Buttons(
        MineButton(colorScheme), rowCount, columnCount,
        floatArrayOf(0.02f, 0.02f), floatArrayOf(0.01f, 0.005f), colorMap),
    board: Board = Board(colorScheme),
    pointLightEdit: PointLight = PointLight(),
    shadowMap: ShadowMap = ShadowMap(),
    val borderGap: FloatArray = floatArrayOf(0.02f, 0.02f),
    val boardEdge: FloatArray = floatArrayOf(0.03f, 0.03f),
    val glyph: Glyph = Glyph(colorScheme),
    val textures: Textures = Textures(),
    val renderingCtx: RenderingCtx = RenderingCtx()) {
    var model : Model? = null
    /**
     * open gl shader programs
     */
    var shaderPrograms : ShaderPrograms? = null

    /**
     * application settings
     */
    var appSettings: AppSettings? = null
        set(value) {
            if (value != field) {
                val oldAppSettings = field 
                if (oldAppSettings != null) {
                    oldAppSettings.handleToEditLighting = null
                    oldAppSettings.colorSchemeContainer = null
                }
                field = value
                if (value != null) {
                    value.handleToEditLighting = { 
                        onEditLightSetting()
                    }
                    value.colorSchemeContainer = colorSchemeContainer
                }
            }
        }

    /**
     * glrs interface
     */
    var glrs : glrs.InitOutput? 
        get() {
            return renderingCtx.glrs
        }
        set(value) {
            renderingCtx.glrs = value
        }

    /**
     * camera
     */
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
    /**
     * viewport 
     */
    val viewport : IntArray?
        get() {
            val id = canvasId
            var result: IntArray? = null
            if (id != null) {
                val canvas = document.querySelector(id) as HTMLCanvasElement
                result = intArrayOf(0, 0, canvas.width, canvas.height) 
            }
            return result
        }
  
    /**
     * point lighting
     */
    var pointLight: net.ocsoft.mswp.PointLight?
        get() {
            return pointLightEdit.pointLight
        }
        set(value) {
            pointLightEdit.pointLight = value
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

    /**
     * display
     */
    var display = Display(renderingCtx, buttons, board, pointLightEdit)

    /**
     * instance to edit point light 
     */
    var pointLightEdit = pointLightEdit 

    /**
     * instance for managing shadow mapping
     */
    var shadowMap = shadowMap
      
    /**
     * the editor for point light
     */
    val pointLightSetting: PointLightSetting
        get() {
            val result = PointLightSetting(pointLightSettingOption)
            result.grid = this
            return result
        }  
    /**
     * is editing point light
     */
    val isEditingPointLight: Boolean
        get() {
            return pointLightSetting.isEditing
        }

    /**
     * color scheme
     */
    var colorScheme: ColorScheme = colorScheme
        get() {
            return field 
        }
        set(value) {
            if (value != field) {
                field = value 
                postSyncWithColorScheme() 
            }
        }

    /**
     * color scheme container
     */
    val colorSchemeContainer: ColorSchemeContainer
        get() {
            val grid = this
            return object : ColorSchemeContainer {
                override var colorScheme: ColorScheme
                    get() {
                        return grid.colorScheme
                    } 
                    set(value) {
                        grid.colorScheme = value 
                    }
            }
        }

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
     * environment
     */
    var environment: Environment? = null
    /**
     * web gl rendering context
     */
    var canvasId : String? = null

    /**
     * canvas element
     */
    val canvas: HTMLElement?
        get() {
            var result: HTMLElement? = null
            val canvasId = this.canvasId
            if (canvasId != null) {
                val canvasNode = jQuery(canvasId)
                if (canvasNode.length.toInt() > 0) {
                    result = canvasNode[0] as HTMLCanvasElement
                }
            }
            return result 
        }
    /**
     * rendering context
     */
    val renderingContext: WebGLRenderingContext?
        get() {
            var result: WebGLRenderingContext? = null
            val canvasElem = this.canvas
            if (canvasElem != null) {
                val canvas = canvasElem as HTMLCanvasElement 
                result = canvas.getContext("webgl") as WebGLRenderingContext
            }
            return result
        }
 
    /**
     * game over modal dialog
     */
    var gameOverModalId : String? = null

    /**
     * player won the game daialog
     */
    var playerWonModalId : String? = null

    /**
     * menu setting item query for html dom node
     */
    var mainMenuItemQuery : String? = null

    /**
     * back to main interface ui html node id
     */
    var backToMainQuery: String? = null

    /**
     * event handler
     */
    var onClickHandler : ((Event) -> Unit) ? = null

    /**
     * touch event handler
     */
    var onTouchHandler : ((TouchEvent) -> Unit) ? = null

    /**
     * game over modal hidden handler
     */
    var onHiddenGameOverModalHandler: ((JQueryEventObject, Any) -> Any)? = null

    /**
     * game over modal hidden handler
     */
    var onHiddenPlayerWonModalHandler: ((JQueryEventObject, Any) -> Any)? = null
 

    /**
     * play again button handler
     */
    var onClickToPlayAgainHandler: ((JQueryEventObject, Any) -> Any)? = null

    /**
     * hadler to respond window resize
     */
    var onResizedHdlr: ((event: Event) -> Unit)? = null

    
    /**
     * next game operation
     */
    var nextGameOperation: (() -> Unit)? = null

    /**
     * icon changed handler
     */
    var onMineIconChanged: ((Any?, String)->Unit)? = null

    /**
     * set up shadow dummy
     */
    val setupShadow0: ((Grid, WebGLRenderingContext)->Unit) = {
            _, _->
        }

    /**
     * set up shadow
     */
    val setupShadow1: ((Grid, WebGLRenderingContext)->Unit) = {
        grid, gl ->
        grid.shadowMap.setup(grid, gl)
        grid.setupShadow = grid.setupShadow0 
    }

    /**
     * setting up shadow environemnt
     */ 
    var setupShadow: ((Grid, WebGLRenderingContext)->Unit) = setupShadow0


    /**
     * icon setting. you can access this setting while ui is bound.
     */
    var iconSetting: IconSetting? = null 

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
     * enable shadow depth rendering
     */
    var shadowDepthEnabled = true
 
    /**
     * initialize gl context
     */
    fun setupEnv(gl :WebGLRenderingContext) {
        gl.clearColor(backColor[0], backColor[1], backColor[2], backColor[3])

        gl.enable(WebGLRenderingContext.DEPTH_TEST)
        gl.enable(WebGLRenderingContext.BLEND)
        gl.blendFunc(WebGLRenderingContext.SRC_ALPHA,
            WebGLRenderingContext.ONE_MINUS_SRC_ALPHA)
        gl.frontFace(WebGLRenderingContext.CW)
        gl.depthFunc(WebGLRenderingContext.LEQUAL)
        gl.clear(WebGLRenderingContext.COLOR_BUFFER_BIT or
            WebGLRenderingContext.DEPTH_BUFFER_BIT)
    }
    /**
     * draw scene
     */
    fun drawScene() {
        val gl = this.renderingContext
        if (gl != null) {
            drawScene(gl) 
        }
    }

    /**
     * draw scen lately.
     */
    fun postDrawScene() {
        val gl = this.renderingContext
        if (gl != null) {
            postDrawScene(gl)
        }
    }
      
    /**
     * draw scene lately.
     */
    fun postDrawScene(gl: WebGLRenderingContext) {
        window.setTimeout({
            drawScene(gl)
        }, 100)
    }

    /**
     * draw scene
     */
    fun drawScene(gl: WebGLRenderingContext) {
        setupShadow(this, gl)   
        shadowMap.drawScene(this, gl)
        drawSceneI(gl)
    }

    /**
     * invalidate shadow setting
     */
    fun invalidateShadowSetting() {
        setupShadow = setupShadow1
    }

    /**
     * draw scene internal
     */
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
        renderingOperations.forEach { elem ->
            elem[0](gl)
            setupEnv(gl)
            updateView(gl)
            elem[1](gl)
        }
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
        shadowDepthEnabled = false

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
        shadowDepthEnabled = true
    } 

    @Suppress("UNUSED_PARAMETER")
    fun beginDrawing(gl: WebGLRenderingContext) {
    } 
    fun endDrawing(gl: WebGLRenderingContext) {
        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
            null)
    } 

    /**
     * setup 3d rendering environment
     */ 
    fun setup(gl: WebGLRenderingContext)  {
        setupSceneBuffer(gl)
        setupShaderProgram(gl)
        setupBuffer(gl)
        setupCamera(gl)
        setupWorkingFramebuffer(gl)
        setupTextures(gl)
        setupMatrices()
        pointLightEdit.setupLightUiPlane(this)
        pointLightEdit.setupBuffer(gl, renderingCtx) 
        shadowMap.setup(this, gl)
    }
    fun teardown(gl: WebGLRenderingContext) {
        renderingCtx.tearDown(gl)
    }
    /**
     * set up camera
     */
    fun setupCamera(gl: WebGLRenderingContext) {
        detachCameraListener()
        syncCameraAspectWithContext(gl)
        attachCameraListener() 
    }
    /**
     * synchronize camera aspect with rendering context
     */
    fun syncCameraAspectWithContext(gl: WebGLRenderingContext) {
        var aspect = gl.canvas.clientWidth.toFloat()
        aspect /= gl.canvas.clientHeight
        this.camera?.aspect = aspect
     }

    /**
     * set up frame buffer for picking
     */
    fun setupWorkingFramebuffer(gl: WebGLRenderingContext) {
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

    /**
     * set up scene buffer
     */
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
        textures.setup(gl, glyph)
    }

    /**
     * setup shader programs
     */
    fun setupShaderProgram(gl: WebGLRenderingContext) {
        val vertexShaders = createVertexShaders(gl)
        var fragmentShaders = createFragmentShaders(gl)
        this.renderingCtx.teardownShaderProgram(gl)
        this.renderingCtx.shaderPrograms = Array<WebGLProgram?>(
            minOf(vertexShaders.size, fragmentShaders.size)) {
            val vertexShader = vertexShaders[it]
            val fragmentShader = fragmentShaders[it] 
            var shaderProg: WebGLProgram? = null
            if (vertexShader != null 
                && fragmentShader != null) { 
                shaderProg = gl.createProgram()  
                if (shaderProg != null) {
                    gl.attachShader(shaderProg, vertexShader)
                    gl.attachShader(shaderProg, fragmentShader)
                    gl.linkProgram(shaderProg)
                    assertLinkError(gl, shaderProg)
                }
            }
            shaderProg
        }
    
    }
    /**
     * on clik event
     */ 
    fun onClick(event: Event) {
        if (event is MouseEvent) {
            val mouseEvent = event 
            val canvas = document.querySelector(
                canvasId!!) as HTMLCanvasElement
            val y = (canvas.height
                - mouseEvent.offsetY)   
            val x = mouseEvent.offsetX
            postHandleUserInput(x, y)
        }
    }


    /**
     * on touch event
     */
    fun onTouch(event: TouchEvent) {
        console.log(event.type) 
    }

    /**
     * handle user input
     */ 
    fun postHandleUserInput(x : Double, y: Double) {
        window.setTimeout({
           handleUserInput(x, y)  
        }, 100)
    }
    /**
     * handle user input
     */
    fun handleUserInput(x : Double, y: Double) {
        val gl = renderingContext
        if (gl != null) {
            if (!isEditingPointLight) {
                handleUserInput(gl, round(x).toInt(), round(y).toInt())
            } else {
                handleUserInputForLightEdit(
                    gl, round(x).toInt(), round(y).toInt())
            }
        }
        
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
                if (!model.logic.status!!.inAnimating
                    && !model.logic.isOver) {
                    
                    var cells : Set<CellIndex> = model.logic.getOpenableCells(
                        location[0], location[1])
                    if (cells.size == 0) {
                        cells = model.logic.mineLocations
                    }
                
                    startAnimation(cells)
                    val buttonIndices = Array<IntArray>(cells.size) {
                        cells.elementAt(it).toIntArray()
                    }
                    

                    Animation.setupButtons(buttons, 
                        model.logic.status!!.getOpenedIndices(),
                        buttonIndices, renderingCtx)
                    postStartAnimation(gl, { finishAnimation(cells) })
                    // update session
                    Activity.record()
                } 
            }
        }
        endDrawingForPicking(gl)
    }

    /**
     * handle user input for editing point-light
     */
    fun handleUserInputForLightEdit(wgl: WebGLRenderingContext,
        x: Int, y: Int) {
        pointLightEdit.handleUserInput(this, wgl, x, y)
    }
    
    /**
     * start animation
     */
    fun startAnimation(cells: Set<CellIndex>?) {
        val model = this.model!!
        
        model.logic.status!!.inAnimating = true
        model.logic.status!!.openingButtons = cells
    }
    /**
     * finish animation
     */
    fun finishAnimation(cells: Set<CellIndex>?) {
        val model = this.model!!
        model.logic.status!!.openingButtons = null
        model.logic.status!!.inAnimating = false 

    
        if (cells != null) {
            cells.forEach({ 
                model.logic.registerOpened(it.row, it.column)
            })
        }
        if (model.logic.isOver) {
            if (model.logic.gamingStatus == GamingStatus.LOST) {
                postDisplayGameOverModal()    
            } else {
                postDisplayPlayerWonModal()    
            }
        }
     }
    
    @Suppress("UNUSED_PARAMETER")
    fun tapButton(gl: WebGLRenderingContext,
        rowIndex: Int, colIndex: Int) {
        
    } 
    
    /**
     * connect a canvas and create grid
     */
    fun bind( 
        settings: GridSettings,
        model: Model,
        camera: Camera,
        pointLight: net.ocsoft.mswp.PointLight,
        colorScheme: ColorScheme,
        environment: Environment,
        shaderPrograms: ShaderPrograms,
        appSettings: AppSettings) {
        
        model.logic.rowSize = rowCount
        model.logic.columnSize = columnCount
        this.model = model
        this.buttons.logic = model.logic
        this.buttons.textures = this.textures
        this.board.textures = this.textures
        this.camera = camera
        this.pointLight = pointLight
        this.colorScheme = colorScheme
        this.shaderPrograms = shaderPrograms
        this.canvasId = settings.canvasId 
        this.gameOverModalId = settings.gameOverModalId
        this.playerWonModalId = settings.playerWonModalId
        this.backToMainQuery = settings.backToMainQuery
        this.mainMenuItemQuery = settings.mainMenuItemQuery
        this.environment = environment
        this.appSettings = appSettings
        this.onMineIconChanged = {
            sender, msg ->
            handleIconChanged(sender, msg)
        }
        
        this.iconSetting = settings.iconSetting
        this.iconSetting?.addListener(this.onMineIconChanged!!)
        jQuery {
           
            onClickHandler = { event -> this.onClick(event) }
            onTouchHandler = { event -> this.onTouch(event) }

            onClickToPlayAgainHandler = {
                event, args ->
                this.handleClickToPlayAgain(event, args)
                Activity.record()
            }
            onResizedHdlr = {
                event ->
                onResized(event)
            }

            val touchEventHandler = this.onTouchHandler as ((Event)->Unit)

            setupGameOverModal()
            setupPlayerWonModal()
            val canvas = this.canvas
            syncCanvasWithClientSize { syncViewportWithCanvasSize() }
            window.addEventListener("resize", onResizedHdlr!!)
            canvas?.addEventListener("click", onClickHandler)
            canvas?.addEventListener("touchstart", touchEventHandler)
            canvas?.addEventListener("touchend", touchEventHandler)
            canvas?.addEventListener("touchcancel", touchEventHandler)
            canvas?.addEventListener("touchmove", touchEventHandler)

            glyph.bind(settings.glyphCanvasId, settings.iconSetting)
            var gl = renderingContext
            if (gl != null) {             
                setup(gl)
                environment.syncWithColorScheme()
                drawScene(gl)
            }
        } 
    }


    /**
     * disconnect creaged grid from node.
     */
    fun unbind() {
        this.environment = null
        this.iconSetting?.removeListener(this.onMineIconChanged!!)
        this.onMineIconChanged = null
        this.iconSetting = null
        val canvasNode = jQuery(this.canvasId!!)
        val canvas = canvasNode[0] as HTMLCanvasElement

        val touchEventHandler = this.onTouchHandler as ((Event)->Unit)

        canvas.removeEventListener("touchstart", touchEventHandler)
        canvas.removeEventListener("touchend", touchEventHandler)
        canvas.removeEventListener("touchcancel", touchEventHandler)
        canvas.removeEventListener("touchmove", touchEventHandler)

        canvas.removeEventListener("click", onClickHandler) 
        window.removeEventListener("resize", onResizedHdlr!!)
        onResizedHdlr = null
        this.onTouchHandler = null
        this.onClickHandler = null
        this.appSettings = null
    }

    /**
     * response icon setting changed event.
     */
    @Suppress("UNUSED_PARAMETER")
    fun handleIconChanged(sender: Any?, msg: String) {
        
        when (msg) { 
            IconSetting.NG_ICON,
            IconSetting.OK_ICON,
            IconSetting.FLAG_ICON -> 
                syncIconImageWithSettings()
        }
    }
    /**
     * synchronize icon image with settings.
     */
    fun syncIconImageWithSettings() {
        val iconSetting = this.iconSetting
        if (iconSetting != null) {
            glyph.updateSpecialImage(iconSetting)
            var gl = renderingContext
            if (gl != null) {
                textures.updateOkTexture(gl, glyph)
                textures.updateNgTexture(gl, glyph) 
                textures.updateNumberFlagTexture(gl, glyph)
                postDrawScene(gl)
            }
        }
    }
    
    /**
     * synchronize this with color scheme lately
     */
    fun postSyncWithColorScheme() {
        window.setTimeout({
            syncWithColorScheme() 
        })
    }

    /**
     * synchronize this with color scheme
     */
    fun syncWithColorScheme() {
        buttons.updateColorScheme(colorScheme)
        glyph.updateColorScheme(colorScheme)
        board.updateColorScheme(colorScheme)
        updateColorScheme(colorScheme) 

        val iconSetting = this.iconSetting
        if (iconSetting != null) {
            glyph.updateImages(iconSetting)
            val gl = renderingContext
            if (gl != null) {
                updateColorBuffer(gl)
                textures.updateNumberBlankTexture(gl, glyph)
                textures.updateOkTexture(gl, glyph)
                textures.updateNgTexture(gl, glyph) 
                textures.updatePointLightMarkerTexture(gl, glyph)
                postDrawScene(gl)
            }
        }
    }

    /**
     * update color scheme
     */
    fun updateColorScheme(colorScheme: ColorScheme) {
        environment?.colorScheme = colorScheme
        val background = colorScheme.getEnvironment(ColorScheme.Background)
        if (background != null) {
            for (i in 0 until min(backColorForDrawing.size, background.size)) {
                backColorForDrawing[i] = background[i]
            }
        }
    }

    /**
     * setup game over modal
     */
    private fun setupGameOverModal() {
        onHiddenGameOverModalHandler = { 
            event, args -> 
            this.handleHiddenGameOverModal(event, args)
        }
        jQuery(gameOverModalId!!).on("hidden.bs.modal", 
            onHiddenGameOverModalHandler!!)
        jQuery(".btn-primary", gameOverModalId!!).on("click",
            onClickToPlayAgainHandler!!)
    }
    /**
     * setup player won the game modal
     */
    private fun setupPlayerWonModal() {
        onHiddenPlayerWonModalHandler = { 
            event, args -> 
            this.handleHiddenPlayerWonModal(event, args)
        }
        jQuery(playerWonModalId!!).on("hidden.bs.modal", 
            onHiddenPlayerWonModalHandler!!)
        jQuery(".btn-primary", playerWonModalId!!).on("click",
            onClickToPlayAgainHandler!!)
    }

    private fun postStartAnimation(gl: WebGLRenderingContext,
        animationFinishedListener: (()->Unit)) {
        window.setTimeout({ 
            startAnimation(gl, animationFinishedListener) 
        }, 100)
    }
    private fun startAnimation(gl: WebGLRenderingContext,
        animationFinishedListener: (()->Unit)) {
        @Suppress("UNUSED_PARAMETER")
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
    /**
     * setup buffer
     */        
    private fun setupBuffer(gl: WebGLRenderingContext) {
        renderingCtx.buttonBuffer = createButtonBuffer(gl)
        renderingCtx.buttonNormalVecBuffer = createButtonNormalVecBuffer(gl)
        renderingCtx.buttonTextureCoordinatesBuffer =
            createButtonTextureCoordinateBuffer(gl)
        
        renderingCtx.boardBuffer = createBoardBuffer(gl)
        renderingCtx.boardNormalVecBuffer = createBoardNormalVecBuffer(gl)
        renderingCtx.boardTextureCoordinateBuffer =
            createBoardTextureCoordinateBuffer(gl)
        setupColorBuffer(gl)

    }

    /**
     * setup color buffer
     */
    private fun setupColorBuffer(gl: WebGLRenderingContext) {
        renderingCtx.boardColorBuffer = createBoardColorBuffer(gl)
        renderingCtx.buttonColorBuffer = createButtonColorBuffer(gl)
    }

    /**
     * tear down board color buffer
     */
    private fun teardownColorBuffer(gl: WebGLRenderingContext) {
        renderingCtx.teardownColorBuffers(gl)
    }

    /**
     * update color buffer
     */
    private fun updateColorBuffer(gl: WebGLRenderingContext) {
        teardownColorBuffer(gl)
        setupColorBuffer(gl)
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
        renderingCtx.spinVMotionMatricesIndex = createSpinAndVMotionMatrices()
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
     * board texture coordinate buffer
     */
    private fun createBoardTextureCoordinateBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, result)
        gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
            board.textureCoordinatesAsFloat32,
            WebGLRenderingContext.STATIC_DRAW)
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
            syncPickingBufferSizeWithSceneBufferI(gl)

            gl.framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
                WebGLRenderingContext.COLOR_ATTACHMENT0, 
                WebGLRenderingContext.RENDERBUFFER, result)
 
            gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
                savedBuffer) 
        }  
        return result
    }

    /**
     * synchronize picking buffer size with main scene buffer
     */  
    fun syncPickingBufferSizeWithSceneBuffer(
        gl: WebGLRenderingContext) {
        val renderBuffer = renderingCtx.pickingBuffer
        if (renderBuffer != null) {
            syncPickingBufferSizeWithSceneBuffer(gl, renderBuffer) 
        }
    }
 
    /**
     * synchronize picking buffer size with main scene buffer
     */  
    fun syncPickingBufferSizeWithSceneBuffer(
        gl: WebGLRenderingContext,
        renderBuffer: WebGLRenderbuffer) {
        val savedBuffer = gl.getParameter(
            WebGLRenderingContext.RENDERBUFFER_BINDING) 
                as WebGLRenderbuffer?
        gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
            renderBuffer)
        syncPickingBufferSizeWithSceneBufferI(gl)

        gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
            savedBuffer) 
    }
         
    /**
     * synchronize picking buffer size with main scene buffer
     */  
    private fun syncPickingBufferSizeWithSceneBufferI(
        gl: WebGLRenderingContext) {

        gl.renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
            WebGLRenderingContext.RGB5_A1,
            gl.canvas.width, gl.canvas.height)
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
                gl.canvas.width, gl.canvas.height)
   
            gl.framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
                WebGLRenderingContext.DEPTH_ATTACHMENT, 
                WebGLRenderingContext.RENDERBUFFER, result)
        }  
        return result
    }
    /**
     * synchronize the size of depth buffer for working  with scene buffer
     */
    private fun syncWorkingDepthBufferSizeWithSceneBuffer(
        gl: WebGLRenderingContext) {
        val renderBuffer = renderingCtx.depthBufferForWorking
        if (renderBuffer != null) {
            syncWorkingDepthBufferSizeWithSceneBuffer(
                gl, renderBuffer)
        }

    }
 
    /**
     * synchronize the size of depth buffer for working  with scene buffer
     */
    private fun syncWorkingDepthBufferSizeWithSceneBuffer(
        gl: WebGLRenderingContext,
        renderBuffer: WebGLRenderbuffer) {
        val savedBuffer = gl.getParameter(
            WebGLRenderingContext.RENDERBUFFER_BINDING) 
                as WebGLRenderbuffer?
        gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
            renderBuffer)
        syncWorkingDepthBufferSizeWithSceneBufferI(gl)
        gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
            savedBuffer) 
          
    }
    /**
     * synchronize the size of depth buffer for working  with scene buffer
     */
    private fun syncWorkingDepthBufferSizeWithSceneBufferI(
        gl: WebGLRenderingContext) {
        gl.renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
            WebGLRenderingContext.DEPTH_COMPONENT16,
            gl.canvas.width, gl.canvas.height)
    }

    /**
     * update view
     */ 
    private fun updateView(gl: WebGLRenderingContext) {
        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null) {
            val savedProgram = gl.getParameter(
                WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?;
 
            gl.useProgram(shaderProg)
            enableLightingAttrib(gl)
            enableCameraAttrib(gl) 
            updateCamera(gl)
            enableLightingContext(gl, lightingContextEnabled)
            enableShadowDepth(gl, shadowDepthEnabled)
            shadowMap.setupShadowSettingForDrawing(this, gl)
            display.drawScene(gl)
            gl.useProgram(savedProgram)

        }
        if (isEditingPointLight) {
            val pointShaderProg = this.renderingCtx.pointShaderProgram
            if (pointShaderProg != null) {
                val savedProgram = gl.getParameter(
                    WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
 
                gl.useProgram(pointShaderProg)
                attachCameraToProjectionMatrix(gl)
                pointLightEdit.attachModelMatrix(gl, renderingCtx) 
                pointLightEdit.drawScene(gl, renderingCtx,
                    glrs!!, glyph, textures)
                gl.useProgram(savedProgram)
            }
        }
    }
    
   
    /**
     * update camera
     */
    private fun updateCamera(gl: WebGLRenderingContext) {
        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null) {
            val savedProgram = gl.getParameter(
                WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
            gl.useProgram(shaderProg)
            attachCameraToProjectionMatrix(gl)
            gl.useProgram(savedProgram)
        }
    }

    /**
     * attach camera matrix into projection matrix in shader program
     */
    private fun attachCameraToProjectionMatrix(
        gl: WebGLRenderingContext) {
        val camMatrix = createCameraMatrix();
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val uProjMat = gl.getUniformLocation(shaderProg, 
                "uProjectionMatrix")    
            if (camMatrix != null) { 
                gl.uniformMatrix4fv(uProjMat, false,
                    camMatrix)
            }
        }
    }

    fun createCameraMatrix(): Float32Array? {
        val cam = camera
        val glrs = this.glrs
        var result: Float32Array? = null
        if (cam != null && glrs != null) {
            val camCenter = cam.center
            val camEye = cam.eye
            val camUp = cam.up

            val projMat = glrs.matrix_new_perspective(
                cam.fieldOfView, cam.aspect, cam.zNear, cam.zFar)

            glrs.matrix_look_at_mut(projMat,
                camEye[0], camEye[1], camEye[2],
                camCenter[0], camCenter[1], camCenter[2],
                camUp[0], camUp[1], camUp[2])

            result = glrs.matrix_get_components_col_order_32(projMat)

            glrs.matrix_release(projMat)
        }
        return result
    }

    /**
     * enable lighting related Attribute
     */
    private fun enableLightingAttrib(gl: WebGLRenderingContext) {
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
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
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val enableLoc = gl.getUniformLocation(shaderProg,
                "uEnableLighting")
            fun Boolean.toInt() = if (this) 1 else 0 
            gl.uniform1i(enableLoc as WebGLUniformLocation, 
                enabled.toInt());
        }
    } 

    /**
     * enable shadow depth rendering
     */
    private fun enableShadowDepth(
        gl: WebGLRenderingContext,
        enable: Boolean) {
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val enableLoc = gl.getUniformLocation(shaderProg,
                "uEnableShadowDepth")
            fun Boolean.toInt() = if (this) 1 else 0 
            gl.uniform1i(enableLoc as WebGLUniformLocation, 
                enable.toInt());
        }
     }

    /**
     * enable camera related Attribute
     */
    private fun enableCameraAttrib(gl: WebGLRenderingContext) {
        val cam = this.camera
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
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
    private fun createVertexShaders(
        gl: WebGLRenderingContext): Array<WebGLShader?> {
        val shaderProg = this.shaderPrograms!!
        val strProg = arrayOf(
            shaderProg.vertexShader,
            shaderProg.pointVertexShader,
            shaderProg.depthVertexShader) 
        val result = Array<WebGLShader?>(strProg.size) {
            createVertexShader(gl, strProg[it])
        }
        return result 
    }
    private fun createFragmentShaders(
        gl: WebGLRenderingContext): Array<WebGLShader?> {
        val shaderProg = this.shaderPrograms!!
        val strProg = arrayOf(
            shaderProg.fragmentShader,
            shaderProg.pointFragmentShader,
            shaderProg.depthFragmentShader) 
        val result = Array<WebGLShader?>(strProg.size) {
            createFragmentShader(gl, strProg[it])
        }
        return result 
    }
    /**
     * compile vertex shader program
     */
    private fun createVertexShader( 
        gl: WebGLRenderingContext,
        shaderProgram: String): WebGLShader? {
        var result : WebGLShader? = null
        val shader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER)
        if (shader != null) {
            gl.shaderSource(shader, shaderProgram)
            gl.compileShader(shader)
            result = assertCompileError(gl, shader)
        }
        return result
    } 
    /**
     * compile fragment shader program
     */
    private fun createFragmentShader(
        gl: WebGLRenderingContext,
        shaderProgram: String): WebGLShader? {
        var result : WebGLShader? = null
        val shader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER)
        if (shader != null) {
            gl.shaderSource(shader, shaderProgram)
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
        } else {
            println(gl.getShaderInfoLog(shader))
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
    fun createSpinAndVMotionMatrices(): Pair<Array<FloatArray>, IntArray>? {
        
        val t = 1f
        val rotationCount = 0.5f
        val axis = floatArrayOf(1f, 0f, 0f)
        var result : Pair<Array<FloatArray>, IntArray>?

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
        var result : Array<FloatArray>?

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
    @Suppress("UNUSED_PARAMETER")
    private fun onCameraChanged(eventName : String, camera : Camera) {
    }

    /**
     * synchronize canvas size with client size.
     */
    private fun syncCanvasWithClientSize(resized: (()->Unit)?) {
        val nodeId = this.canvasId
        if (nodeId != null) {
            val canvas = document.querySelector(
                canvasId!!) as HTMLCanvasElement
            syncCanvasWithClientSize(canvas, resized)
        }  
    }

    /**
     * synchronize canvas size with client size.
     */
    private fun syncCanvasWithClientSize(
        canvas: HTMLCanvasElement,
        resized: (()->Unit)?) {

        var doResize: Boolean
        doResize = canvas.width != canvas.clientWidth
        if (!doResize) {
            doResize = canvas.height != canvas.clientHeight
        }
        if (doResize) {
            canvas.width = canvas.clientWidth
            canvas.height = canvas.clientHeight
            if (resized != null) {
                resized()
            }
        }  
    }

    /**
     * sync gl viewport with canvas size
     */
    private fun syncViewportWithCanvasSize() {

        var gl = renderingContext
        if (gl != null) {
            syncViewportWithCanvasSize(gl)
        }
    }
    /**
     * sync gl viewport with canvas size
     */
    private fun syncViewportWithCanvasSize(
        gl: WebGLRenderingContext) {

        val curPort = gl.getParameter(
            WebGLRenderingContext.VIEWPORT) as Int32Array
        
        val portNew = this.viewport!!
        if (portNew[0] != curPort[0] || portNew[1] != curPort[1]
            || portNew[2] != curPort[2] || portNew[3] != curPort[3]) {
            gl.viewport(portNew[0], portNew[1], portNew[2], portNew[3])
        } 
    }


    /**
     * handle the event for resizing window
     */
    @Suppress("UNUSED_PARAMETER")
    private fun onResized(event: Event) {
        syncCanvasWithClientSize {
            val gl = renderingContext
            if (gl != null) { 
                syncViewportWithCanvasSize(gl)
                setupCamera(gl)
                syncPickingBufferSizeWithSceneBuffer(gl)
                syncWorkingDepthBufferSizeWithSceneBuffer(gl)
                syncShadwoDepthWithSceneBuffer(gl)
                syncPointLightEditWithSceneBuffer()
                postDrawScene(gl)
            }
        }

    }

    /**
     * synchronize shadow depth buffer with scene buffer
     */
    private fun syncShadwoDepthWithSceneBuffer(
        gl: WebGLRenderingContext) {
        shadowMap.syncShadwoDepthAndTextureWithSceneSize(gl, renderingCtx)
    }

    /**
     * sychronize point edit light with scene buffer
     */
    private fun syncPointLightEditWithSceneBuffer() {
        pointLightEdit.updateWithSceneBuffer(this)
    }
    /**
     * display game over modal
     */
    private fun postDisplayGameOverModal() {
        window.setTimeout({
            displayGameOverModal()
        }, 100) 
    }
    /**
     * display player won modal
     */
    private fun postDisplayPlayerWonModal() {
        window.setTimeout({
            displayPlayerWonModal()
        }, 100) 
    }


    /**
     * display player won modal
     */
    private fun displayPlayerWonModal() {
        jQuery(playerWonModalId!!).asDynamic().modal()
    }

    /**
     * display won modal
     */
    private fun displayGameOverModal() {
        jQuery(gameOverModalId!!).asDynamic().modal()
    }



    /**
     * modal hidden event handler
     */
    @Suppress("UNUSED_PARAMETER")
    private fun handleHiddenGameOverModal(e : JQueryEventObject,
        args: Any) : Any {
        if (nextGameOperation != null) {
            nextGameOperation!!()
        }
        return true 
    }
    /**
     * modal hidden event handler
     */
    @Suppress("UNUSED_PARAMETER")
    private fun handleHiddenPlayerWonModal(e : JQueryEventObject,
        args: Any) : Any {
        if (nextGameOperation != null) {
            nextGameOperation!!()
        }
        return true 
    }

    /**
     * play again button handler
     */
    @Suppress("UNUSED_PARAMETER")
    private fun handleClickToPlayAgain(e : JQueryEventObject, 
        args: Any): Any {
        this.nextGameOperation = {
            postResetGame() 
            this.nextGameOperation = null 
        } 
        return true 
    }
  
    /**
     * reset game lately
     */
    private fun postResetGame() {
        window.setTimeout({
            resetGame()
        }, 100)
    }
    /**
     * reset game
     */
    private fun resetGame() {
        val model = this.model!!
        val status = model.logic.status 
        if (status != null) {
            status.clearOpenedButtons()
            val buttonIndices = Array<IntArray>(0) { intArrayOf(0,0) }
            Animation.setupButtons(buttons, 
                status.getOpenedIndices(),
                buttonIndices, renderingCtx)
            while (Animation.hasNextFrame(renderingCtx)) {
                Animation.doAnimate(renderingCtx)
            }  
            drawScene()             
        }
    }

    /**
     * edit light settings
     */
    private fun onEditLightSetting() {
        val pointLightSetting = this.pointLightSetting
        pointLightSetting.show {
            postDrawScene()
        }
        postDrawScene()
    }
}
// vi: se ts=4 sw=4 et:
