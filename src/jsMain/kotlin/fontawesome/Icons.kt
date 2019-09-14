
package fontawesome


import kotlin.js.js
import kotlin.browser.window
import org.w3c.dom.get
/**
 * fontawsome icons.
 */
class Icons {
   
    /**
     * class instance
     */ 
    companion object {
        /**
         * get all identifiers
         */
        fun getAllIdentifiers() : Set<Identifier> {
            val result = HashSet<Identifier>()

            val fa = window.get("__FONT_AWESOME__")
            val styles = js("fa.styles")

            val prefixes = js("Object.keys(styles)") 
            for (i0 in 0..prefixes.lenght - 1) {
                val prefix = prefixes[i0]
                val icons = styles[prefix]
                val names = js("Object.keys(icons)")
                for (i1 in 0..names.length - 1) {
                    val name = names[i1]
                    result.add(Identifier(prefix, name))
                }
            }
            return result 
        }
    } 
    /**
     * icon idetifier
     */
    data class Identifier(val prefix: String,
        val name: String)  {
    }


}
