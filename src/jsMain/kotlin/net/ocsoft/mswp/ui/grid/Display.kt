
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
            renderingCtx.buttonMatrices!![
                rowIndex * columnCount + columnIndex]
            val mat = renderingCtx.buttonMatrices!![
                rowIndex * columnCount + columnIndex]
            gl.uniformMatrix4fv(uModelMat, false, 
                Float32Array(Array<Float>(mat.size) { i -> mat[i] }))
        
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
}


