@file:JsModule("glrs")
@file:JsNonModule
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION")
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
import glrs.WebAssembly.Module

external interface InitOutput {
    fun vector_array_create(): Number
    fun vector_array_from_js_array(array: Array<Any>): Number
    fun vector_array_retain(v: Number): Number
    fun vector_array_release(v: Number): Number
    fun vector_array_dimension(v: Number): Number?
    fun vector_array_size(v: Number): Number?
    fun vector_array_add_0(va: Number, v: Number): Boolean
    fun vector_array_add_1(va: Number, v: Float64Array): Boolean
    fun vector_array_get_components_as_array_array64(va: Number): Array<Any>?
    fun vector_array_get_components_as_array_array32(va: Number): Array<Any>?
    fun float_vec_retain(vec: Number): Number
    fun float_vec_release(vec: Number): Number
    fun float_vec_size(vec: Number): Number
    fun float_vec_get(vec: Number, idx: Number): Number
    fun vector_create(components: Float64Array): Number
    fun vector_get_components(v: Number): Float64Array?
    fun vector_get_components_32(v: Number): Float32Array?
    fun vector_get_component(v: Number, idx: Number): Number?
    fun vector_retain(v: Number): Number
    fun vector_release(v: Number): Number
    fun vector_dimension(v: Number): Number
    fun plane_create_0(n: Number, c: Number): Number
    fun plane_create_with_2d_0(p1: Number, p2: Number): Number
    fun plane_create(n: Float64Array, c: Float64Array): Number
    fun plane_create_with_2d(p1: Float64Array, p2: Float64Array): Number
    fun plane_retain(p: Number): Number
    fun plane_release(p: Number): Number
    fun plane_get_dimension(p: Number): Number?
    fun plane_distance_0(p: Number, v: Number): Number?
    fun plane_distance(p: Number, v: Float64Array): Number?
    fun plane_project_0(p: Number, v: Number): Float64Array?
    fun plane_project(p: Number, v: Float64Array): Float64Array?
    fun plane_sort_points_0_i(p: Number, va: Number): Number
    fun plane_sort_points_0(p: Number, point_container: Any): Number
    fun plane_sort_points_1_i(p: Number, va: Number): Number
    fun plane_sort_points_1(p: Number, point_container: Any): Number
    fun float_indices_retain(fi: Number): Number
    fun float_indices_release(fi: Number): Number
    fun float_indices_get_float_keys(fi: Number): Number
    fun float_indices_get_indices(fi: Number, float_obj: Number): Uint32Array?
    fun float_create(value: Number): Number
    fun float_retain(float_obj: Number): Number
    fun float_release(float_obj: Number): Number
    fun float_get_value(float_obj: Number): Number?
    fun distances_retain(distances: Number): Number
    fun distances_release(distances: Number): Number
    fun distances_size(distances: Number): Number
    fun distances_get(distances: Number, idx: Number): Number
    fun distance_create(dis: Number): Number
    fun distance_retain(distance: Number): Number
    fun distance_release(distance: Number): Number
    fun distance_get_value(distance: Number): Number?
    fun distance_get_abs_value(distance: Number): Number?
    fun distance_indices_retain(di: Number): Number
    fun distance_indices_release(di: Number): Number
    fun distance_indices_get_distances(di: Number): Number
    fun distance_indices_get_indices(di: Number, distance: Number): Uint32Array?
    fun geom_d2_offset_points_00(offset: Number, p1: Number, p2: Number, tolerance: Number): Number
    fun geom_d2_offset_points_01(offset: Number, p1: Float64Array, p2: Float64Array, tolerance: Number): Number
    fun geom_d2_offset_points_02(offset: Number, p1: Float64Array, p2: Float64Array, tolerance: Number): Array<Any>?
    fun geom_d2_offset_points_10(offset: Number, p1: Number, p2: Number): Number
    fun geom_d2_offset_points_11(offset: Number, p1: Float64Array, p2: Float64Array): Number
    fun geom_d2_offset_points_12(offset: Number, p1: Float64Array, p2: Float64Array): Array<Any>?
    fun geom_2d_offset_points_vec_00(offset: Number, points: Number, close: Boolean, tolerance: Number): Number
    fun geom_2d_offset_points_vec_01(offset: Number, points: Array<Any>, close: Boolean, tolerance: Number): Number
    fun geom_2d_offset_points_vec_02(offset: Number, points: Array<Any>, close: Boolean, tolerance: Number): Array<Any>?
    fun geom_2d_offset_points_vec_10(offset: Number, points: Number, close: Boolean): Number
    fun geom_2d_offset_points_vec_11(offset: Number, points: Array<Any>, close: Boolean): Number
    fun geom_2d_offset_points_vec_12(offset: Number, points: Array<Any>, close: Boolean): Array<Any>?
    fun segment_retain(seg: Number): Number
    fun segment_release(seg: Number): Number
    fun segment_create_00(d: Number, c: Number, t0: Number, t1: Number): Number
    fun segment_create_01(d: Float64Array, c: Float64Array, t0: Number, t1: Number): Number
    fun segment_create_10(p1: Number, p2: Number): Number
    fun segment_create_11(p1: Float64Array, p2: Float64Array): Number
    fun segment_p1_0(seg: Number): Number
    fun segment_p1(seg: Number): Float64Array?
    fun segment_p2_0(seg: Number): Number
    fun segment_p2(seg: Number): Float64Array?
    fun segment_direction_0(seg: Number): Number
    fun segment_direction(seg: Number): Float64Array?
    fun segment_point_on_t_0(seg: Number, t: Number): Number
    fun segment_point_on_t(seg: Number, t: Number): Float64Array?
    fun segment_get_parameter_range_0(seg: Number): Number
    fun segment_get_parameter_range(seg: Number): Float64Array?
    fun segment_point_on_line_0(seg: Number): Number
    fun segment_point_on_line(seg: Number): Float64Array?
    fun segment_cross_point_parameter_2d_00(seg_1: Number, seg_2: Number, tolerance: Number): Number
    fun segment_cross_point_parameter_2d_01(seg_1: Number, seg_2: Number, tolerance: Number): Float64Array?
    fun segment_cross_point_parameter_2d_10(seg_1: Number, seg_2: Number): Number
    fun segment_cross_point_parameter_2d_11(seg_1: Number, seg_2: Number): Float64Array?
    fun segment_cross_point_parameter_2d_exact_00(seg_1: Number, seg_2: Number, tolerance: Number): Number
    fun segment_cross_point_parameter_2d_exact_01(seg_1: Number, seg_2: Number, tolerance: Number): Float64Array?
    fun segment_cross_point_parameter_2d_exact_10(seg_1: Number, seg_2: Number): Number
    fun segment_cross_point_parameter_2d_exact_11(seg_1: Number, seg_2: Number): Float64Array?
    fun matrix_create_with_components_row_order(components: Float64Array): Number
    fun matrix_create_with_components_col_order(components: Float64Array): Number
    fun matrix_create_with_dimension(dim: Number): Number
    fun matrix_retain(mat: Number): Number
    fun matrix_release(mat: Number): Number
    fun matrix_get_components_col_order(mat: Number): Float64Array?
    fun matrix_get_components_col_order_32(mat: Number): Float32Array?
    fun matrix_get_components_row_order(mat: Number): Float64Array?
    fun matrix_get_components_row_order_32(mat: Number): Float32Array?
    fun matrix_multiply_mut(mat1: Number, mat2: Number): Boolean?
    fun matrix_multiply(mat1: Number, mat2: Number): Number
    fun matrix_inverse(mat: Number): Number
    fun matrix_scale_mut(mat: Number, scale: Number): Boolean
    fun matrix_scale(mat: Number, scale: Number): Number
    fun matrix_apply_l_with_vec(mat: Number, v: Number): Number
    fun matrix_apply_r_with_vec(mat: Number, v: Number): Number
    fun matrix_apply_l_64(mat: Number, v: Float64Array): Float64Array?
    fun matrix_apply_l_32(mat: Number, v: Float32Array): Float32Array?
    fun matrix_apply_r_64(mat: Number, v: Float64Array): Float64Array?
    fun matrix_apply_r_32(mat: Number, v: Float32Array): Float32Array?
    fun matrix_new_cross_product(x: Number, y: Number, z: Number): Number
    fun matrix_new_axis_rotation(theta: Number, x: Number, y: Number, z: Number): Number
    fun matrix_rotate_mut(mat: Number, theta: Number, x: Number, y: Number, z: Number): Boolean
    fun matrix_rotate(mat: Number, theta: Number, x: Number, y: Number, z: Number): Number
    fun matrix_translate_mut(mat: Number, x: Number, y: Number, z: Number): Boolean
    fun matrix_translate(mat: Number, x: Number, y: Number, z: Number): Number
    fun matrix_scale3_mut(mat: Number, x: Number, y: Number, z: Number): Boolean
    fun matrix_scale3(mat: Number, x: Number, y: Number, z: Number): Number
    fun matrix_new_flustum(l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Number
    fun matrix_frustum_mut(mat: Number, l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Boolean
    fun matrix_frustum(mat: Number, l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Number
    fun matrix_new_ortho(l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Number
    fun matrix_ortho_mut(mat: Number, l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Boolean
    fun matrix_ortho(mat: Number, l: Number, r: Number, b: Number, t: Number, n: Number, f: Number): Number
    fun matrix_new_perspective(fovy: Number, aspect: Number, z_near: Number, z_far: Number): Number
    fun matrix_perspective_mut(mat: Number, fovy: Number, aspect: Number, z_near: Number, z_far: Number): Boolean
    fun matrix_perspective(mat: Number, fovy: Number, aspect: Number, z_near: Number, z_far: Number): Number
    fun matrix_new_look_at(eye_x: Number, eye_y: Number, eye_z: Number, center_x: Number, center_y: Number, center_z: Number, up_x: Number, up_y: Number, up_z: Number): Number
    fun matrix_look_at_mut(mat: Number, eye_x: Number, eye_y: Number, eye_z: Number, center_x: Number, center_y: Number, center_z: Number, up_x: Number, up_y: Number, up_z: Number): Boolean
    fun matrix_look_at(mat: Number, eye_x: Number, eye_y: Number, eye_z: Number, center_x: Number, center_y: Number, center_z: Number, up_x: Number, up_y: Number, up_z: Number): Number
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
