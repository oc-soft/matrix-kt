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
     * texture for button 
     */
    var buttonTexture : WebGLTexture? = null
    
    /**
     * vertex buffer for board
     */
    var boardBuffer : WebGLBuffer? = null

    /**
     * color buffer for board
     */
    var boardColorBuffer : WebGLBuffer? = null
    /**
     * vertex normal vector buffer for board
     */
    var boardNormalVecBuffer : WebGLBuffer? = null
    
    /**
     * texture for board
     */
    var boardTexture : WebGLTexture? = null

    /**
     * buttons location
     */
    var buttonMatrices : Array<FloatArray>? = null

    /**
     * bload location
     */
    var boardMatrix : FloatArray? = null

    fun tearDown(gl : WebGLRenderingContext) {
        teardownBuffer(gl)
        teardownShaderProgram(gl) 
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
            boardBuffer,
            boardNormalVecBuffer,
            boardColorBuffer).forEach({ buffer ->
            if (buffer != null) {
                gl.deleteBuffer(buffer)
            }
        })
        buttonBuffer = null
        buttonNormalVecBuffer = null
        buttonColorBuffer = null
        boardBuffer = null
        boardNormalVecBuffer = null
        boardColorBuffer = null
    }
    fun teardonwMatrix() {
        this.buttonMatrices = null
        this.boardMatrix = null
    }


}
