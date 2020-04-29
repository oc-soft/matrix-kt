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

external interface Obj {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
}

external interface Window {
    var innerHeight: Number
    var offsetHeight: Number
    var innerWidth: Number
    var offsetWidth: Number
    var pageXOffset: Number
    var pageYOffset: Number
    var getComputedStyle: Any
    fun addEventListener(type: Any, listener: Any, optionsOrUseCapture: Any = definedExternally)
    fun removeEventListener(type: Any, listener: Any, optionsOrUseCapture: Any = definedExternally)
    var Element: Element
    var HTMLElement: HTMLElement
    var Node: Node
    override fun toString(): String /* "[object Window]" */
    var devicePixelRatio: Number
}

external interface Rect {
    var width: Number
    var height: Number
    var x: Number
    var y: Number
}

external interface Offsets {
    var y: Number
    var x: Number
}

external interface StateRects {
    var reference: Rect
    var popper: Rect
}

external interface StateOffsets {
    var popper: Offsets
    var arrow: Offsets?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$0` {
    var reference: dynamic /* Element | VirtualElement */
        get() = definedExternally
        set(value) = definedExternally
    var popper: HTMLElement
    var arrow: HTMLElement?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$1` {
    var reference: Array<dynamic /* Element | Window */>
    var popper: Array<dynamic /* Element | Window */>
}

external interface `T$2` {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
}

external interface `T$3` {
    @nativeGetter
    operator fun get(key: String): dynamic /* String | Boolean */
    @nativeSetter
    operator fun set(key: String, value: String)
    @nativeSetter
    operator fun set(key: String, value: Boolean)
}

external interface `T$4` {
    @nativeGetter
    operator fun get(key: String): `T$3`?
    @nativeSetter
    operator fun set(key: String, value: `T$3`)
}

external interface State {
    var elements: `T$0`
    var options: Options
    var placement: Any
    var strategy: String /* "absolute" | "fixed" */
    var orderedModifiers: Array<Modifier<Any>>
    var rects: StateRects
    var scrollParents: `T$1`
    var styles: `T$2`
    var attributes: `T$4`
    var modifiersData: Json
    var reset: Boolean
}

external interface StatePartial {
    var elements: `T$0`?
        get() = definedExternally
        set(value) = definedExternally
    var options: Options?
        get() = definedExternally
        set(value) = definedExternally
    var placement: Any?
        get() = definedExternally
        set(value) = definedExternally
    var strategy: String /* "absolute" | "fixed" */
    var orderedModifiers: Array<Modifier<Any>>?
        get() = definedExternally
        set(value) = definedExternally
    var rects: StateRects?
        get() = definedExternally
        set(value) = definedExternally
    var scrollParents: `T$1`?
        get() = definedExternally
        set(value) = definedExternally
    var styles: `T$2`?
        get() = definedExternally
        set(value) = definedExternally
    var attributes: `T$4`?
        get() = definedExternally
        set(value) = definedExternally
    var modifiersData: Json?
        get() = definedExternally
        set(value) = definedExternally
    var reset: Boolean?
        get() = definedExternally
        set(value) = definedExternally
}

external interface Instance {
    var state: State
    var destroy: () -> Unit
    var forceUpdate: () -> Unit
    var update: () -> Promise<StatePartial>
    var setOptions: (options: OptionsPartial) -> Promise<StatePartial>
}

external interface ModifierArguments<Options> {
    var state: State
    var instance: Instance
    var options: Any
    var name: String
}

external interface Modifier<Options> {
    var name: String
    var enabled: Boolean
    var phase: Any
    var requires: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var requiresIfExists: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var fn: (arg0: ModifierArguments<Options>) -> dynamic
    var effect: ((arg0: ModifierArguments<Options>) -> () -> dynamic)?
        get() = definedExternally
        set(value) = definedExternally
    var options: Obj?
        get() = definedExternally
        set(value) = definedExternally
    var data: Obj?
        get() = definedExternally
        set(value) = definedExternally
}

external interface ModifierPartial<Options> {
    var name: String?
        get() = definedExternally
        set(value) = definedExternally
    var enabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var phase: Any?
        get() = definedExternally
        set(value) = definedExternally
    var requires: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var requiresIfExists: Array<String>?
        get() = definedExternally
        set(value) = definedExternally
    var fn: ((arg0: ModifierArguments<Options>) -> dynamic)?
        get() = definedExternally
        set(value) = definedExternally
    var effect: ((arg0: ModifierArguments<Options>) -> () -> dynamic)?
        get() = definedExternally
        set(value) = definedExternally
    var options: Obj?
        get() = definedExternally
        set(value) = definedExternally
    var data: Obj?
        get() = definedExternally
        set(value) = definedExternally
}

external interface EventListeners {
    var scroll: Boolean
    var resize: Boolean
}

external interface Options {
    var placement: Any
    var modifiers: Array<ModifierPartial<Any>>
    var strategy: String /* "absolute" | "fixed" */
    var onFirstUpdate: ((arg0: StatePartial) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

external interface OptionsPartial {
    var placement: Any?
        get() = definedExternally
        set(value) = definedExternally
    var modifiers: Array<ModifierPartial<Any>>?
        get() = definedExternally
        set(value) = definedExternally
    var strategy: String /* "absolute" | "fixed" */
    var onFirstUpdate: ((arg0: StatePartial) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
}

typealias UpdateCallback = (arg0: State) -> Unit

external interface ClientRectObject {
    var x: Number
    var y: Number
    var top: Number
    var left: Number
    var right: Number
    var bottom: Number
    var width: Number
    var height: Number
}

external interface SideObject {
    var top: Number
    var left: Number
    var right: Number
    var bottom: Number
}

external interface SideObjectPartial {
    var top: Number?
        get() = definedExternally
        set(value) = definedExternally
    var left: Number?
        get() = definedExternally
        set(value) = definedExternally
    var right: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bottom: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface VirtualElement {
    var getBoundingClientRect: () -> dynamic
    var contextElement: Element?
        get() = definedExternally
        set(value) = definedExternally
}
