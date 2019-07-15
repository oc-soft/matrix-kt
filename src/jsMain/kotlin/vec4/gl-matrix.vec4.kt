@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@file:JsQualifier("vec4")
package vec4

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

external fun create(): GLM.IArray = definedExternally
external fun clone(a: GLM.IArray): GLM.IArray = definedExternally
external fun fromValues(x: Number, y: Number, z: Number, w: Number): GLM.IArray = definedExternally
external fun copy(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun set(out: GLM.IArray, x: Number, y: Number, z: Number, w: Number): GLM.IArray = definedExternally
external fun add(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun subtract(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun sub(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun multiply(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun mul(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun divide(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun div(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun min(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun max(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun scale(out: GLM.IArray, a: GLM.IArray, b: Number): GLM.IArray = definedExternally
external fun scaleAndAdd(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray, scale: Number): GLM.IArray = definedExternally
external fun distance(a: GLM.IArray, b: GLM.IArray): Number = definedExternally
external fun dist(a: GLM.IArray, b: GLM.IArray): Number = definedExternally
external fun squaredDistance(a: GLM.IArray, b: GLM.IArray): Number = definedExternally
external fun sqrDist(a: GLM.IArray, b: GLM.IArray): Number = definedExternally
external fun length(a: GLM.IArray): Number = definedExternally
external fun len(a: GLM.IArray): Number = definedExternally
external fun squaredLength(a: GLM.IArray): Number = definedExternally
external fun sqrLen(a: GLM.IArray): Number = definedExternally
external fun negate(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun inverse(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun normalize(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun dot(a: GLM.IArray, b: GLM.IArray): Number = definedExternally
external fun lerp(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray, t: Number): GLM.IArray = definedExternally
external fun random(out: GLM.IArray): GLM.IArray = definedExternally
external fun random(out: GLM.IArray, scale: Number): GLM.IArray = definedExternally
external fun transformMat4(out: GLM.IArray, a: GLM.IArray, mat: GLM.IArray): GLM.IArray = definedExternally
external fun transformQuat(out: GLM.IArray, a: GLM.IArray, quat: GLM.IArray): GLM.IArray = definedExternally
external fun forEach(out: GLM.IArray, string: Number, offset: Number, count: Number, callback: (a: GLM.IArray, b: GLM.IArray, arg: Any) -> Unit, arg: Any): GLM.IArray = definedExternally
external fun forEach(out: GLM.IArray, string: Number, offset: Number, count: Number, callback: (a: GLM.IArray, b: GLM.IArray) -> Unit): GLM.IArray = definedExternally
external fun str(a: GLM.IArray): String = definedExternally
