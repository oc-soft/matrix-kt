package net.ocsoft.mswp.ui

import org.khronos.webgl.*

class RenderingCtx() {
    /**
     * shader program
     */
    var shaderProgram: WebGLProgram? = null
    /**
     * vertex buffer for button 
     */
    var buttonBuffer : WebGLBuffer? = null

    /**
     * vertex normal vector for buttons 
     */
    var buttonNormalVecBuffer : WebGLBuffer? = null

    /**
     * color buffer for button
     */
    var buttonColorBuffer : WebGLBuffer? = null

    /**
     * button texture coordinates buffer
     */
    var buttonTextureCoordinatesBuffer : WebGLBuffer? = null
     

    /**
     * color buffer for picking
     */
    var buttonPickingColorBuffer : WebGLBuffer? = null

   

    /**
     * texture for button 
     */
    var buttonTexture : WebGLTexture? = null
    
    /**
     * vertex buffer for board
     */
    var boardBuffer : WebGLBuffer? = null

    /**
     * board color buffer for display 
     */
    var boardColorBuffer : WebGLBuffer? = null

    /**
     * board color buffer for picking
     */
    var boardPickingColorBuffer : WebGLBuffer? = null

    /**
     * vertex normal vector buffer for board
     */
    var boardNormalVecBuffer : WebGLBuffer? = null
    
    /**
     * texture for board
     */
    var boardTexture : WebGLTexture? = null

    /**
     * board texture coordinate buffer
     */
    var boardTextureCoordinateBuffer : WebGLBuffer? = null
    
    /**
     * buffer for offscreen
     */
    var workableFramebuffer : WebGLFramebuffer? = null
    
    /**
     * depth buffer for working
     */
    var depthBufferForWorking : WebGLRenderbuffer? = null

    /**
     * buffer for picking some objects
     */
    var pickingBuffer: WebGLRenderbuffer? = null
    /**
     * buttons location
     */
    var buttonMatrices : Array<FloatArray>? = null

    /**
     * matrix for button's normal vector.
     */
    var buttonNormalVecMatrices : Array<FloatArray>? = null

    /**
     * button matrices for displaying
     */
    var buttonMatricesForDrawing : Array<FloatArray>? = null 

    /**
     * matrix for button's normal vector.
     */
    var buttonNormalVecMatricesForDrawing : Array<FloatArray>? = null

    /**
     * spin and vertical motion matrices
     */
    var spinAndVMotionMatrices : Array<FloatArray>? = null

    /**
     * spin motion matrices
     */
    var spinMotionMatrices: Array<FloatArray>? = null
    /**
     * matrices for animation
     */
    var animationMatrices: MutableList<Array<FloatArray>>? = null

    /**
     *
     */
    var animationNormalMatrices: MutableList<Array<FloatArray>>? = null

    /**
     * board location
     */
    var boardMatrix : FloatArray? = null

    /**
     * board normal vector matrix
     */
    var boardNormalVecMatrix : FloatArray? = null
    /**
     * main scene render buffer
     */
    var sceneBuffer: WebGLRenderbuffer? = null

     
    /**
     * create clone buttons matrices 
     */
    fun cloneButtonMatrices() : Array<FloatArray>? {
        var result : Array<FloatArray>? = null
        val buttonMatrices = this.buttonMatrices
        if (buttonMatrices != null) {
            result = Array<FloatArray>(buttonMatrices.size) {
                i ->
                buttonMatrices[i].copyOf()
            }
        }
        return result
    }

    /**
     * get scene render buffer size
     */
    fun getSceneBufferSize(gl: WebGLRenderingContext) : IntArray {
        val savedBuffer = gl.getParameter(
            WebGLRenderingContext.RENDERBUFFER_BINDING) as
                WebGLRenderbuffer?
        gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
            sceneBuffer)
        
        val width = gl.getRenderbufferParameter(
            WebGLRenderingContext.RENDERBUFFER,
            WebGLRenderingContext.RENDERBUFFER_WIDTH) as Int
        val height = gl.getRenderbufferParameter(
            WebGLRenderingContext.RENDERBUFFER,
            WebGLRenderingContext.RENDERBUFFER_HEIGHT) as Int
        gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
            savedBuffer) 
        return intArrayOf(width, height)
    }
    
    fun tearDown(gl : WebGLRenderingContext) {
        teardownBuffer(gl)
        teardownRenderBuffer(gl)
        teardownShaderProgram(gl) 
        teardownFramebuffer(gl)
        teardownTextures(gl)
        teardonwMatrix()
    }
    /**
     * free shader program
     */
    fun teardownShaderProgram(gl: WebGLRenderingContext) {
        val shaderProgram = this.shaderProgram
        if (shaderProgram != null) {
            val shaders = gl.getAttachedShaders(shaderProgram)
            if (shaders != null) {
                shaders.forEach({
                    shader->
                    gl.detachShader(shaderProgram, shader)
                    gl.deleteShader(shader)
                })
            }
            gl.deleteProgram(shaderProgram)
            this.shaderProgram = null
        } 
    }
    /**
     * free buffer
     */
    private fun teardownBuffer(gl: WebGLRenderingContext) {
        arrayOf(buttonBuffer, 
            buttonNormalVecBuffer,
            buttonColorBuffer,
            buttonPickingColorBuffer,
            buttonTextureCoordinatesBuffer,
            boardBuffer,
            boardNormalVecBuffer,
            boardColorBuffer,
            boardTextureCoordinateBuffer,
            boardPickingColorBuffer).forEach({ buffer ->
            if (buffer != null) {
                gl.deleteBuffer(buffer)
            }
        })
        buttonBuffer = null
        buttonNormalVecBuffer = null
        buttonColorBuffer = null
        buttonTextureCoordinatesBuffer = null
        buttonPickingColorBuffer = null
        boardBuffer = null
        boardNormalVecBuffer = null
        boardColorBuffer = null
        boardPickingColorBuffer = null
        boardTextureCoordinateBuffer = null
    }
    fun teardownRenderBuffer(gl: WebGLRenderingContext) {
        arrayOf(pickingBuffer,
            depthBufferForWorking).forEach({ buffer ->
            if (buffer != null) {
                gl.deleteRenderbuffer(buffer)
            }
        })
        depthBufferForWorking = null
        pickingBuffer = null
    }
    fun teardownFramebuffer(gl: WebGLRenderingContext) {
        arrayOf(workableFramebuffer).forEach({ buffer ->
            if (buffer != null) {
                gl.deleteFramebuffer(buffer)
            }
        })
        workableFramebuffer = null
    }
    /**
     * destroy all textures
     */
    fun teardownTextures(gl: WebGLRenderingContext) {
        arrayOf(buttonTexture).forEach({ gl.deleteTexture(it) })
        this.buttonTexture = null
    }
    fun teardonwMatrix() {
        this.buttonMatrices = null
        this.boardMatrix = null
    }


}

// vi: se ts=4 sw=4 et:
