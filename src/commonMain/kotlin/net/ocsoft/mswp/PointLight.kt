package net.ocsoft.mswp

import kotlin.math.pow

/**
 * represent pointing light
 */
class PointLight(point: FloatArray = floatArrayOf(-1f, 1f, 2.5f)) {

    companion object {
        /**
         * format point value
         */
        fun formatPointValue(value: Float): Float {
            var result = kotlin.math.round(value * 10f.pow(5))
            result = result * 10f.pow(-5)
            return result 
        }

        /**
         * format point
         */
        fun formatPoint(point: FloatArray): FloatArray {
            return FloatArray(point.size) {
                formatPointValue(point[it])
            }
        }
    }
    /**
     * light origin
     */
    var point = point.copyOf()
        set (value) {
            for (i in 0 until kotlin.math.min(field.size, value.size)) {
                field[i] = formatPointValue(value[i])
            } 
        }
        get() {
            return field.copyOf()
        }

}
// vi: se ts=4 sw=4 et:
