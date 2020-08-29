@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("@oc-soft/color-picker")
@file:JsNonModule
package net.ocsoft.color.picker

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

external interface `T$12` {
    var value: String
    var colorCircleCanvas: String
}

external interface `T$13` {
    var type: String
    var sender: Any
}

external interface `T$14` {
    var loc: Array<Number>
    var radius: Number
}

external interface `T$15` {
    var radius: Number
    var radian: Number
}

external interface `T$16` {
    var indexValue: Number
    var pickerLocation: `T$15`
}

external interface `T$17` {
    var radian: Number
    var radius: Number
}

external open class UI(template: String? = definedExternally, classMapping: `T$12`? = definedExternally, indexValue: Number = definedExternally, colorType: String = definedExternally) {
    open var indexValueField: Any
    open var colorTypeField: Any
    open var pickerLocationField: Any
    open var pickerMarkerField: Any
    open var markerRadiusField: Any
    open var updateUi: Any
    open var syncFieldWithUi: Any
    open var raiseEvent: Any
    open var classMapping: Any
    open var pickColorHandler: Any
    open var valueHandler: Any
    open var listeners: Any
    open var rootElement: Any
    open var oldContents: Any
    open var template: String?
    open var markColor: Array<Number>?
    open fun bind(rootElement: HTMLElement)
    open fun unbind()
    open fun addEventListener(type: String?, listener: (arg: `T$13`) -> Unit)
    open fun removeEventListener(type: String?, listener: (arg: `T$13`) -> Unit)
    open fun notify(type: String)
    open fun bindValue(rootElement: HTMLElement)
    open fun unbindValue(rootElement: HTMLElement)
    open fun bindColorCircle(rootElement: HTMLElement)
    open fun unbindColorCircle(rootElement: HTMLElement)
    open fun postHandleClickInColorCircle(event: Event)
    open fun handleClickInColorCircle(event: MouseEvent)
    open fun getColorCircleLocRadius(colorCanvas: HTMLCanvasElement): `T$14`
    open fun postUpdateColorCircleCanvas()
    open fun updateColorCircleCanvas(rootElement: HTMLElement)
    open fun convertPickerLocToRgb(pickerLoc: `T$15`): Array<Number>
    open fun convertRgbToPickerLocationAndIndex(rgb255: Array<Number>): `T$16`
    open fun updateColorCircle(ctx: CanvasRenderingContext2D)
    open fun updateColorCircleProgress(ctx: CanvasRenderingContext2D)
    open fun updatePickerMarker(ctx: CanvasRenderingContext2D)
    open fun calcCanvasLocationFromPickerLocation(canvas: HTMLCanvasElement, pickerLocation: `T$15`): Array<Number>
    open fun calcPickerLocationFromCanvasLocation(canvas: HTMLCanvasElement, cartesian: Array<Number>): `T$17`
    open fun handleValueValidate()
    open fun handleValue(event: Event)
    open fun syncValueWithUi()
    open fun syncValueUiWithValue()

    companion object {
        fun createDefaultPickerMarker(lineWidth: Any): (ctx: CanvasRenderingContext2D, x: Number, y: Number, r: Number, i: Number) -> Unit
        fun calcRecognizableGrayIndex(grayIndex: Any): Number
    }
}
