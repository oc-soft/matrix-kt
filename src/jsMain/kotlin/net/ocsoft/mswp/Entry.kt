package  net.ocsoft.mswp

import kotlin.browser.window
import org.w3c.dom.get

/**
 * run the program
 */
actual fun run() {
    val settings = window.get("mswpSettings")
    Context.mainPage?.run(settings)
}
