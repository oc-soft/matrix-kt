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
                return js("require('fontawesome-svg-core')");    
            } 
    }	
}
