
package net.ocsoft.mswp.ui.grid

import net.ocsoft.mswp.ui.*
import org.khronos.webgl.*

/**
 * has responsibility to render draw some visual objects. 
 */
class Display(var renderingCtx : RenderingCtx,
    var buttons : Buttons,
    var board : Board)  {
    
    /**
     * row count
     */
    val rowCount : Int
        get() {
            return buttons.rowCount
        }
    /**
     * column count
     */
    val columnCount: Int
        get() {
            return buttons.columnCount
        }
    /**
     * bind buffer for button color for display
     */
    val buttonColorForDisplayBind : (WebGLRenderingContext) -> Unit
        = { gl -> 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,  
                renderingCtx.buttonColorBuffer)

        }

    /**
     * bind buffer for button color for picking 
     */
    val buttonColorForPickingBind : (WebGLRenderingContext) -> Unit
        = { gl -> 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,  
                renderingCtx.buttonPickingColorBuffer)
        }
    /**
     * bind buffer for button texture for displaying
     */ 
    val buttonTextureBindForDisplay : (WebGLRenderingContext, Int, Int) -> Unit
        = { gl, rowIndex, colIndex -> 
            val numTex = buttons.getNumberImage(rowIndex, colIndex)
            var tex : WebGLTexture?
            if (numTex != null) {
                tex = numTex
            } else {
                tex = buttons.transparentTexture
            }
            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, tex)
            val shaderProg = this.renderingCtx.shaderProgram
            if (shaderProg != null) {
                val enableTexLoc = gl.getUniformLocation(shaderProg,
                    "uEnableTexture")
                fun Boolean.toInt() = if (this) 1 else 0 
                gl.uniform1i(enableTexLoc as WebGLUniformLocation, 
                    (numTex != null).toInt());

            }
        }

    /**
     * bind buffer for button texture for picking
     */ 
    val buttonTextureBindForPicking : (WebGLRenderingContext, Int, Int) -> Unit
        = { gl, rowIndex, colIndex -> 

            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                buttons.transparentTexture)
 
            val shaderProg = this.renderingCtx.shaderProgram
            if (shaderProg != null) {
                val enableTexLoc = gl.getUniformLocation(shaderProg,
                    "uEnableTexture")
                gl.uniform1i(enableTexLoc as WebGLUniformLocation, 0);
 
            }
        }



    /**
     * bind buffer for board color for display
     */
    val boardColorForDisplayBind : (WebGLRenderingContext) -> Unit
        = { gl -> 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,  
                renderingCtx.boardColorBuffer)
        }

    /**
     * bind buffer for board color for picking 
     */
    val boardColorForPickingBind : (WebGLRenderingContext) -> Unit
        = { gl -> 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,  
                renderingCtx.boardPickingColorBuffer)
        }


     /**
     * set up color data for display
     */
    val buttonColorDataForDisplay : (WebGLRenderingContext, Int, Int) -> Unit
        = { gl, rowIndex, columnIndex ->
        }

    /**
     * setup color buffer for picking
     */
    val buttonColorDataForPicking : (WebGLRenderingContext, Int, Int) -> Unit
        = { 
            gl, rowIndex, columnIndex ->
            val color = buttons.getPickingColor(rowIndex, columnIndex) 
            val countOfColors = buttons.mineButton.vertices.size / 3
            val colors = Array<Float>(countOfColors * color.size) { 
                i -> color[i % color.size]
            } 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,  
                renderingCtx.buttonPickingColorBuffer)
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                Float32Array(colors), 
                WebGLRenderingContext.STATIC_DRAW) 
        }
    /**
     * bind color for button
     */
    var buttonColorBufferBind : (WebGLRenderingContext) -> Unit
        = buttonColorForDisplayBind 
    /**
     * bind texture for button
     */
    var buttonTextureBind : (WebGLRenderingContext, Int, Int) -> Unit
        = buttonTextureBindForDisplay 


    /**
     *  set up color for drawing
     */
    var buttonColorDataForDraw : (WebGLRenderingContext, Int, Int) -> Unit
        = buttonColorDataForDisplay 

    /**
     * setup color buffer binding for board
     */
    var boardColorBufferBind : (WebGLRenderingContext) -> Unit
        = boardColorForDisplayBind 

    /**
     * draw scene
     */
    fun drawScene(gl: WebGLRenderingContext) {
       updateView(gl)
    }
    /**
     * update view
     */
    fun updateView(gl: WebGLRenderingContext) {
        updateButtons(gl)
        updateBoard(gl)
    }

    /**
     * update buttons rendered image
     */
    private fun updateButtons(gl: WebGLRenderingContext) {
        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null) {

            val verLoc = gl.getAttribLocation(shaderProg, 
                "aVertexPosition")
            val verColor = gl.getAttribLocation(shaderProg,
                "aVertexColor")
            val normalVecLoc = gl.getAttribLocation(shaderProg,
                "aNormalVector")
            val texLoc = gl.getAttribLocation(shaderProg,
                "aTextureCoord")
            val texSampler = gl.getUniformLocation(shaderProg,
                "uSampler")


            val savedArrayBuffer = gl.getParameter(
                WebGLRenderingContext.ARRAY_BUFFER_BINDING)

            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                renderingCtx.buttonBuffer)
            gl.vertexAttribPointer(
                verLoc,
                3,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            gl.enableVertexAttribArray(verLoc)

            buttonColorBufferBind(gl) 
            gl.vertexAttribPointer( 
                verColor,
                4,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            gl.enableVertexAttribArray(verColor)

            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                renderingCtx.buttonTextureCoordinatesBuffer)
            gl.vertexAttribPointer(
                texLoc, 2,
                WebGLRenderingContext.FLOAT,
                false, 0, 0)
            gl.enableVertexAttribArray(texLoc)
 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                renderingCtx.buttonNormalVecBuffer)
            gl.vertexAttribPointer(
                normalVecLoc, 3,
                WebGLRenderingContext.FLOAT,
                false, 0, 0)
            gl.enableVertexAttribArray(normalVecLoc)

            val savedTex = gl.getParameter(
                WebGLRenderingContext.TEXTURE_BINDING_2D)
            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                renderingCtx.buttonTexture)
            
            val savedTexNum = gl.getParameter(
                WebGLRenderingContext.ACTIVE_TEXTURE)
            gl.activeTexture(
                buttons.mineButton.textureIndex0)
            var txtNumber = buttons.mineButton.textureIndex0
            txtNumber -= WebGLRenderingContext.TEXTURE0
            gl.uniform1i(texSampler, txtNumber)
  
            for (rowIndex in 0 until rowCount) {
                for (colIndex in 0 until columnCount) { 
                    buttonTextureBind(gl, rowIndex, colIndex)
                    buttonColorDataForDraw(gl, rowIndex, colIndex)
                     
                    updateButtonViewMatrix(gl, rowIndex, colIndex)
                    gl.bindBuffer(
                        WebGLRenderingContext.ARRAY_BUFFER,
                        renderingCtx.buttonBuffer)

                    gl.drawArrays(
                        buttons.mineButton.drawingMode, 0,
                        buttons.mineButton.vertices.size / 3) 
                }
            }
            gl.activeTexture(savedTexNum as Int)

            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                savedTex as WebGLTexture?)
 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                savedArrayBuffer as WebGLBuffer?)
         }
    }

    /**
     * update button view matrix
     */
    private fun updateButtonViewMatrix(
        gl: WebGLRenderingContext,
        rowIndex : Int,
        columnIndex : Int) {

        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null) {
            val uModelMat = gl.getUniformLocation(shaderProg,
                "uModelViewMatrix")
            val uNormalVecMat = gl.getUniformLocation(shaderProg,
                "uNormalVecMatrix")
            renderingCtx.buttonMatrices!![
                rowIndex * columnCount + columnIndex]
            val mat = renderingCtx.buttonMatricesForDrawing!![
                rowIndex * columnCount + columnIndex]

            gl.uniformMatrix4fv(uModelMat, false, 
                Float32Array(Array<Float>(mat.size) { i -> mat[i] }))
        
            val normalVecMat = renderingCtx.buttonNormalVecMatrices!![
                rowIndex * columnCount + columnIndex]

            gl.uniformMatrix4fv(uNormalVecMat, false,
                Float32Array(Array<Float>(normalVecMat.size) {
                    normalVecMat[it] 
                }))
        }
    }
    /**
     * update board rendered image.
     */
    private fun updateBoard(gl: WebGLRenderingContext) {
        val shaderProg = this.renderingCtx.shaderProgram
        if (shaderProg != null) {
            val uModelMat = gl.getUniformLocation(shaderProg,
                "uModelViewMatrix")
            val uNormalVecMat = gl.getUniformLocation(shaderProg,
                "uNormalVecMatrix")
            val texLoc = gl.getAttribLocation(shaderProg,
                "aTextureCoord")
            val verLoc = gl.getAttribLocation(shaderProg, 
                "aVertexPosition")
            val verColor = gl.getAttribLocation(shaderProg,
                "aVertexColor")
            val enableTexLoc = gl.getUniformLocation(shaderProg,
                "uEnableTexture")
            
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
                renderingCtx.boardTextureCoordinateBuffer)
            gl.vertexAttribPointer(
                texLoc, 2,
                WebGLRenderingContext.FLOAT,
                false, 0, 0)

            val savedTexNum = gl.getParameter(
                WebGLRenderingContext.ACTIVE_TEXTURE)
            gl.activeTexture(
                WebGLRenderingContext.TEXTURE0)
 
            val savedTex = gl.getParameter(
                WebGLRenderingContext.TEXTURE_BINDING_2D)
            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                board.transparentTexture)
 
            boardColorBufferBind(gl)
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

            gl.uniform1i(enableTexLoc as WebGLUniformLocation, 
                0);
 
            gl.bindBuffer(
                WebGLRenderingContext.ARRAY_BUFFER,
                renderingCtx.boardBuffer)
            val mat =  renderingCtx.boardMatrix!!
            gl.uniformMatrix4fv(uModelMat, false, 
                Float32Array(Array<Float>(mat.size) { i -> mat[i] }))
            val normalVecMat = renderingCtx.boardNormalVecMatrix!!
            gl.uniformMatrix4fv(uNormalVecMat, false,
                Float32Array(Array<Float>(normalVecMat.size) {
                    normalVecMat[it] 
                }))
            gl.drawArrays(
                board.drawingMode, 
                0, 
                board.vertices.size / 3) 

            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                savedTex as WebGLTexture?)
            gl.activeTexture(
                savedTexNum as Int)
             
        }
    }
}


