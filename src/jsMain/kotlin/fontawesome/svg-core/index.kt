@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")

package fontawesome

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

external interface Config {
    var familyPrefix: String 
    var replacementClass: String
    var autoReplaceSvg: dynamic /* Boolean | String /* "nest" */ */
    var autoAddCss: Boolean
    var autoA11y: Boolean
    var searchPseudoElements: Boolean
    var observeMutations: Boolean
    var keepOriginalSource: Boolean
    var measurePerformance: Boolean
    var showMissingIcons: Boolean
}
external interface AbstractElement {
    var tag: String
    var attributes: Any
    var children: Array<AbstractElement>? get() = definedExternally; set(value) = definedExternally
}
external interface FontawesomeObject {
    var abstract: Array<AbstractElement>
    var html: Array<String>
    var node: HTMLCollection
}
external interface Icon : FontawesomeObject, IconDefinition {
    var type: String /* "icon" */
}
external interface Text : FontawesomeObject {
    var type: String /* "text" */
}
external interface Counter : FontawesomeObject {
    var type: String /* "counter" */
}
external interface Layer : FontawesomeObject {
    var type: String /* "layer" */
}
external interface Attributes {
    @nativeGetter
    operator fun get(key: String): dynamic /* String | Number */
    @nativeSetter
    operator fun set(key: String, value: String)
    @nativeSetter
    operator fun set(key: String, value: Number)
}
external interface Styles {
    @nativeGetter
    operator fun get(key: String): String?
    @nativeSetter
    operator fun set(key: String, value: String)
}
external interface Transform {
    var size: Number? get() = definedExternally; set(value) = definedExternally
    var x: Number? get() = definedExternally; set(value) = definedExternally
    var y: Number? get() = definedExternally; set(value) = definedExternally
    var rotate: Number? get() = definedExternally; set(value) = definedExternally
    var flipX: Boolean? get() = definedExternally; set(value) = definedExternally
    var flipY: Boolean? get() = definedExternally; set(value) = definedExternally
}
external interface Params {
    var title: String? get() = definedExternally; set(value) = definedExternally
    var classes: dynamic /* String | Array<String> */ get() = definedExternally; set(value) = definedExternally
    var attributes: Attributes? get() = definedExternally; set(value) = definedExternally
    var styles: Styles? get() = definedExternally; set(value) = definedExternally
}
external interface CounterParams : Params
external interface TextParams : Params {
    var transform: Transform? get() = definedExternally; set(value) = definedExternally
}
external interface IconParams : Params {
    var transform: Transform? get() = definedExternally; set(value) = definedExternally
    var symbol: dynamic /* String | Boolean */ get() = definedExternally; set(value) = definedExternally
    var mask: IconLookup? get() = definedExternally; set(value) = definedExternally
}
external interface `T$0fa` {
    var node: Node
    var callback: () -> Unit
}
external interface DOM {
    fun i2svg(params: `T$0fa`? = definedExternally /* null */): Promise<Unit>
    fun css(): String
    fun insertCss(): String
    fun watch()
}
external interface Library {
    fun add(vararg definitions: Any)
    fun reset()
}
