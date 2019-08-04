package net.ocsoft.mswp.ui

import kotlin.math.*

class ColorMap {
       
    companion object {
        val ColorRange = byteArrayOf(
            0, 0b11111
        )
        val ButtonsColorRangeIndices = intArrayOf(
            0b0, 0xfff
        )
        

        val displacement : Float
            = 1 / (ColorRange[1] - ColorRange[0]).toFloat()

        val displacementByte : Byte
            = (0xff / (ColorRange[1] - ColorRange[0])).toByte()
        fun getColorKey(index: Int): Short {
            var tmpIdx = max(ButtonsColorRangeIndices[0], index)
            tmpIdx = min(ButtonsColorRangeIndices[1], tmpIdx)
            return ((tmpIdx shl 1) or 0x1).toShort()
        } 
        fun getKeyIndex(key: Short): Int {
            var result = key.toInt() shr 1 
            result = max(ButtonsColorRangeIndices[0], result)
            result = min(ButtonsColorRangeIndices[1], result)
            return result
        }
        fun floatColorToByteColor(rgb : FloatArray) : ByteArray {
            val tmpRgb = rgb.copyOf(3)
            return floatColorToByteColor(tmpRgb[0], tmpRgb[1], tmpRgb[2])
        } 
        fun floatColorToByteColor(r : Float, g : Float, b : Float) : ByteArray {
            var rgb = floatArrayOf(r, g, b)
            val tmpRgb = FloatArray(rgb.size) { i -> 
                var tmpValue = min(rgb[i], 1f)
                tmpValue = max(tmpValue, 0f)
                tmpValue 
            }
            rgb = tmpRgb 
            return ByteArray(rgb.size) { i -> 
                round(rgb[i] 
                    * (ColorRange[1] - ColorRange[0])
                    + ColorRange[0]).toByte()
            }
        }  
        fun byteColorToFloatColor(rgb : ByteArray) : FloatArray {
            val tmpRgb = rgb.copyOf(3)
            return byteColorToFloatColor(tmpRgb[0], tmpRgb[1], tmpRgb[2])
        }
        fun byteColorToFloatColor(r : Byte, g : Byte, b : Byte) : FloatArray {
            var rgb = byteArrayOf(r, g, b)
            return FloatArray(rgb.size) { i -> 
                var tmpVal = min(rgb[i].toFloat(), ColorRange[1].toFloat())
                tmpVal = max(tmpVal.toFloat(), ColorRange[0].toFloat())
                tmpVal -= ColorRange[0]
                tmpVal /= (ColorRange[1] - ColorRange[0])
                tmpVal
            }
        }
        fun isColorKey(key : ByteArray) : Boolean {
            var result = key.size > 3
            if (result) {
                result = key[3] == 0xff.toByte()
            }
            return result
        }
        fun conv8to5Color(color: ByteArray) : ByteArray {
            val colorToConv = color.copyOf(3) 
            val result = ByteArray(colorToConv.size) { 
                val tmpValI = (colorToConv[it].toInt() and 0xff).toInt()
                (tmpValI / displacementByte - ColorRange[0]).toByte()
            }
            return result
        }
           
        fun colorToIndex(color : FloatArray) : Int  {
            return getKeyIndex(colorToKey(color))     
        }
        fun indexToKey(index : Int) : Short {
            return getColorKey(index)
        } 
        fun colorIndexToColor(index : Int): FloatArray {
            return keyToColor(indexToKey(index))
        }
        fun keyToColor(key : Short) : FloatArray {
            val mask = 0b11111
            val shiftIndices =  intArrayOf(11, 6, 1)
            val byteColor = ByteArray(shiftIndices.size) { i ->
                ((key.toInt() shr shiftIndices[i].toInt()) and mask).toByte()   
            }
            val result = byteColorToFloatColor(byteColor).copyOf(
                byteColor.size + 1)
            result[byteColor.size] = (key.toInt() and 0b1).toFloat()
            return result
        } 

        fun colorToKey(color : FloatArray) : Short {
            val colorToBeConverted = color.copyOf(3)
            val byteColor = floatColorToByteColor(colorToBeConverted)
            return colorToKey(byteColor)
        }
        fun colorToKey(color : ByteArray) : Short {
            val byteColor = color.copyOf(3)
            val mask = 0b11111
            val shiftIndices =  intArrayOf(11, 6, 1)
            var result = 0b1.toShort()
            shiftIndices.forEachIndexed({ idx, shift ->  
                val sMask = mask shl shift
                result = ((sMask.inv() and result.toInt()) 
                    or ((mask and byteColor[idx].toInt()) shl shift)).toShort()
            }) 
            return result 
             
        } 
    }
  
    val colorKeyMap = HashMap<Short, Any?>() 
     
    fun register(key : FloatArray, value : Any) {
        val keyI = colorToKey(key) 
        colorKeyMap[keyI] = value 
    }
    fun getValue(key : FloatArray) : Any? {
        val keyI = colorToKey(key) 
        return colorKeyMap[keyI]
    } 
    
    fun getValue(key : Short) : Any? {
        return colorKeyMap[key]
    }
    fun getValueBy255(key : ByteArray) : Any? {
        var result : Any? = null
        if (isColorKey(key)) {
            val key5 = conv8to5Color(key)

            val keyI = colorToKey(key5) 
            result = getValue(keyI)
        }
        return result
    } 
     
} 
