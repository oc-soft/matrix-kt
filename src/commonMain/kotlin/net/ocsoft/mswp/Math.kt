package net.ocsoft.mswp

import kotlin.math.*


/**
 * manage some mathmatical formulas.
 */
class Math {
    companion object {
        /**
         * calcuate rotation matrix
         * returned matrix is open gl order. 
         */ 
        fun calcRotationMatrix3(axis: FloatArray,
            rotation: Float) : FloatArray? {
            val axisN = axis
            var result : FloatArray? = null
            if (axisN != null) {
                val cosValue = cos(rotation)
                val sinValue = sin(rotation)
                val elemFormulas: Array<(Float, Float, Float, Float)->Float> =
                    arrayOf(
                        { u1, u2, u3, u4 -> 
                            cosValue + u1.pow(2) * (1 - cosValue) },
                        { u1, u2, u3, u4 ->
                            u2 * u1 * (1 - cosValue) + u3 * sinValue },
                        { u1, u2, u3, u4 ->
                            u3 * u1 * (1 - cosValue) - u2 * sinValue },
                        { u1, u2, u3, u4 -> u4 }
                    )
                    
                result = FloatArray(4 * 4) { i ->
                    val rowIndex = i / 4
                    val colIndex = i % 4
                    var vec0 = FloatArray(4) { j ->
                        var value0 = 0f
                        if (rowIndex < 3) {
                            if (j < 3) {
                                value0 = axisN[(rowIndex + i) % 3]
                            }
                        } else {
                            if (colIndex == 3) {
                                value0 = 1f
                            }
                        }
                        value0
                    }
                    elemFormulas[rowIndex](vec0[0], vec0[1], vec0[2], vec0[3])
                }                    
                    
            } 
            return result
        }  
        /**
         * normalize vector
         */
        fun normalize3(vec: FloatArray) : FloatArray? {
            val vectmp = vec.copyOf(3)
            val len = sqrt(vectmp.sumByDouble({elem -> 
                elem.pow(2).toDouble()})).toFloat()
            var result: FloatArray? = null 
            if (len > 0) {
                result = FloatArray(vectmp.size) { i -> vectmp[i] / len }
            }
            return result
        }
    } 
}
