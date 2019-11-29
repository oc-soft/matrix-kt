package net.ocsoft.mswp.ui.grid

import kotlin.math.*
import net.ocsoft.mswp.ui.*
import net.ocsoft.mswp.CellIndex
import org.khronos.webgl.*
import net.ocsoft.mswp.Logic
import org.w3c.dom.*



class Buttons(var mineButton : MineButton,
    var rowCount: Int,
    var columnCount: Int,
    var buttonGap : FloatArray,
    var buttonZGap: FloatArray,
    val colorMap : ColorMap) {

    /**
     * cached color for picking
     */
    var colorsForPickingCache : FloatArray? = null
    /**
     * color for picking
     */
    val colorsForPicking : FloatArray
        get() {
            if (colorsForPickingCache == null) {
                registerPickingInfoIntoColorMap()
            }
            return colorsForPickingCache!!
        }
       

    /**
     * button size
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
    val zGapForDrawing : FloatArray
        get() {
            val totalButtonSize = this.totalButtonSize
            val maxSize = totalButtonSize.max() as Float
            return buttonZGap.map({ it * maxSize }).toFloatArray()
        }

    /**
     * textures
     */
    var textures: Textures? = null

    /**
     * logic
     */
    var logic: Logic? = null

    /**
     * transparent texture
     */
    val transparentTexture: WebGLTexture?
        get() {
            return getTransparentTexture()
        }
    /**
     * get picking color at row and column.
     */
    fun getPickingColor(rowIndex : Int, colIndex : Int): FloatArray {
        val colors = colorsForPicking
        val baseIndex = (rowIndex * columnCount + colIndex) * 4
        return FloatArray(4) { i -> colors[baseIndex + i] }
    } 

    /**
     * find position by color
     */
    fun findPositionByPickingColor(color : FloatArray) : IntArray? {
        return colorMap.getValue(color) as IntArray?
    }
    /**
     * find position by color
     */
    fun findPositionByPickingColor(color : ByteArray) : IntArray? {
        return colorMap.getValueBy255(color) as IntArray?
    }
 
    fun findPositionByPickingColor(color : Short) : IntArray? {
        return colorMap.getValue(color) as IntArray?
    }
    /**
     * register picking (row and column) into color map
     */
    fun registerPickingInfoIntoColorMap() {
        val maxIndex = min(ColorMap.ButtonsColorRangeIndices[1],
            rowCount * columnCount + ColorMap.ButtonsColorRangeIndices[0])
        val colors = FloatArray((maxIndex
            - ColorMap.ButtonsColorRangeIndices[0]) * 4)
        for (i in ColorMap.ButtonsColorRangeIndices[0]..maxIndex) {
            val baseIndex = i -  ColorMap.ButtonsColorRangeIndices[0]
            val colIndex = baseIndex % columnCount 
            val rowIndex = baseIndex / columnCount
            val color = ColorMap.colorIndexToColor(i)
            colorMap.register(color, intArrayOf(rowIndex, colIndex)) 
            color.forEachIndexed({ j, elem ->
                colors[baseIndex * 4 + j] = elem
            }) 
        } 
        colorsForPickingCache = colors
    }
    
    /**
     * get texture instance for button
     */
    fun getNumberImage(rowIndex: Int, colIndex: Int): WebGLTexture? {
        var result : WebGLTexture? = null
        var logic = this.logic
        var textures = this.textures
        if (textures != null && logic != null) {
            var num : Int? = null 
            num = logic.getNumberIfOpened(rowIndex, colIndex)  
            if (num != null && num > 0) {
                result = textures.getNumberImageBlankTexture(num)
            }  
        }
        return result
    }
    /**
     * get alternate texture
     */
    fun getAlternateTexture(rowIndex: Int, colIndex: Int) : WebGLTexture? {
        var result : WebGLTexture? = null
        var logic = this.logic
        if (logic != null) {
            val cell = CellIndex(rowIndex, colIndex)
            if (logic.isOver && cell in logic.mineLocations) {
                result = getNgTexture() 
            } else {
                result = getTransparentTexture()
            }
        }
        return result
    }
    
   
    /**
     * get transparent texture
     */
    fun getTransparentTexture() : WebGLTexture? {
        var textures = this.textures
        var result : WebGLTexture? = null
        if (textures != null) {
            result = textures.blackTransparentTexture 
        }
        return result
    }
    /**
     * get ng image texture
     */
    fun getNgTexture() : WebGLTexture? {
        var textures = this.textures
        var result : WebGLTexture? = null
        if (textures != null) {
            result = textures.ngImageTexture 
        }
        return result
    }
}


