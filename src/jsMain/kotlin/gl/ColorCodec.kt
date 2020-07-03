package gl
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

/**
 * handle open gl color data
 */
class ColorCodec {

    companion object {
        /**
         * decode float from color
         */
        fun decodeFloat(buffer: Uint8Array): Float? {
            var result: Float? = null 
            
            if (buffer.length > 3) {
                result = 0.0f                
                for (idx in 3 downTo 0) {
                    var tmpVal = buffer[idx].toFloat() /  0xff.toFloat() 
                    tmpVal *= 0xf.toFloat()
                    tmpVal /= (0xf * (idx + 1)).toFloat()
                    result += tmpVal 
                }
            }

            return result
        }
    }
}

// vi: se ts=4 sw=4 et:
