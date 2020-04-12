package fontawesome


import kotlin.js.js


/**
 * fontawesome svg-core
 */
class SvgCore {

    /**
     * class methods
     */
    companion object {
        val instance : dynamic
            get() {
                return js("require('@fortawesome/fontawesome-svg-core')");    
            } 
    }	
}
/* vi: se ts=4 sw=4 et: */
