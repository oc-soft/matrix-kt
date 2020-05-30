@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
@file:JsModule("glrs")
@file:JsNonModule
package glrs
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
import tsstdlib.WebAssembly.Memory
import tsstdlib.WebAssembly.Module

external fun matrix_create_with_components_row_order(components: Float64Array): Number

external fun matrix_create_with_components_col_order(components: Float64Array): Number

external fun matrix_create_with_dimension(dim: Number): Number

external fun matrix_retain(mat: Number): Number

external fun matrix_release(mat: Number): Number

external fun matrix_get_components_col_order(mat: Number): Float64Array?

external fun matrix_get_components_col_order_32(mat: Number): Float32Array?

external fun matrix_get_components_row_order(mat: Number): Float64Array?

external fun matrix_get_components_row_order_32(mat: Number): Float32Array?

external fun matrix_multiply_mut(mat1: Number, mat2: Number): Boolean?

external fun matrix_multiply(mat1: Number, mat2: Number): Number

external fun matrix_inverse(mat: Number): Number

external fun matrix_scale_mut(mat: Number, scale: Number): Boolean

external fun matrix_scale(mat: Number, scale: Number): Number

external fun matrix_apply_l_with_vec(mat: Number, v: Number): Number

external fun matrix_apply_r_with_vec(mat: Number, v: Number): Number

external fun matrix_apply_l_64(mat: Number, v: Float64Array): Float64Array?

external fun matrix_apply_l_32(mat: Number, v: Float32Array): Float32Array?

external fun matrix_apply_r_64(mat: Number, v: Float64Array): Float64Array?

external fun matrix_apply_r_32(mat: Number, v: Float32Array): Float32Array?

external fun matrix_new_cross_product(x: Number, y: Number, z: Number): Number

external fun matrix_new_axis_rotation(theta: Number, x: Number, y: Number, z: Number): Number

external fun matrix_rotate_mut(mat: Number, theta: Number, x: Number, y: Number, z: Number): Boolean

external fun matrix_rotate(mat: Number, theta: Number, x: Number, y: Number, z: Number): Number

external fun matrix_translate_mut(mat: Number, x: Number, y: Number, z: Number): Boolean

external fun matrix_translate(mat: Number, x: Number, y: Number, z: Number): Number

external fun matrix_scale3_mut(mat: Number, x: Number, y: Number, z: Number): Boolean

external fun matrix_scale3(mat: Number, x: Number, y: Number, z: Number): Number

external fun matrix_new_flustum(l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Number

external fun matrix_frustum_mut(mat: Number, l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Boolean

external fun matrix_frustum(mat: Number, l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Number

external fun matrix_new_ortho(l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Number

external fun matrix_ortho_mut(mat: Number, l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Boolean

external fun matrix_ortho(mat: Number, l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Number

external fun matrix_new_perspective(fovy: Number, aspect: Number, z_near: Number, z_far: Number): Number

external fun matrix_perspective_mut(mat: Number, fovy: Number, aspect: Number, z_near: Number, z_far: Number): Boolean

external fun matrix_perspective(mat: Number, fovy: Number, aspect: Number, z_near: Number, z_far: Number): Number

external fun matrix_new_look_at(eye_x: Number, eye_y: Number, eye_z: Number, center_x: Number, center_y: Number, center_z: Number, up_x: Number, up_y: Number, up_z: Number): Number

external fun matrix_look_at_mut(mat: Number, eye_x: Number, eye_y: Number, eye_z: Number, center_x: Number, center_y: Number, center_z: Number, up_x: Number, up_y: Number, up_z: Number): Boolean

external fun matrix_look_at(mat: Number, eye_x: Number, eye_y: Number, eye_z: Number, center_x: Number, center_y: Number, center_z: Number, up_x: Number, up_y: Number, up_z: Number): Number

external fun distances_retain(distances: Number): Number

external fun distances_release(distances: Number): Number

external fun distances_size(distances: Number): Number

external fun distances_get(distances: Number, idx: Number): Number

external fun distance_create(dis: Number): Number

external fun distance_retain(distance: Number): Number

external fun distance_release(distance: Number): Number

external fun distance_get_value(distance: Number): Number?

external fun distance_get_abs_value(distance: Number): Number?

external fun distance_indices_retain(di: Number): Number

external fun distance_indices_release(di: Number): Number

external fun distance_indices_get_distances(di: Number): Number

external fun distance_indices_get_indices(di: Number, distance: Number): Uint32Array?

external fun plane_create_0(n: Number, c: Number): Number

external fun plane_create(n: Float64Array, c: Float64Array): Number

external fun plane_retain(p: Number): Number

external fun plane_release(p: Number): Number

external fun plane_get_dimension(p: Number): Number?

external fun plane_distance_0(p: Number, v: Number): Number?

external fun plane_distance(p: Number, v: Float64Array): Number?

external fun plane_sort_points_0(p: Number, va: Number): Number

external fun plane_sort_points(p: Number, point_container: Any): Number

external fun vector_create(components: Float64Array): Number

external fun vector_get_components(v: Number): Float64Array?

external fun vector_get_components_32(v: Number): Float32Array?

external fun vector_get_component(v: Number, idx: Number): Number?

external fun vector_retain(v: Number): Number

external fun vector_release(v: Number): Number

external fun vector_dimension(v: Number): Number

external fun vector_array_create(): Number

external fun vector_array_retain(v: Number): Number

external fun vector_array_release(v: Number): Number

external fun vector_array_dimension(v: Number): Number?

external fun vector_array_size(v: Number): Number?

external fun vector_array_add_0(va: Number, v: Number): Boolean

external fun vector_array_add_1(va: Number, v: Float64Array): Boolean

external interface InitOutput {
    var memory: Memory
    var matrix_create_with_components_row_order: (a: Number, b: Number) -> Number
    var matrix_create_with_components_col_order: (a: Number, b: Number) -> Number
    var matrix_create_with_dimension: (a: Number) -> Number
    var matrix_retain: (a: Number) -> Number
    var matrix_release: (a: Number) -> Number
    var matrix_get_components_col_order: (a: Number) -> Number
    var matrix_get_components_col_order_32: (a: Number) -> Number
    var matrix_get_components_row_order: (a: Number) -> Number
    var matrix_get_components_row_order_32: (a: Number) -> Number
    var matrix_multiply_mut: (a: Number, b: Number) -> Number
    var matrix_multiply: (a: Number, b: Number) -> Number
    var matrix_inverse: (a: Number) -> Number
    var matrix_scale_mut: (a: Number, b: Number) -> Number
    var matrix_scale: (a: Number, b: Number) -> Number
    var matrix_apply_l_with_vec: (a: Number, b: Number) -> Number
    var matrix_apply_r_with_vec: (a: Number, b: Number) -> Number
    var matrix_apply_l_64: (a: Number, b: Number) -> Number
    var matrix_apply_l_32: (a: Number, b: Number) -> Number
    var matrix_apply_r_64: (a: Number, b: Number) -> Number
    var matrix_apply_r_32: (a: Number, b: Number) -> Number
    var matrix_new_cross_product: (a: Number, b: Number, c: Number) -> Number
    var matrix_new_axis_rotation: (a: Number, b: Number, c: Number, d: Number) -> Number
    var matrix_rotate_mut: (a: Number, b: Number, c: Number, d: Number, e: Number) -> Number
    var matrix_rotate: (a: Number, b: Number, c: Number, d: Number, e: Number) -> Number
    var matrix_translate_mut: (a: Number, b: Number, c: Number, d: Number) -> Number
    var matrix_translate: (a: Number, b: Number, c: Number, d: Number) -> Number
    var matrix_scale3_mut: (a: Number, b: Number, c: Number, d: Number) -> Number
    var matrix_scale3: (a: Number, b: Number, c: Number, d: Number) -> Number
    var matrix_new_flustum: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number) -> Number
    var matrix_frustum_mut: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number, g: Number) -> Number
    var matrix_frustum: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number, g: Number) -> Number
    var matrix_new_ortho: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number) -> Number
    var matrix_ortho_mut: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number, g: Number) -> Number
    var matrix_ortho: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number, g: Number) -> Number
    var matrix_new_perspective: (a: Number, b: Number, c: Number, d: Number) -> Number
    var matrix_perspective_mut: (a: Number, b: Number, c: Number, d: Number, e: Number) -> Number
    var matrix_perspective: (a: Number, b: Number, c: Number, d: Number, e: Number) -> Number
    var matrix_new_look_at: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number, g: Number, h: Number, i: Number) -> Number
    var matrix_look_at_mut: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number, g: Number, h: Number, i: Number, j: Number) -> Number
    var matrix_look_at: (a: Number, b: Number, c: Number, d: Number, e: Number, f: Number, g: Number, h: Number, i: Number, j: Number) -> Number
    var distances_retain: (a: Number) -> Number
    var distances_release: (a: Number) -> Number
    var distances_size: (a: Number) -> Number
    var distances_get: (a: Number, b: Number) -> Number
    var distance_create: (a: Number) -> Number
    var distance_retain: (a: Number) -> Number
    var distance_release: (a: Number) -> Number
    var distance_get_value: (a: Number, b: Number) -> Unit
    var distance_get_abs_value: (a: Number, b: Number) -> Unit
    var distance_indices_retain: (a: Number) -> Number
    var distance_indices_release: (a: Number) -> Number
    var distance_indices_get_distances: (a: Number) -> Number
    var distance_indices_get_indices: (a: Number, b: Number) -> Number
    var plane_create_0: (a: Number, b: Number) -> Number
    var plane_create: (a: Number, b: Number) -> Number
    var plane_retain: (a: Number) -> Number
    var plane_release: (a: Number) -> Number
    var plane_get_dimension: (a: Number, b: Number) -> Unit
    var plane_distance_0: (a: Number, b: Number, c: Number) -> Unit
    var plane_distance: (a: Number, b: Number, c: Number) -> Unit
    var plane_sort_points_0: (a: Number, b: Number) -> Number
    var plane_sort_points: (a: Number, b: Number) -> Number
    var vector_create: (a: Number) -> Number
    var vector_get_components: (a: Number) -> Number
    var vector_get_components_32: (a: Number) -> Number
    var vector_get_component: (a: Number, b: Number, c: Number) -> Unit
    var vector_retain: (a: Number) -> Number
    var vector_release: (a: Number) -> Number
    var vector_dimension: (a: Number) -> Number
    var vector_array_create: () -> Number
    var vector_array_retain: (a: Number) -> Number
    var vector_array_release: (a: Number) -> Number
    var vector_array_dimension: (a: Number, b: Number) -> Unit
    var vector_array_size: (a: Number, b: Number) -> Unit
    var vector_array_add_0: (a: Number, b: Number) -> Number
    var vector_array_add_1: (a: Number, b: Number) -> Number
    var __wbindgen_malloc: (a: Number) -> Number
}

@JsName("default")
external fun init(module_or_path: Request = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: String = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: URL = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: Response = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: ArrayBufferView = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: ArrayBuffer = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: Module = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(module_or_path: Promise<dynamic /* Request | String | URL | Response | ArrayBufferView | ArrayBuffer | WebAssembly.Module */> = definedExternally): Promise<InitOutput>

@JsName("default")
external fun init(): Promise<InitOutput>
