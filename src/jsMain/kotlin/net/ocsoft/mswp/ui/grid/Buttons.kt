package net.ocsoft.mswp.ui


class Buttons(var mineButton : MineButton,
    var rowCount: Int,
    var columnCount: Int,
    var buttonGap : FloatArray,
    var buttonZGap: FloatArray) {

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
 }


