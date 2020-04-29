@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
package popper

import kotlin.js.*
import kotlin.js.Json
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface PopperGeneratorArgs {
    var defaultModifiers: Array<Modifier<Options>>?
        get() = definedExternally
        set(value) = definedExternally
    var defaultOptions: Any?
        get() = definedExternally
        set(value) = definedExternally
}

/**
 * popper interface
 */
interface PopperI {
    fun popperGenerator(generatorOptions: PopperGeneratorArgs): (reference: dynamic /* Element | VirtualElement */, popper: HTMLElement, options: Any) -> Instance

    var createPopper: (reference: dynamic /* Element | VirtualElement */, popper: HTMLElement, options: Any) -> Instance
}

@JsModule("@popperjs/core")
@JsNonModule
external val Popper: PopperI = definedExternally

// vi: se ts=4 sw=4 et:
