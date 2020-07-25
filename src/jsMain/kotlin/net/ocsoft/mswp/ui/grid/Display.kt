
package net.ocsoft.mswp.ui.grid

import kotlin.math.*

import net.ocsoft.mswp.ui.*
import org.khronos.webgl.*
import kotlin.collections.ArrayList

/**
 * has responsibility to render draw some visual objects. 
 */
class Display(var renderingCtx : RenderingCtx,
    var buttons : Buttons,
    var board : Board,
    var pointLight: PointLight)  {

    /**
     * singleton functions
     */
    companion object {

        /**
         * calculate power of 2 value which is higher than input value and near
         * the input value
         */
        fun calcPower2Value(value: Int): Int {
            return 2.0.pow(ceil(log2(value.toDouble()))).toInt()
        }
    }
    
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
                tex = buttons.getAlternateTexture(rowIndex, colIndex)
            }
            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, tex)
            val shaderProg = gl.getParameter(
                WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
            if (shaderProg != null) {
                val enableTexLoc = gl.getUniformLocation(shaderProg,
                    "uEnableTexture")
                fun Boolean.toInt() = if (this) 1 else 0 
                gl.uniform1i(enableTexLoc as WebGLUniformLocation, 
                    (tex != null).toInt());

            }
        }

    /**
     * bind buffer for button texture for picking
     */ 
    val buttonTextureBindForPicking : (WebGLRenderingContext, Int, Int) -> Unit
        = { gl, rowIndex, colIndex -> 

            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                buttons.transparentTexture)
            val shaderProg = gl.getParameter(
                WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?

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
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {

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
            bindButtonVerticesBuffer(gl) 


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
                     
                    drawButtonI(gl, rowIndex, colIndex)

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
     * draw button at a cell which is specified with row and column.
     * this call write over gl array buffer.
     */
    fun drawButtonI(gl: WebGLRenderingContext,
        rowIndex: Int,
        colIndex: Int) {
        updateButtonViewMatrix(gl, rowIndex, colIndex)
        gl.bindBuffer(
            WebGLRenderingContext.ARRAY_BUFFER,
            renderingCtx.buttonBuffer)
        gl.drawArrays(
            buttons.mineButton.drawingMode, 0,
            buttons.mineButton.vertices.size / 3) 
     }

    /**
     * bind button vertices buffer
     */
    fun bindButtonVerticesBuffer(gl: WebGLRenderingContext) {
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val verLoc = gl.getAttribLocation(shaderProg, 
                "aVertexPosition")
            if (verLoc != null) { 
                val savedArrayBuffer = gl.getParameter(
                    WebGLRenderingContext.ARRAY_BUFFER_BINDING) as WebGLBuffer?
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
                    savedArrayBuffer)
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
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?

        if (shaderProg != null) {
            val uModelMat = gl.getUniformLocation(shaderProg,
                "uModelViewMatrix")
            val uNormalVecMat = gl.getUniformLocation(shaderProg,
                "uNormalVecMatrix")

            if (uModelMat != null) {
                val mat = getButtonMatrixForDrawing(rowIndex, columnIndex)
                gl.uniformMatrix4fv(uModelMat, false, 
                    Float32Array(Array<Float>(mat.size) { i -> mat[i] }))
            }
        
            if (uNormalVecMat != null) {
                val normalVecMat = renderingCtx.buttonNormalVecMatrices!![
                    rowIndex * columnCount + columnIndex]

                gl.uniformMatrix4fv(uNormalVecMat, false,
                    Float32Array(Array<Float>(normalVecMat.size) {
                        normalVecMat[it] 
                    }))
            }
        }
    }

    /**
     * get button matrix for drawing
     */

    fun getButtonMatrixForDrawing(
        rowIndex: Int,
        columnIndex: Int): FloatArray {
        return renderingCtx.buttonMatricesForDrawing!![
            rowIndex * columnCount + columnIndex]
    }

    
    /**
     * calculate all of coordinates for game
     */
    fun calcGameBounds(): Array<FloatArray> {
        val vertices = calcButtonsMovingBoundsI()
        val vertexList = ArrayList<FloatArray>()
        vertices.forEach {
            elem0 ->
            elem0.forEach {
                elem1 ->
                elem1.forEach {
                    vertexList.add(it)
                }
            }
        }
        calcBoardCoordinate().forEach {
            elem ->
            vertexList.add(FloatArray(elem.length) { elem[it] })
        }
        return Array<FloatArray>(vertexList.size) { vertexList[it] }
    }

    /**
     * calc buttons vertices coordinate bounds
     */ 
    fun calcButtonsMovingBounds(): Array<FloatArray> {
        val vertices = calcButtonsMovingBoundsI()
        val vertexList = ArrayList<FloatArray>()
        vertices.forEach {
            elem0 ->
            elem0.forEach {
                elem1 ->
                elem1.forEach {
                    vertexList.add(it)
                }
            }
        }
        return Array<FloatArray>(vertexList.size) { vertexList[it] }
    }
 

    /**
     * calc buttons vertices coordinate bounds
     */ 
    fun calcButtonsMovingBoundsI(): Array<Array<Array<FloatArray>>> {
        return calcButtonsMovingBoundsI(renderingCtx.glrs!!)
    }
 
    /**
     * calc buttons vertices coordinate bounds
     */ 
    fun calcButtonsMovingBoundsI(
        glrs: glrs.InitOutput): Array<Array<Array<FloatArray>>>  {
        val boundsIndices = arrayOf(
            intArrayOf(0, 0),
            intArrayOf(0, columnCount - 1),
            intArrayOf(rowCount - 1, columnCount - 1),
            intArrayOf(rowCount - 1, 0))
        val buttonMatrices = renderingCtx.cloneButtonMatrices()!! 
        val buttonVertices = buttons.mineButton.verticesAsFloat32
        val spinVMotionMatricesIndex = renderingCtx.spinVMotionMatricesIndex!!
        val result = Array<Array<Array<FloatArray>>>(boundsIndices.size) {
            val rowCol = boundsIndices[it]
            val locMat = renderingCtx.buttonMatrices!![
                rowCol[0] * columnCount + rowCol[1]]
            val locMatRef = glrs.matrix_create_with_components_col_order(
                Float64Array(Array<Double>(locMat.size) {
                    locMat[it].toDouble()
                })) 
            val res = Array<Array<FloatArray>>(
                spinVMotionMatricesIndex.second.size + 1) {
                idx0 ->
                if (idx0 < spinVMotionMatricesIndex.second.size) {
                    val topMoveMat = spinVMotionMatricesIndex.first[it]
                    val topMoveMatRef =
                        glrs.matrix_create_with_components_col_order(
                            Float64Array(Array<Double>(topMoveMat.size) { 
                                topMoveMat[it].toDouble()
                            }))
                    val boundsMat = glrs.matrix_multiply(
                        locMatRef, topMoveMatRef)
                     
                    val res0 = Array<FloatArray>(buttonVertices.length / 3) {
                        idx1 -> 
                        
                        val transformed = glrs.matrix_apply_r_32(
                            boundsMat,
                            Float32Array(Array<Float>(4) {
                                idx2 ->
                                if (idx2 < 3) {
                                    buttonVertices[3 * idx1 + idx2] 
                                } else {
                                    1f
                                }
                            })) 
                        FloatArray(transformed!!.length) { transformed[it] } 
                    }
                    glrs.matrix_release(boundsMat)
                    glrs.matrix_release(topMoveMatRef)
                    res0
                } else { 
                    Array<FloatArray>(buttonVertices.length / 3) {
                        idx1 ->
                        val transformed = glrs.matrix_apply_r_32(
                            locMatRef,
                            Float32Array(Array<Float>(4) {
                                idx2 ->
                                if (idx2 < 3) {
                                    buttonVertices[3 * idx1 + idx2] 
                                } else {
                                    1f
                                }
                            })) 
                        FloatArray(transformed!!.length) { transformed[it] } 
                    }
                }
            } 
            glrs.matrix_release(locMatRef)
            res
        }
        return result
    }
    

    /**
     * calculate buttons coordinate
     */
    fun calcButtonsCoordinate() :Array<FloatArray> {
        val coordArray = ArrayList<FloatArray>()
        for (rowIndex in 0 until rowCount) {
            for (colIndex in 0 until columnCount) { 
                val coord = calcButtonCoordinate(rowIndex, colIndex)
                coord.forEach{ coordArray.add(it) } 
            }
        }
        val result = Array<FloatArray>(coordArray.size) { coordArray[it] }
        return result
    }
   
    
    /**
     * calculate button coordinate
     */
    fun calcButtonCoordinate(rowIndex :Int, columnIndex: Int):
        Array<FloatArray> {
        val mat = renderingCtx.buttonMatricesForDrawing!![
            rowIndex * columnCount + columnIndex]
        val glrs = renderingCtx.glrs!!
        
        val matRef = glrs.matrix_create_with_components_col_order(
            Float64Array(
                Array<Double>(mat.size) { i -> mat[i].toDouble() })) 
        val vertices = buttons.mineButton.vertices 

        val coordList = ArrayList<FloatArray>()
        for (i in 0 until vertices.size / 3) {
            val vecRef0 = glrs.vector_create(
                Float64Array(arrayOf(vertices[3 * i].toDouble(), 
                    vertices[3 * i + 1].toDouble(),
                    vertices[3 * i + 2].toDouble(), 
                    1.0)))
        
            val vecRef1 = glrs.matrix_apply_r_with_vec(matRef, vecRef0)
            val vec = glrs.vector_get_components_32(vecRef1)
            if (vec != null) {
                coordList.add(FloatArray(vec.length) { vec[it] })
            }
            glrs.vector_release(vecRef1)
            glrs.vector_release(vecRef0)
        }
        glrs.matrix_release(matRef)

        val result = Array<FloatArray>(coordList.size) { coordList[it] }
        return result 
    }
    

    /**
     * update board rendered image.
     */
    private fun updateBoard(gl: WebGLRenderingContext) {
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
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
 
            val normalVecMat = renderingCtx.boardNormalVecMatrix!!
            gl.uniformMatrix4fv(uNormalVecMat, false,
                Float32Array(Array<Float>(normalVecMat.size) {
                    normalVecMat[it] 
                }))
            drawBoardI(gl)

            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                savedTex as WebGLTexture?)
            gl.activeTexture(
                savedTexNum as Int)
             
        }
    }


    /**
     * draw board
     */
    fun drawBoardI(gl: WebGLRenderingContext) {
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val uModelMat = gl.getUniformLocation(shaderProg,
                "uModelViewMatrix")
            gl.bindBuffer(
                WebGLRenderingContext.ARRAY_BUFFER,
                renderingCtx.boardBuffer)
            val mat = renderingCtx.boardMatrix!!
            gl.uniformMatrix4fv(uModelMat, false, 
                Float32Array(Array<Float>(mat.size) { i -> mat[i] }))
            gl.drawArrays(
                board.drawingMode, 
                0, 
                board.vertices.size / 3) 
        }
    }


    /**
     * calculate board coordinate
     */
    fun calcBoardCoordinate(): Array<Float32Array> {
        val mat = renderingCtx.boardMatrix!!
        val glrs = renderingCtx.glrs!!
        
        val matRef = glrs.matrix_create_with_components_col_order(
            Float64Array(Array<Double>(mat.size) { mat[it].toDouble() })) 
 
        val vertices = board.vertices
        val coordList = ArrayList<Float32Array>()
        for (i in 0 until vertices.size / 3) {

            val vecRef0 = glrs.vector_create(
                Float64Array(arrayOf(vertices[3 * i].toDouble(), 
                    vertices[3 * i + 1].toDouble(), 
                    vertices[3 * i + 2].toDouble(), 
                    1.0)))
            
            val vecRef1 = glrs.matrix_apply_r_with_vec(matRef, vecRef0)
            val vec = glrs.vector_get_components_32(vecRef1)
            if (vec != null) {
                coordList.add(vec)
            }
            glrs.vector_release(vecRef1)
            glrs.vector_release(vecRef0)
        }
        glrs.matrix_release(matRef)

        val result = Array<Float32Array>(coordList.size) { coordList[it] }
        return result 
    }
    
}

/* vi: se ts=4 sw=4 et: */

