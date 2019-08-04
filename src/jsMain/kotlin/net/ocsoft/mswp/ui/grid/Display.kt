
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
                renderingCtx.buttonNormalVecBuffer)
            gl.vertexAttribPointer(
                normalVecLoc, 3,
                WebGLRenderingContext.FLOAT,
                false, 0, 0)
            gl.enableVertexAttribArray(normalVecLoc)


            for (rowIndex in 0 until rowCount) {
                for (colIndex in 0 until columnCount) { 
                    buttonColorBufferBind(gl) 
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
            
        }
    }
    
}


