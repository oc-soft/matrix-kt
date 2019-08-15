@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@file:JsModule("webfontloader")
package WebFont

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

external fun load(config: Config): Unit = definedExternally
external interface Config {
    var classes: Boolean? get() = definedExternally; set(value) = definedExternally
    var events: Boolean? get() = definedExternally; set(value) = definedExternally
    var timeout: Number? get() = definedExternally; set(value) = definedExternally
    val loading: (() -> Unit)? get() = definedExternally
    val active: (() -> Unit)? get() = definedExternally
    val inactive: (() -> Unit)? get() = definedExternally
    val fontloading: ((familyName: String, fvd: String) -> Unit)? get() = definedExternally
    val fontactive: ((familyName: String, fvd: String) -> Unit)? get() = definedExternally
    val fontinactive: ((familyName: String, fvd: String) -> Unit)? get() = definedExternally
    var context: Array<String>? get() = definedExternally; set(value) = definedExternally
    var custom: Custom? get() = definedExternally; set(value) = definedExternally
    var google: Google? get() = definedExternally; set(value) = definedExternally
    var typekit: Typekit? get() = definedExternally; set(value) = definedExternally
    var fontdeck: Fontdeck? get() = definedExternally; set(value) = definedExternally
    var monotype: Monotype? get() = definedExternally; set(value) = definedExternally
}
external interface Google {
    var families: Array<String>
    var text: String? get() = definedExternally; set(value) = definedExternally
}
external interface Typekit {
    var id: Array<String>? get() = definedExternally; set(value) = definedExternally
}
external interface `T$0` {
    @nativeGetter
    operator fun get(fontFamily: String): String?
    @nativeSetter
    operator fun set(fontFamily: String, value: String)
}
external interface Custom {
    var families: Array<String>? get() = definedExternally; set(value) = definedExternally
    var urls: Array<String>? get() = definedExternally; set(value) = definedExternally
    var testStrings: `T$0`? get() = definedExternally; set(value) = definedExternally
}
external interface Fontdeck {
    var id: String? get() = definedExternally; set(value) = definedExternally
}
external interface Monotype {
    var projectId: String? get() = definedExternally; set(value) = definedExternally
    var version: Number? get() = definedExternally; set(value) = definedExternally
}
