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

external interface `T$4` {
    var value: Number
    var maxIndex: Any
    var minIndex: Any
}

external interface `T$5` {
    var chroma: `T$4`
    var hue: Any
}

external interface `T$6` {
    var alpha: Number
    var beta: Number
    var hue2: Number
    var chroma2: Number
}

external interface `T$7` {
    var red: Number
    var green: Number
    var blue: Number
}

external interface `T$8` {
    var hue2: Any
    var chroma2: Any
    var v: Any
    var saturation: Any
}

external interface `T$9` {
    var hue2: Number
    var chroma2: Number
    var l: Number
    var saturation: Any
}

external interface `T$10` {
    var rgb: Array<Any>
    var row: Number
    var col: Number
}

external interface `T$11` {
    var start: (progress: Any) -> Promise<Any>
}

external open class RgbHs {
    companion object {
        fun rgbToHex(rgb: Any): Any
        fun hexToRgb(rgb: Any): Any
        fun toRgbArray(rgb: Any): Array<Any>
        fun findIndex(rgb: Any, comparator: Any): Any
        fun findMaxIndex(rgb: Any): Any
        fun findMinIndex(rgb: Any): Any
        fun calcChroma(rgb: Any): `T$4`
        fun calcHue(rgb: Any, chroma: Any): Any
        fun calcHueChroma(rgb: Any): `T$5`
        fun calcHueChroma2(rgb: Any): `T$6`
        fun luma(rgb: Any, coefficients: Any): Any
        fun lumaY709(rgb: Any): Any
        fun lumaY601(rgb: Any): Any
        fun rgbMaxValue(rgb: Any): Any
        fun rgbMinValue(rgb: Any): Any
        fun rgbMinMaxAverage(rgb: Any): Number
        fun rgbAverage(rgb: Any): Number
        fun rgb255ToRgb1(rgb: Any): `T$7`
        fun rgbToHsv2(rgb: Any): `T$8`
        fun rgbToHsl2(rgb: Any): `T$9`
        fun calcChromaRatioFromHue(hue: Any): Any
        fun xyrvToRgb(x: Any, y: Any, r: Any, v: Any, vToColorValue: Any): Any
        fun hueChromaToRgb(hue: Any, chroma: Any, v: Any, vToColorValue: Any): Array<Number>
        fun vToColorValue(v: Any, chroma: Any, rgbTempValue: Any): Number
        fun lToColorValue(l: Any, chroma: Any, rgbTempValue: Any): Number
        fun lumaY709ToColorValue(y709: Any, chroma: Any, rgbTempValue: Any): Number
        fun lumaY601ToColorValue(y601: Any, chroma: Any, rgbTempValue: Any): Number
        fun isInCircle(x: Any, y: Any, radius: Any): Boolean
        fun createColorCircle(radius: Any, indexValue: Any, notCircleValue: Any): `T$10`
        fun createColorCircleProgress(radius: Any, indexValue: Any, notCircleValue: Any): `T$11`
    }
}
