package net.ocsoft.mswp
/**
 * color scheme
 */
class ColorScheme(
    /** 
     * color [0,1] values
     */
    val colors: Array<FloatArray> = Array<FloatArray>(
        ColorScheme.colors.size) {
            ColorScheme.colors[it].copyOf()
        },
    /**
     * environment colors
     */
    val envColors: Array<FloatArray> = Array<FloatArray>(
        ColorScheme.envColors.size) { 
            ColorScheme.envColors[it].copyOf()
        }) {
    
    companion object {
        /**
         * color [0,1] values
         */
        val colors = arrayOf<FloatArray> (
            floatArrayOf(0xF2/(0xFF).toFloat(),  
                0xF2 / (0xFF).toFloat(),
                0xEF / (0xFF).toFloat(),
                1f),
            floatArrayOf(0x38 / (0xFF).toFloat(),
                0xBB / (0xFF).toFloat(),
                0xDA / (0xFF).toFloat(),
                1f),
            floatArrayOf(0x64 / (0xFF).toFloat(),
                0xA3 / (0xFF).toFloat(),
                0xBC / (0xFF).toFloat(),
                1f), 
            floatArrayOf(0x74 / (0xFF).toFloat(),
                0x6B / (0xFF).toFloat(),
                0x74 / (0xFF).toFloat(),
                1f), 
            floatArrayOf(0x44 / (0xFF).toFloat(),
                0x4D / (0xFF).toFloat(),
                0x5F / (0xFF).toFloat(),
                1f))
        /**
         * environment colors
         */
        val envColors = arrayOf(
            floatArrayOf(0f, 0f, 0f, 1f))

        /**
         * board color index
         */
        val Board: Int = 4


        /**
         * mine number color
         */
        val MineNumber: Int = 0

        /**
         * mine mark color index
         */
        val Mine: Int = MineNumber

        /**
         * mine button front color index
         */
        val ButtonFront: Int = 1

        /**
         * mine button back color index
         */
        val ButtonBack: Int = 3

        /**
         * environment background color index
         */
        val Background: Int = 0
    }
    
    /**
     * get a indexed color
     */
    operator fun get(i: Int): FloatArray {
        return colors[i].copyOf()
    }

    /**
     * set a indexed color
     */
    operator fun set(i: Int, v: FloatArray) {
        for (j in 0 until minOf(v.size, colors[i].size)) {
            colors[i][j] = v[j]
        }
    }

    /**
     * indexed color size
     */
    val size: Int
        get() {
            return colors.size
        }


    /**
     * backgraound color
     */
    val backgournd: FloatArray
        get() {
            return envColors[ColorScheme.Background]
        }    

    
}
// vi: se ts=4 sw=4 et:
