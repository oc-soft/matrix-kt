@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "NESTED_CLASS_IN_EXTERNAL_INTERFACE")
@file:JsQualifier("mat3")
package mat3

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
external fun copy(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun identity(out: GLM.IArray): GLM.IArray = definedExternally
external fun transpose(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun invert(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun adjoint(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun determinant(a: GLM.IArray): Number = definedExternally
external fun multiply(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun mul(out: GLM.IArray, a: GLM.IArray, b: GLM.IArray): GLM.IArray = definedExternally
external fun str(mat: GLM.IArray): String = definedExternally
external fun frob(a: GLM.IArray): Number = definedExternally
external fun normalFromMat4(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun fromQuat(out: GLM.IArray, q: GLM.IArray): GLM.IArray = definedExternally
external fun fromMat4(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun scale(out: GLM.IArray, a: GLM.IArray, v: GLM.IArray): GLM.IArray = definedExternally
external fun fromMat2d(out: GLM.IArray, a: GLM.IArray): GLM.IArray = definedExternally
external fun translate(out: GLM.IArray, a: GLM.IArray, v: GLM.IArray): GLM.IArray = definedExternally
external fun rotate(out: GLM.IArray, a: GLM.IArray, rad: Number): GLM.IArray = definedExternally
