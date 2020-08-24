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

external interface `T$0` {
    var value: String
    var colorCircleCanvas: String
}

external open class UI(template: String = definedExternally, classMapping: `T$0` = definedExternally, indexValue: Number = definedExternally, colorType: String = definedExternally) {
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
    open var template: Any?
    open fun bind(rootElement: Any)
    open fun unbind()
    open fun addEventListener(type: Any, listener: Any)
    open fun removeEventListener(type: Any, listener: Any)
    open fun notify(type: Any)
    open fun bindValue(rootElement: Any)
    open fun unbindValue(rootElement: Any)
    open fun bindColorCircle(rootElement: Any)
    open fun unbindColorCircle(rootElement: Any)
    open fun postHandleClickInColorCircle(event: Any)
    open fun handleClickInColorCircle(event: Any)
    open fun getColorCircleLocRadius(colorCanvas: Any): Any
    open fun postUpdateColorCircleCanvas()
    open fun updateColorCircleCanvas(rootElement: Any)
    open fun convertPickerLocToRgb(pickerLoc: Any): Any
    open fun convertRgbToPickerLocationAndIndex(rgb255: Any): Any
    open fun updateColorCircle(ctx: Any)
    open fun updateColorCircleProgress(ctx: Any)
    open fun updatePickerMarker(ctx: Any)
    open fun calcCanvasLocationFromPickerLocation(canvas: Any, pickerLocation: Any): Any
    open fun calcPickerLocationFromCanvasLocation(canvas: Any, cartesian: Any): Any
    open fun handleValueValidate()
    open fun handleValue(event: Any)
    open fun syncValueWithUi()
    open fun syncValueUiWithValue()

    companion object {
        fun createDefaultPickerMarker(lineWidth: Any): (ctx: Any, x: Any, y: Any, r: Any, i: Any) -> Unit
        fun calcRecognizableGrayIndex(grayIndex: Any): Any
    }
}
