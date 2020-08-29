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
    var red: Number
    var green: Number
    var blue: Number
}

external interface `T$1` {
    var value: Number
    var maxIndex: Number
    var minIndex: Number
}

external interface `T$2` {
    var maxIndex: Number
    var value: Number
}

external interface `T$3` {
    var chroma: `T$1`
    var hue: Number
}

external interface `T$4` {
    var alpha: Number
    var beta: Number
    var hue2: Number
    var chroma2: Number
}

external interface `T$5` {
    var hue2: Number
    var chroma2: Number
    var v: Number
    var saturation: Number
}

external interface `T$6` {
    var hue2: Number
    var chroma2: Number
    var l: Number
    var saturation: Number
}

external interface `T$7` {
    var value: Number
    var type: String
}

external interface `T$8` {
    var rgb: Array<dynamic /* Number | Any */>
    var row: Number
    var col: Number
}

external interface `T$9` {
    var rgb: Array<Number>
    var row: Number
    var col: Number
    var calcRowColumnIndex: (index: Number) -> Number
}

external interface `T$10` {
    var state: `T$9`
    var curRange: Array<Number>
}

external interface `T$11` {
    var start: (progress: (state: `T$9`, curRange: Array<Number>) -> Unit) -> Promise<`T$10`>
}

external open class RgbHs {
    companion object {
        fun rgbToHex(rgb: Array<Number>): Number
        fun hexToRgb(rgb: Number): Array<Number>
        fun toRgbArray(rgb: `T$0`): Array<Number>
        fun findIndex(rgb: Array<Number>, comparator: (a: Number, b: Number) -> Number): Number
        fun findMaxIndex(rgb: Array<Number>): Number
        fun findMinIndex(rgb: Array<Number>): Number
        fun calcChroma(rgb: Array<Number>): `T$1`
        fun calcHue(rgb: Array<Number>, chroma: `T$2`): Number
        fun calcHueChroma(rgb: Array<Number>): `T$3`
        fun calcHueChroma2(rgb: Array<Number>): `T$4`
        fun luma(rgb: Array<Number>, coefficients: Array<Number>): Number
        fun lumaY709(rgb: Array<Number>): Number
        fun lumaY601(rgb: Array<Number>): Number
        fun rgbMaxValue(rgb: Array<Number>): Number
        fun rgbMinValue(rgb: Array<Number>): Number
        fun rgbMinMaxAverage(rgb: Array<Number>): Number
        fun rgbAverage(rgb: Array<Number>): Number
        fun rgb255ToRgb1(rgb: `T$0`): `T$0`
        fun rgbToHsv2(rgb: `T$0`): `T$5`
        fun rgbToHsl2(rgb: `T$0`): `T$6`
        fun calcChromaRatioFromHue(hue: Number): Number
        fun xyrvToRgb(x: Number, y: Number, r: Number, v: Number, vToColorValue: (v: Number, chroma: Number, rgb: Array<Number>) -> Number): Array<Number>?
        fun hueChromaToRgb(hue: Number, chroma: Number, v: Number, vToColorValue: (v: Number, chroma: Number, rgb: Array<Number>) -> Number): Array<Number>
        fun vToColorValue(v: Number, chroma: Number, rgbTempValue: Array<Number>): Number
        fun lToColorValue(l: Number, chroma: Number, rgbTempValue: Array<Number>): Number
        fun lumaY709ToColorValue(y709: Number, chroma: Number, rgbTempValue: Array<Number>): Number
        fun lumaY601ToColorValue(y601: Number, chroma: Number, rgbTempValue: Array<Number>): Number
        fun isInCircle(x: Number, y: Number, radius: Number): Boolean
        fun createColorCircle(radius: Number, indexValue: `T$7`, notCircleValue: Any): `T$8`
        fun createColorCircleProgress(radius: Number, indexValue: `T$7`, notCircleValue: Any): `T$11`
    }
}