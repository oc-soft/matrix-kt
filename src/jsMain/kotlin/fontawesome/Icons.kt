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
         * get all identifiers in code order
         */
        fun getAllIdentifiersCodeOrder() : List<Identifier> {
            val comparator = object: Comparator<Identifier> {
                override fun compare(a: Identifier, b: Identifier): Int {
                    return a.code.compareTo(b.code) 
                }
            }
            return getAllIdentifiers().sortedWith(comparator)
        }
        /**
         * get all identifiers
         */
        fun getAllIdentifiers() : Set<Identifier> {
            val result = HashSet<Identifier>()

            val fa = window.get("___FONT_AWESOME___")
            val styles = js("fa.styles")

            val prefixes = js("Object.keys(styles)") 
            for (i0 in 0..prefixes.length - 1) {
                val prefix = prefixes[i0]
                if (prefix != "fa") {
                    val icons = styles[prefix]
                    val names = js("Object.keys(icons)")
                    for (i1 in 0..names.length - 1) {
                        val name = names[i1]
                        result.add(Identifier(prefix, name, 
                            icons[name][3]))
                    }
                }
            }
            return result 
        }

        /**
         * load an icon to be ready to be rendered.
         */
        fun loadIcon(name: String, prefix: String?) {
            if (js("typeof window.FontAwesome  !== 'undefined'")) {
                val fa: dynamic = js("window.FontAwesome")
                fa.findIconDefinition(object {
                    @JsName("prefix")
                    var prefix = prefix

                    @JsName("iconName")
                    var iconName = name
                    
                })
            }
        }
    } 


    /**
     * icon idetifier
     */
    data class Identifier(val prefix: String,
        val name: String,
        val code: String)  {
    }


}
