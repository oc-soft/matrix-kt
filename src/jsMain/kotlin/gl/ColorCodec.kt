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
                    val tmpVal = buffer[idx].toFloat() /  0xff.toFloat() 
                    result += (tmpVal * 0xf.toFloat()) / (0xf * idx).toFloat()
                }
            }

            return result
        }
    }
}

// vi: se ts=4 sw=4 et:
