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
            val coord0 = Float64Array(1)  
            coord0[0] = x
            coord0[1] = y
            coord0[2] = z
        
            return project(glrs, coord0, model, proj, view)
        }
        /**
         * project vertex to window coordinate
         */
        fun project(
            glrs: glrs.InitOutput,
            coordinates: Float32Array,
            model: Float32Array,
            proj: Float32Array,
            viewport: IntArray): Float32Array? {
            val (projRef, viewportRef) =  createProjectMatrixRef(
                glrs,
                Float64Array(Array<Double>(model.length) 
                    { model[it].toDouble() }), 
                Float64Array(Array<Double>(proj.length)
                    { proj[it].toDouble() }), viewport)
            var result: Float32Array? = null
            if (projRef != null && viewportRef != null) {
                result = Float32Array((coordinates.length / 3) * 3)
                for (idx in 0 .. coordinates.length / 3) { 
                    val coord0 = Float32Array(4)
                    coord0[0] = coordinates[3 * idx]
                    coord0[1] = coordinates[3 * idx + 1]
                    coord0[2] = coordinates[3 * idx + 2]
                    coord0[3] = 1.0f 
                    val tmpCoord0 = glrs.matrix_apply_r_32(projRef, coord0)!!
                    for (idx0 in 0..2) {
                        tmpCoord0[idx0] /= tmpCoord0[3]
                    }

                    tmpCoord0[3] = 1.0f 
                    val tmpCoord1 = glrs.matrix_apply_r_32(viewportRef, 
                        tmpCoord0)!!
                    result[3 * idx] = tmpCoord1[0]
                    result[3 * idx + 1] = tmpCoord1[1] 
                    result[3 * idx + 2] = tmpCoord1[2]
                } 
            } 
            if (projRef != null) {
                glrs.matrix_release(projRef)
            } 
            if (viewportRef != null) {
                glrs.matrix_release(viewportRef)
            }
            return result
        }
        /**
         * project vertex to window coordinate
         */
        fun project(
            glrs: glrs.InitOutput,
            coordinates: Float64Array,
            model: Float64Array,
            proj: Float64Array,
            viewport: IntArray): Float64Array? {
            val (projRef, viewportRef) =  createProjectMatrixRef(
                glrs, model, proj, viewport)
            var result: Float64Array? = null
            if (projRef != null && viewportRef != null) {
                result = Float64Array((coordinates.length / 3) * 3)
                for (idx in 0 .. coordinates.length / 3) { 
                    val coord0 = Float64Array(4)    
                    coord0[0] = coordinates[3 * idx]
                    coord0[1] = coordinates[3 * idx + 1]
                    coord0[2] = coordinates[3 * idx + 2]
                    coord0[3] = 1.0 
                    val tmpCoord0 = glrs.matrix_apply_r_64(projRef, coord0)!!

                    for (idx0 in 0..2) {
                        tmpCoord0[idx0] /= tmpCoord0[3]
                    }

                    tmpCoord0[3] = 1.0 
                    val tmpCoord1 = glrs.matrix_apply_r_64(viewportRef, 
                        tmpCoord0)!!
                    result[3 * idx] = tmpCoord1[0]
                    result[3 * idx + 1] = tmpCoord1[1] 
                    result[3 * idx + 2] = tmpCoord1[2]
                } 
            } 
            if (projRef != null) {
                glrs.matrix_release(projRef)
            } 
            if (viewportRef != null) {
                glrs.matrix_release(viewportRef)
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
            viewport: IntArray): Float64Array? {
              
            return unproject(glrs,
                Float64Array(arrayOf(xw, yw, zw)),
                model, proj, viewport) 
        }
        /**
         * unproject window coordinate to vertex coordinate
         */
        fun unproject(
            glrs: glrs.InitOutput,
            coordinates: Float64Array,
            model: Float64Array,
            proj: Float64Array,
            viewport: IntArray): Float64Array? {

            val (projRef, viewportRef) =  createUnprojectMatrixRef(
                glrs, model, proj, viewport)
            var result: Float64Array? = null
            if (projRef != null && viewportRef != null) {
                result = Float64Array((coordinates.length / 3) * 3)
                for (idx in 0 .. coordinates.length / 3) { 
                    val coord0 = Float64Array(4)    
                    coord0[0] = coordinates[3 * idx]
                    coord0[1] = coordinates[3 * idx + 1]
                    coord0[2] = coordinates[3 * idx + 2]
                    coord0[3] = 1.0 
                    val tmpCoord0 = glrs.matrix_apply_r_64(
                        viewportRef, coord0)!!
                    tmpCoord0[3] = 1.0 
                    val tmpCoord1 = glrs.matrix_apply_r_64(
                        projRef, tmpCoord0)!!

                    result[3 * idx] = tmpCoord1[0] / tmpCoord1[3]
                    result[3 * idx + 1] = tmpCoord1[1] / tmpCoord1[3] 
                    result[3 * idx + 2] = tmpCoord1[2] / tmpCoord1[3]
                } 
            } 
            if (projRef != null) {
                glrs.matrix_release(projRef)
            } 
            if (viewportRef != null) {
                glrs.matrix_release(viewportRef);
            }
            return result
        }
 
        /**
         * create project matrix
         */
        fun createUnprojectMatrix(
            glrs: glrs.InitOutput,
            modelMatrix: Float64Array,
            projectionMatrix: Float64Array,
            viewport: IntArray): Pair<Float64Array?, Float64Array?> {
            val (modelProjInvRef, viewportInvRef) = createUnprojectMatrixRef(
                glrs, modelMatrix, projectionMatrix, viewport)
            var result : Pair<Float64Array?, Float64Array?> = Pair(null, null)
            if (modelProjInvRef != null && viewportInvRef != null) {
                result = Pair(
                    glrs.matrix_get_components_col_order(
                        modelProjInvRef),
                    glrs.matrix_get_components_col_order(
                        viewportInvRef))
            }

            if (modelProjInvRef != null) {
                glrs.matrix_release(modelProjInvRef)
            } 
            if (viewportInvRef != null) {
                glrs.matrix_release(viewportInvRef)
            }
            return result              
        }
 
        /**
         * create unproject matrix
         */
        fun createUnprojectMatrixRef(
            glrs: glrs.InitOutput,
            modelMatrix: Float64Array,
            projectionMatrix: Float64Array,
            viewport: IntArray): Pair<Number?, Number?> {

            val (modelProjRef, viewportRef) = createProjectMatrixRef(
                glrs, modelMatrix, projectionMatrix, viewport)
            var result : Pair<Number?, Number?> = Pair(null, null)
            if (modelProjRef != null && viewportRef != null) {
                var modelProjInv = glrs.matrix_inverse(modelProjRef) 
                var viewportInv = glrs.matrix_inverse(viewportRef)
                if (modelProjInv != 0 && viewportInv != 0) {
                    result = Pair(modelProjInv, viewportInv)  
                    modelProjInv = 0 
                    viewportInv = 0 
                }
                if (modelProjInv != 0) {
                    glrs.matrix_release(modelProjInv)
                }
                if (viewportInv != 0) {
                    glrs.matrix_release(viewportInv)
                }
            }
            if (modelProjRef != null && modelProjRef != 0) {
                glrs.matrix_release(modelProjRef)
            } 
            if (viewportRef != null && viewportRef != 0) {
                glrs.matrix_release(viewportRef)
            }
            return result
        }

        /**
         * create project matrix
         */
        fun createProjectMatrix(
            glrs: glrs.InitOutput,
            modelMatrix: Float64Array,
            projectionMatrix: Float64Array,
            viewport: IntArray): Pair<Float64Array?, Float64Array?> {
            var (modelProjRef, viewportRef) = createProjectMatrixRef(
                glrs, modelMatrix, projectionMatrix, viewport)
            var result : Pair<Float64Array?, Float64Array?> = Pair(null, null)
            if (modelProjRef != null && viewportRef != null) {
                result = Pair(
                    glrs.matrix_get_components_col_order(
                        modelProjRef),
                    glrs.matrix_get_components_col_order(
                        viewportRef))
            }

            if (modelProjRef != null) {
                glrs.matrix_release(modelProjRef)
            } 
            if (viewportRef != null) {
                glrs.matrix_release(viewportRef)
            }
            return result              
        }
 
        /**
         * create project matrix
         */
        fun createProjectMatrixRef(
            glrs: glrs.InitOutput,
            modelMatrix: Float64Array,
            projectionMatrix: Float64Array,
            viewport: IntArray): Pair<Number?, Number?> {
            var matRef = glrs.matrix_create_with_components_col_order(
                modelMatrix)
            val projMatRef = glrs.matrix_create_with_components_col_order(
                projectionMatrix)

            val onePlusMatRef = glrs.matrix_create_with_components_row_order(
                Float64Array(arrayOf(
                    1.0, 0.0, 0.0, 1.0,
                    0.0, 1.0, 0.0, 1.0,
                    0.0, 0.0, 1.0, 1.0,
                    0.0, 0.0, 0.0, 1.0
            )))
            var viewportMatRef = glrs.matrix_create_with_components_row_order(
                Float64Array(arrayOf(
                    viewport[2].toDouble() / 2.0, 0.0, 0.0, 
                        viewport[0].toDouble(),
                    0.0, viewport[3].toDouble() / 2.0, 0.0, 
                        viewport[1].toDouble(),
                    0.0, 0.0, 1.0/2.0, 0.0,
                    0.0, 0.0, 0.0, 1.0)))
            

            var result : Pair<Number?, Number?> = Pair(null, null) 
            if (matRef != null && projMatRef != null
                && onePlusMatRef != null && viewportMatRef != null) {
                var bState = glrs.matrix_multiply_mut(matRef, projMatRef)
                if (bState != null && bState) {
                    bState = glrs.matrix_multiply_mut(
                        viewportMatRef, onePlusMatRef)
                }
                if (bState != null && bState) {
                    result = Pair(matRef, viewportMatRef)
                    matRef = 0 
                    viewportMatRef = 0
                }
            }
            if (projMatRef != 0) {
                glrs.matrix_release(projMatRef)
            }
            if (matRef != 0) {
                glrs.matrix_release(matRef)
            }
            if (onePlusMatRef != 0) {
                glrs.matrix_release(onePlusMatRef)
            }
            if (viewportMatRef != 0) {
                glrs.matrix_release(viewportMatRef)
            }
            return result
        }
    }
}

// vi: se ts=4 sw=4 et:
