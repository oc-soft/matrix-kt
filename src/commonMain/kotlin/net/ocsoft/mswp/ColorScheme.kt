package net.ocsoft.mswp

import kotlin.math.min
import kotlin.math.roundToInt

/**
 * equals extension
 */
fun FloatArray.elementEquals(other: FloatArray): Boolean {
    val otherFloatArray = other
    var result = this.size == otherFloatArray.size
    if (result) {
        for (i in 0 until this.size) {
            result = this[i] == otherFloatArray[i]
            if (!result) {
                break
            }
        }
    }
    return result
}

/**
 * color scheme
 */
class ColorScheme(
    /** 
     * color [0,1] values
     */
    colors: Array<FloatArray> = Array<FloatArray>(
        ColorScheme.colorsI.size) {
            ColorScheme.colorsI[it].copyOf()
        },
    /**
     * environment colors
     */
    envColors: Array<FloatArray> = Array<FloatArray>(
        ColorScheme.envColorsI.size) { 
            ColorScheme.envColorsI[it].copyOf()
        }) {
    
    companion object {
        /**
         * color [0,1] values
         */
        private val colorsI = arrayOf<FloatArray> (
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
         * initial setting colors
         */ 
        val colors: Array<FloatArray>
            get() {
                return Array<FloatArray>(colorsI.size) {
                    colorsI[it].copyOf()
                }
            }
        /**
         * environment colors
         */
        private val envColorsI = arrayOf(
            floatArrayOf(0f, 0f, 0f, 1f),
            floatArrayOf(1f, 1f, 1f, 1f))

        /**
         * environment initial colors
         */
        val envColors: Array<FloatArray>
            get() {
                return Array<FloatArray>(envColorsI.size) {
                    envColorsI[it].copyOf()
                }
            }


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

        /**
         * environment foreground color index
         */
        val Foreground: Int = 1


        /**
         * convert html rgb string from float array [0,1.0]
         */
        fun toHtmlRgb(color: FloatArray): String {
            val intColor = IntArray(3) {
                (color[it] * 0xff.toFloat()).roundToInt()
            }
            return "rgb(${intColor[0]}, ${intColor[1]}, ${intColor[2]})"
        }
    }
    
    /**
     * colors
     */
    private val colors =
        Array<FloatArray>(colors.size) { colors[it].copyOf() }

    /**
     * environment colors
     */
    private val envColors =
        Array<FloatArray>(envColors.size) { envColors[it].copyOf() }

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
            return getEnvironment(Background)!!.copyOf()
        }

    /**
     * foreground color
     */
    val foreground: FloatArray
        get() {
            return getEnvironment(Foreground)!!.copyOf()
        }

    /**
     * size of environment color
     */
    val envColorSize: Int
        get() {
            return envColors.size
        }

    /**
     * constructor
     */
    constructor(colorScheme: ColorScheme): 
        this(colorScheme.colors, colorScheme.envColors) {
    }


    /**
     * get environment color
     */
    fun getEnvironment(index: Int): FloatArray? {
        var result: FloatArray? = null 
        if (0 <= index && index < envColorSize) {
            result = envColors[index].copyOf()
        }
        return result
    }

    /**
     * set environment color
     */
    fun setEnvironment(index: Int, color: FloatArray) {
        if (0 <= index && index < envColorSize) {
            for (i in 0 until min(envColors[index].size, color.size)) {
                envColors[index][i] = color[i]
            }
        }         
    }

    /**
     * equals 
     */
    override fun equals(other: Any?): Boolean {
        var result = other is ColorScheme
        if (result) {
            val otherColorScheme = other as ColorScheme
            result = size == otherColorScheme.size  
            if (result) {
                for (i in 0 until size) {
                    result = this[i].elementEquals(otherColorScheme[i])
                    if (!result) {
                        break
                    }
                }
            }
            if (result) {
                result = envColorSize == otherColorScheme.envColorSize
            }
            if (result) {
                for (i in 0 until envColorSize) {
                    result = getEnvironment(i)!!.elementEquals(
                        otherColorScheme.getEnvironment(i)!!)
                    if (!result) {
                        break
                    }
                }
            }
        }
        return result
    }
}
// vi: se ts=4 sw=4 et:
