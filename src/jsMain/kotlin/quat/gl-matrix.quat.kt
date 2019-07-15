@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@file:JsQualifier("quat")
package quat

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
external fun identity(out: GLM.IArray): GLM.IArray = definedExternally
external fun setAxisAngle(out: GLM.IArray, axis: GLM.IArray, rad: Number): GLM.IArray = definedExternally
external fun add(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun multiply(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun mul(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun scale(out: GLM.IArray, a: GLM.IArray, b: Number): GLM.IArray = definedExternally
external fun length(a: GLM.IArray): Number = definedExternally
external fun len(a: GLM.IArray): Number = definedExternally
external fun squaredLength(a: GLM.IArray): Number = definedExternally
external fun sqrLen(a: GLM.IArray): Number = definedExternally
external fun normalize(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun dot(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): Number = definedExternally
external fun lerp(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray, t: Number): GLM.IArray = definedExternally
external fun slerp(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray, t: Number): GLM.IArray = definedExternally
external fun invert(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun conjugate(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun str(a: GLM.IArray): String = definedExternally
external fun rotateX(out: GLM.IArray, a: GLM.IArray, rad: Number): GLM.IArray = definedExternally
external fun rotateY(out: GLM.IArray, a: GLM.IArray, rad: Number): GLM.IArray = definedExternally
external fun rotateZ(out: GLM.IArray, a: GLM.IArray, rad: Number): GLM.IArray = definedExternally
external fun fromMat3(out: GLM.IArray, m: GLM.IArray): GLM.IArray = definedExternally
external fun setAxes(out: GLM.IArray, view: GLM.IArray, right: GLM.IArray, up: GLM.IArray): GLM.IArray = definedExternally
external fun rotationTo(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun calculateW(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
