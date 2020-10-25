package net.ocsoft.mswp.settings

import net.ocsoft.Color

/**
 * color scheme object
 */
class ColorScheme {
    
    /**
     * class instance
     */
    companion object {

        /**
         * convert color scheme object to less var string
         */
        fun convertToLessVars(
            colorSchemeObj: net.ocsoft.mswp.ColorScheme): Map<String, String> {
            val less = colorScheme.less
            val keys = less.data.keys 
            val mapping = less.mapping
            val result = HashMap<String, String>()
            keys.forEach { key ->
                val replacementKeys = mapping[key] 
                var strValue: String? = null
                if (replacementKeys != null) {
                    val colorSchemeKey = replacementKeys[0]
                    var color: Color? = null
                    if ("environment" == replacementKeys[0]) {
                        val idx: Int = replacementKeys[1] as Int
                        val colorValues = colorSchemeObj.getEnvironment(idx) 
                        if (colorValues != null) {
                            color = Color(
                                Color.Value(colorValues[0]),
                                Color.Value(colorValues[1]),
                                Color.Value(colorValues[2]),
                                Color.Value(colorValues[1])) 
                        }
                        
                    }
                    if (color != null) {
                        strValue = color.toString()
                    }
                }
                if (strValue != null) {
                    result[key.toString()] = strValue
                }
            }
            return result
        }
    } 
}
// vi: se ts=4 sw=4 et: 
