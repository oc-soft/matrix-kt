package popper

import kotlin.text.split
import kotlin.text.Regex
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

/**
 * set attributes for popper with css setting.
 */
class Css {

    /**
     * class instance
     */
    companion object {
        /**
         * apply option
         */
        fun readOption(element: HTMLElement): OptionsPartial {
            val placement = window.getComputedStyle(element).getPropertyValue(
                "--popper-placement").trim()
            val strategy = window.getComputedStyle(element).getPropertyValue(
                "--popper-strategy").trim()
            val modifiersOffset = window.getComputedStyle(
                element).getPropertyValue("--popper-modifiers-offset").trim()
            val result = js("{}")
            
            if (placement.isNotEmpty()) {
                result.placement = placement
            }
            if (strategy.isNotEmpty()) {
                result.strategy = strategy
            } 
            if (modifiersOffset.isNotEmpty()) {
                val intOffset = parseNumbers(modifiersOffset)
                
                if (intOffset != null && intOffset.size > 1) {
                    val offset = Array<Int>(intOffset.size) { intOffset[it]  }
                    val modifiers = js("""[{
                       name: 'offset',
                       options: {
                         offset: offset
                       }
                     }]""")
                    result.modifiers = modifiers 
                }
            }
            return result
        }

        /**
         * parse space separated numbers
         */
        fun parseNumbers(spaceSeparatedNumber: String): IntArray? {
            val strNums = spaceSeparatedNumber.split(Regex("\\s+")) 

            val result = IntArray(strNums.size)
           
            var failedParse = false 
            for (i in 0..strNums.size - 1) {
                val iValue = strNums[i].toIntOrNull()
                failedParse = iValue == null
                if (!failedParse) {
                    result[i] = iValue!!
                } else {
                    break 
                }
            }
            return if (!failedParse) { result } else { null } 
        }
    }
}

// vi: se ts=4 sw=4 et:
