package net.ocsoft.mswp
import kotlinx.browser.window
import org.w3c.dom.get
import kotlin.js.Promise

/**
 * polyfill
 */
class Polyfill {
    companion object {
        /**
         * load polyfill
         */
        fun load(): Promise<Unit> {
            var result : Promise<Unit>?
            
            result = Promise<Unit> {
                resolve, _ ->
                val te = window.get("TextDecoder")
                if (te == null) {
                    val scriptElem = window.document.createElement("script")
                    val script : dynamic = scriptElem
                    script.src = 
                        "https://unpkg.com/text-encoding/lib/encoding.js"
                    script.onload = {
                        resolve(Unit)
                    }
                    window.document.body?.appendChild(script)
                } else {
                    resolve(Unit)
                }
            } 
            return result
        }
 
        /**
         * load polyfill
         */
        fun load1(): Promise<Unit> {
            var result : Promise<Unit>?
            val te = window.get("TextDecoder")
            if (te != null) {
                var prm = window.fetch(
                    "https://unpkg.com/text-encoding/lib/encoding.js")
                result = prm.then({
                    it.text()
                }).then({
                    val scriptElem = window.document.createElement("script")
                    val script : dynamic = scriptElem
                    script.src = it
                    Unit
                })
            } else {
                result = Promise<Unit> {
                    resolve, _ ->
                    resolve(Unit)
                } 
            }
            return result
        }
    }
}

// vi: se ts=4 sw=4 et:
