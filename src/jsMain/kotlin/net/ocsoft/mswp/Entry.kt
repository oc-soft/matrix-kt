package  net.ocsoft.mswp

import kotlinx.browser.window
import org.w3c.dom.get

/**
 * run the program
 */
actual fun run() {
    val settings = window.get("mswpSettings")
    Context.mainPage?.run(settings)
}

// vi: se ts=4 sw=4 et:
