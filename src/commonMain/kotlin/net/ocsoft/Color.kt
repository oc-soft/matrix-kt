package net.ocsoft

import kotlin.math.min
import kotlin.math.max
import kotlin.text.Regex
import kotlin.text.MatchResult
import kotlin.text.toInt
import kotlin.text.toString
/**
 * color object
 */
class Color(red: Value,
    green: Value,
    blue: Value,
    alpha: Value? = null) {
  
    /**
     * class instance
     */
    companion object {
        /**
         * number pattern
         */
        val numberPattern = "[0-9]+|[0-9]*\\.[0-9]+"

        /**
         * hex number pattern
         */
        val hexNumberPattern = "[0-9a-fA-F]"

        /**
         * parse css color string format
         */
        fun parse(strColor: String): Color? {
            var result: Color?
            val patternParsers = arrayOf(
                createParseRgb(),
                createParseRgba(),
                createParseRgbaL4(),
                createParseHex(),
                createParseHex2())
            val colorContainer = Array<Color?>(1) { null } 
            patternParsers.firstOrNull { 
                val match = it.first.find(strColor)
                if (match != null) {
                    colorContainer[0] = it.second(match)
                }
                colorContainer[0] != null
            }
            result = colorContainer[0]
            return result
        }
        /**
         * rgb pattern
         */
        val rgbPattern = Regex("rgb\\("
            + "\\s*((?:${numberPattern})%?)\\s*,"
            + "\\s*((?:${numberPattern})%?)\\s*," 
            + "\\s*((?:${numberPattern})%?)\\s*\\)")

        /**
         * create pattern and match handler to parse rgb style string
         */
        fun createParseRgb(): Pair<Regex, (MatchResult)->Color?> {
            val parser: (MatchResult)->Color? = {
                match: MatchResult -> 
                val valueStrs = match.groupValues
                val values = Array<Color.Value>(valueStrs.size - 1) {
                    val valueStr = valueStrs[it + 1]
                    parseNumberStr(valueStr)
                }   
                Color(values[0], values[1], values[2])
            }

            return Pair(rgbPattern, parser)
        } 
        /**
         * rgb(a) pattern
         */
        val rgbaPattern = Regex("rgb(?:a?)\\("
            + "\\s*((?:${numberPattern})%?)\\s*,"
            + "\\s*((?:${numberPattern})%?)\\s*," 
            + "\\s*((?:${numberPattern})%?)\\s*"
            + "(?:,\\s*((?:${numberPattern})%?))?\\s*"
            + "\\)")

        /**
         * create pattern and match handler to parse rgb style string
         */
        fun createParseRgba(): Pair<Regex, (MatchResult)->Color?> {
            val parser: (MatchResult)->Color? = {
                match: MatchResult -> 
                val valueStrs = match.groupValues
                val values = Array<Color.Value>(
                    if (valueStrs[4].isNullOrEmpty()) { 3 } else { 4 }) {
                    val valueStr = valueStrs[it + 1]
                    parseNumberStr(valueStr)
                }   
                Color(values[0], values[1], values[2],
                    if (values.size > 3) { values[3] } else { null })
            }

            return Pair(rgbaPattern, parser)
        } 
        /**
         * rgb(a) pattern level 4
         */
        val rgbaPatternl4 = Regex("rgb(?:a?)\\("
            + "\\s*((?:${numberPattern})%?)"
            + "\\s+((?:${numberPattern})%?)" 
            + "\\s+((?:${numberPattern})%?)\\s*"
            + "(?:/\\s*((?:${numberPattern})%?))?\\s*"
            + "\\)")

        /**
         * create pattern and match handler to parse rgb style string
         */
        fun createParseRgbaL4(): Pair<Regex, (MatchResult)->Color?> {
            val parser: (MatchResult)->Color? = {
                match: MatchResult -> 
                val valueStrs = match.groupValues
                val values = Array<Color.Value>(
                    if (valueStrs[4].isNullOrEmpty()) { 3 } else { 4 }) {
                    val valueStr = valueStrs[it + 1]
                    parseNumberStr(valueStr)
                }   
                Color(values[0], values[1], values[2],
                    if (values.size > 3) { values[3] } else { null })
            }

            return Pair(rgbaPatternl4, parser)
        } 

        /**
         * hex pattern
         */
        val hexPattern = Regex("#"
            + "(${hexNumberPattern}{2})"
            + "(${hexNumberPattern}{2})"
            + "(${hexNumberPattern}{2})"
            + "(${hexNumberPattern}{2})?")

        /**
         * create pattern and match handler to parse rgb style string
         */
        fun createParseHex(): Pair<Regex, (MatchResult)->Color?> {
            val parser: (MatchResult)->Color? = {
                match: MatchResult -> 
                val valueStrs = match.groupValues
                val values = Array<Color.Value>(
                    if (valueStrs[4].isNullOrEmpty()) { 3 } else { 4 }) {
                    val valueStr = valueStrs[it + 1]
                    Color.Value(valueStr.toInt(16))
                }  
                Color(values[0], values[1], values[2],
                    if (values.size > 3) { values[3] } else { null })
            }

            return Pair(hexPattern, parser)
        } 

        /**
         * hex pattern
         */
        val hexPattern2 = Regex("#"
            + "(${hexNumberPattern})"
            + "(${hexNumberPattern})"
            + "(${hexNumberPattern})"
            + "(${hexNumberPattern})?")

        /**
         * create pattern and match handler to parse rgb style string
         */
        fun createParseHex2(): Pair<Regex, (MatchResult)->Color?> {
            val parser: (MatchResult)->Color? = {
                match: MatchResult -> 
                val valueStrs = match.groupValues
                val values = Array<Color.Value>(
                    if (valueStrs[4].isNullOrEmpty()) { 3 } else { 4 }) {
                    val valueStr = valueStrs[it + 1]
                    Color.Value("${valueStr}${valueStr}".toInt(16))
                }   
                Color(values[0], values[1], values[2],
                    if (values.size > 3) { values[3] } else { null })
            }

            return Pair(hexPattern2, parser)
        } 



        /**
         * parse number
         */
        fun parseNumberStr(numStr: String): Color.Value {
            var divider = 1f
            var value: Float
            if (numStr.endsWith('%')) {
                divider = 100f
                value = numStr.dropLast(1).toFloat()
            } else {
                if (numStr.indexOf('.') < 0) {
                    divider = 0xff.toFloat()    
                }
                value = numStr.toFloat()
            } 
            return Value(value / divider)
        }
     }
 
    /**
     * color value
     */
    class Value(valueI: Int) {
        
        /**
         * value represent integer
         */
        val intValue: Int = min(max(valueI, 0x0), 0xff)

        /**
         * value represent float
         */
        val floatValue: Float
            get() {
                return intValue.toFloat() / 0xff.toFloat()
            }

        /**
         * secondary constructor
         */
        constructor(valueF: Float): this((valueF * 0xff.toFloat()).toInt())

        /**
         * copy constructor
         */
        constructor(value: Value): this(value.intValue)


        /**
         * plus operator
         */
        operator fun plus(value: Value): Value {
            return Value(intValue + value.intValue)
        } 

        /**
         * minus operator
         */
        operator fun minus(value: Value): Value {
            return Value(intValue - value.intValue)
        } 


        /**
         * increment operator
         */
        operator fun inc(): Value {
            return this + Value(1)
        }
        
        /**
         * decrement operator
         */
        operator fun dec(): Value {
            return this - Value(1)
        }

        /**
         * equals operator
         */
        override fun equals(other: Any?): Boolean {
            var result = false
            if (other is Value) {
                result = compareTo(other) == 0
            } 
            return result
        }

        /**
         * comparator 
         */
        operator fun compareTo(value: Value): Int {
            return intValue - value.intValue
        }
    } 

    private val values: Array<Value> = 
        Array<Value>(if (alpha != null) { 4 } else { 3 }) {
            when (it) {
                0 -> red
                1 -> green
                2 -> blue
                else -> alpha!!
            }
        }
    /**
     * red
     */
    val red: Value = this[0]

    /**
     * green
     */
    val green: Value = this[1]

    /**
     * blue
     */
    val blue: Value = this[2]


    /**
     * alpha
     */
    val alpha: Value? = if (size > 3) { this[3] } else { null }

    /**
     * size
     */
    val size: Int
        get() {
            return values.size
        }
    /**
     * operator
     */
    operator fun get(idx: Int): Value {
        return values[idx]
    }
    
    /**
     * secondary constructor
     */ 
    constructor(red: Int,
        green: Int,
        blue: Int,
        alpha: Int?): 
            this(
                Value(red),
                Value(green),
                Value(blue),
                if (alpha != null) { Value(alpha) } else { null })

    /**
     * secondary constructor
     */ 
    constructor(red: Int,
        green: Int,
        blue: Int,
        alpha: Float?): 
            this(
                Value(red),
                Value(green),
                Value(blue),
                if (alpha != null) { Value(alpha) } else { null })
     
    /**
     * secondary constructor
     */ 
    constructor(red: Float,
        green: Float,
        blue: Float,
        alpha: Float?): 
            this(
                Value(red),
                Value(green),
                Value(blue),
                if (alpha != null) { Value(alpha) } else { null })
 
    /**
     * convert to string
     */
    @ExperimentalUnsignedTypes
    override fun toString(): String {
        val alpha = this.alpha
        var result: String
        result = "#${red.intValue.toUByte().toString(16)}" 
        result += "${green.intValue.toUByte().toString(16)}"
        result += "${blue.intValue.toUByte().toString(16)}"
        if (alpha != null) {
            result += "${alpha.intValue.toUByte().toString(16)}"
        }
        return result
    }
}

// vi: se ts=4 sw=4 et:
