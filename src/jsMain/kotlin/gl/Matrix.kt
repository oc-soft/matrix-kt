package gl

import org.khronos.webgl.Float32Array
import org.khronos.webgl.Float64Array
import org.khronos.webgl.set
import org.khronos.webgl.get

/**
 * manage open gl matrix
 */
class Matrix {
    companion object {
        /**
         * project vertex to window coordinate
         */
        fun project(
            glrs: glrs.InitOutput,
            x: Float, y: Float, z: Float,
            model: FloatArray,
            proj: FloatArray,
            view: IntArray): FloatArray? {
            val res = project(
                glrs,
                x.toDouble(), y.toDouble(), z.toDouble(),
                Float64Array(
                    Array<Double>(model.size) { model[it].toDouble() }),
                Float64Array(
                    Array<Double>(proj.size) { proj[it].toDouble() }),
                view)
            var result: FloatArray? = null
            if (res != null) {
                result = FloatArray(res.length) { res[it].toFloat() }
            }
            return result 
        }
        /**
         * project vertex to window coordinate
         */
        fun project(
            glrs: glrs.InitOutput,
            x: Float, y: Float, z: Float,
            model: Float32Array,
            proj: Float32Array,
            view: IntArray): Float32Array? {
            val res = project(
                glrs,
                x.toDouble(), y.toDouble(), z.toDouble(),
                Float64Array(
                    Array<Double>(model.length) { model[it].toDouble() }),
                Float64Array(
                    Array<Double>(proj.length) { proj[it].toDouble() }),
                view)
            var result: Float32Array? = null
            if (res != null) {
                result = Float32Array(
                    Array<Float>(res.length) { res[it].toFloat() })
            }
            return result 
        }
        /**
         * project vertex to window coordinate
         */
        fun project(
            glrs: glrs.InitOutput,
            x: Double, y: Double, z: Double,
            model: DoubleArray,
            proj: DoubleArray,
            view: IntArray): DoubleArray? {

            val res = project(
                glrs,
                x, y, z,
                Float64Array(Array<Double>(model.size) { model[it] }),
                Float64Array(Array<Double>(proj.size) { proj[it] }),
                view)

            var result: DoubleArray? = null
            if (res != null) {
                result = DoubleArray(res.length) { res[it] }
            }          
            return result
        }
        /**
         * project vertex to window coordinate
         */
        fun project(
            glrs: glrs.InitOutput,
            x: Double, y: Double, z: Double,
            model: Float64Array,
            proj: Float64Array,
            view: IntArray): Float64Array? {
            val matRef = glrs.matrix_create_with_components_col_order(model)
            val projMatRef = glrs.matrix_create_with_components_col_order(proj)

            var result: Float64Array? = null
            if (matRef != null && projMatRef != null) {
                var bState = glrs.matrix_multiply_mut(matRef, projMatRef) 
                if (bState != null && bState) {
                    val coord0 = Float64Array(4)    
                    coord0[0] = x
                    coord0[1] = y
                    coord0[2] = z
                    coord0[3] = 1.0 
                    result = glrs.matrix_apply_r_64(matRef, coord0)
                      
                } 
            } 
            if (matRef != null) {
                glrs.matrix_release(matRef)
            } 
            if (projMatRef != null) {
                glrs.matrix_release(projMatRef)
            }
            return result
        }
 
        /**
         * unproject window coordinate to vertex coordinate
         */
        fun unproject(
            glrs: glrs.InitOutput,
            xw: Float, yw: Float, zw: Float,
            model: FloatArray,
            proj: FloatArray,
            view: IntArray): FloatArray? {
            val res = unproject(
                glrs,
                xw.toDouble(), yw.toDouble(), zw.toDouble(),
                DoubleArray(model.size) { model[it].toDouble() },
                DoubleArray(proj.size) { proj[it].toDouble() },
                view)
            var result: FloatArray? = null
            if (res != null) {
                result = FloatArray(res.size) { res[it].toFloat() } 
            }
            return result
        }

        /**
         * unproject window coordinate to vertex coordinate
         */
        fun unproject(
            glrs: glrs.InitOutput,
            xw: Float, yw: Float, zw: Float,
            model: Float32Array,
            proj: Float32Array,
            view: IntArray): Float32Array? {
            val res = unproject(
                glrs,
                xw.toDouble(), yw.toDouble(), zw.toDouble(),
                Float64Array(
                    Array<Double>(model.length) { model[it].toDouble() }),
                Float64Array(
                    Array<Double>(proj.length) { proj[it].toDouble() }),
                view)
            var result: Float32Array? = null
            if (res != null) {
                result = Float32Array(
                    Array<Float>(res.length) { res[it].toFloat() }) 
            }
            return result
        }


        /**
         * unproject window coordinate to vertex coordinate
         */
        fun unproject(
            glrs: glrs.InitOutput,
            xw: Double, yw: Double, zw: Double,
            model: DoubleArray,
            proj: DoubleArray,
            view: IntArray): DoubleArray? {
            val res = unproject(
                glrs,
                xw, yw, zw,
                Float64Array(Array<Double>(model.size) { model[it] }),
                Float64Array(Array<Double>(proj.size) { proj[it] }),
                view)

            var result: DoubleArray? = null
            if (res != null) {
                result = DoubleArray(res.length) { res[it] }
            }
            return result
        }
        /**
         * unproject window coordinate to vertex coordinate
         */
        fun unproject(
            glrs: glrs.InitOutput,
            xw: Double, yw: Double, zw: Double,
            model: Float64Array,
            proj: Float64Array,
            view: IntArray): Float64Array? {
            val matRef = glrs.matrix_create_with_components_col_order(model)
            val projMatRef = glrs.matrix_create_with_components_col_order(proj)

            var result: Float64Array? = null
            if (matRef != null && projMatRef != null) {
                var bState = glrs.matrix_multiply_mut(matRef, projMatRef) 
                if (bState != null && bState) {
                    val invMatRef = glrs.matrix_inverse(matRef)
                    if (invMatRef != null) {
                        val coord0 = Float64Array( 
                            arrayOf(
                                2.0 * (xw - view[0].toDouble())
                                    / view[2].toDouble() - 1.0,
                                2.0 * (yw - view[1].toDouble())
                                    / view[3].toDouble() - 1.0,
                                2.0 * zw - 1.0,
                                1.0))
                        result = glrs.matrix_apply_r_64(invMatRef, coord0) 
                        glrs.matrix_release(invMatRef)
                    }
                } 
            } 
            if (matRef != null) {
                glrs.matrix_release(matRef)
            } 
            if (projMatRef != null) {
                glrs.matrix_release(projMatRef)
            }
            return result
        }

        /**
         * create unproject matrix
         */
        fun createUnprojectMatrix(
            glrs: glrs.InitOutput,
            modelMatrix: Float64Array,
            projectionMatrix: Float64Array): Float64Array? {
            val matRef = glrs.matrix_create_with_components_col_order(
                modelMatrix)
            val projMatRef = glrs.matrix_create_with_components_col_order(
                projectionMatrix)

            var result: Float64Array? = null
            if (matRef != null && projMatRef != null) {
                var bState = glrs.matrix_multiply_mut(matRef, projMatRef)
                if (bState != null && bState) {
                    val invMatRef = glrs.matrix_inverse(matRef)
                    if (invMatRef != null) {
                        result = glrs.matrix_get_components_col_order(invMatRef)
                    }
                    if (invMatRef != null) {
                        glrs.matrix_release(invMatRef)
                    } 
                }
            }
            if (projMatRef != null) {
                glrs.matrix_release(projMatRef)
            }
            if (matRef != null) {
                glrs.matrix_release(matRef)
            }
            return result
        }

        /**
         * create project matrix
         */
        fun createProjectMatrix(
            glrs: glrs.InitOutput,
            modelMatrix: Float64Array,
            projectionMatrix: Float64Array): Float64Array? {
            val matRef = glrs.matrix_create_with_components_col_order(
                modelMatrix)
            val projMatRef = glrs.matrix_create_with_components_col_order(
                projectionMatrix)

            var result: Float64Array? = null
            if (matRef != null && projMatRef != null) {
                var bState = glrs.matrix_multiply_mut(matRef, projMatRef)
                if (bState != null && bState) {
                    result = glrs.matrix_get_components_col_order(matRef)
                }
            }
            if (projMatRef != null) {
                glrs.matrix_release(projMatRef)
            }
            if (matRef != null) {
                glrs.matrix_release(matRef)
            }
            return result
        }
    }
}

// vi: se ts=4 sw=4 et:
