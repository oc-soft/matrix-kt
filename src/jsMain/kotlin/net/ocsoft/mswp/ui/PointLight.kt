package net.ocsoft.mswp.ui
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.Float64Array
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import net.ocsoft.mswp.ui.grid.Display
import net.ocsoft.mswp.ui.Grid


/**
 * provide user interface for editing point light.
 */
class PointLight(
    val pointOffset: Float = 0.1f) {
    

    /**
     * the point on projection plane for editting point
     */
    var pointOnPlane: FloatArray? = null

    /**
     * the normal projection plane vector for edditing point
     */
    var normalVector: FloatArray? = null

    /**
     * point light
     */
    var pointLight: net.ocsoft.mswp.PointLight? = null
   
    /**
     * light editing table
     */
    var lightEditingTable: Array<FloatArray>? = null


    /**
     * light editing table aray cache
     */
    var lightEditingTableArrayCache: Float32Array? = null

    /**
     * optimized for gl rendering
     */
    val lightEditingTableArray: Float32Array? 
        get() {
            var result: Float32Array? = null
            result = lightEditingTableArrayCache  
            if (result == null) {
                result = createLightEditingTableArray()
                lightEditingTableArrayCache = result
            } 
            return result
        } 

    


    /**
     * projected light edit table coordinate
     */
    var lightEditViewTable: Array<Float32Array>? = null

    
    /**
     * model matrix
     */
    val modelMatrix: Float32Array = 
        Float32Array(Array<Float>(16){ 
            if (it / 4 == it % 4) { 1.0f } else { 0.0f }
        })

    /**
     * projection matrix
     */
    var projectionMatrix: Float32Array? = null

    /**
     * viewport
     */
    var viewport: IntArray? = null


    /**
     * create light editing table array
     */
    fun createLightEditingTableArray(): Float32Array? {
        val vertexArray = lightEditingTable
        var result: Float32Array? = null
       
        if (vertexArray != null && vertexArray.size == 4) {
            val triangleCount = vertexArray.size - 2
            result = Float32Array(
                triangleCount * 3  * 3)
            val indices = arrayOf(
                intArrayOf(0, 1, 2),
                intArrayOf(2, 3, 0))
            for (idx in 0..result.length - 1) {
                val coordIdx = idx % 3
                val groupIdx = idx / (3 * 3) 
                val groupOffset = (idx % (3 * 3)) / 3
                val ptIdx = indices[groupIdx][groupOffset]
                result[idx] = vertexArray[ptIdx][coordIdx]
            }
        } 
        return result
    }
    

    /**
     * setup projection plane for editting light point
     */
    fun setupLighUiPlane(
        grid: Grid): Unit {
        val glrs = grid.glrs 
        val renderingCtx = grid.renderingCtx
        pointOnPlane = null
        normalVector = null
        lightEditingTable = null
        projectionMatrix = null
        lightEditingTableArrayCache = null
        lightEditViewTable = null
        if (glrs != null) {
            val board = grid.board
            val buttonsCoord = grid.display.calcButtonsCoordinate() 
             
            val normal = board.normalVector  
            val bounds  = board.bounds
            val planeRef = glrs.plane_create(
                Float64Array(
                    Array<Double>(normal.size) 
                        { normal[it].toDouble() }), 
                Float64Array(
                    Array<Double>(bounds[0].size)
                        { bounds[0][it].toDouble() }))
            
            val floatIdxRef = glrs.plane_sort_points_0(
                planeRef, buttonsCoord) 

            val floatKeysRef = glrs.float_indices_get_float_keys(
                floatIdxRef) 
            var farPtFromBoard: FloatArray? = null
            if (glrs.float_vec_size(floatKeysRef).toInt() > 0) {
                val floatRef = glrs.float_vec_get(
                    floatKeysRef,
                    glrs.float_vec_size(floatKeysRef).toInt() - 1)
                
                val indices = glrs.float_indices_get_indices(
                    floatIdxRef, floatRef)
                if (indices != null && indices.length > 0) {
                    farPtFromBoard = buttonsCoord[indices[0]]
                }

                glrs.float_release(floatRef)
            }
            if (farPtFromBoard != null) {
                pointOnPlane = calcPointOffset(farPtFromBoard, normal) 
                normalVector = normal  
                lightEditingTable = Array<FloatArray>(bounds.size) {
                    projectOnPlane(glrs,
                        normalVector!!, pointOnPlane!!,
                        bounds[it])!!
                }
            }

            glrs.float_vec_release(floatKeysRef)
            glrs.float_indices_release(floatIdxRef)
            glrs.plane_release(planeRef)
        
        }
        if (lightEditingTable != null) {
            projectionMatrix = createProjectionMatrix(grid)    
            viewport = grid.viewport!!
            lightEditViewTable = calcLightEditViewTable(
                glrs!!,
                lightEditingTable!!,
                projectionMatrix!!,
                viewport!!)
        }
    } 

    /**
     * calculate point moved offset from point 
     */
    fun calcPointOffset(point: FloatArray,
        normalDirection: FloatArray): FloatArray {
         
        val result = floatArrayOf(
            point[0] + normalDirection[0] * pointOffset,
            point[1] + normalDirection[1] * pointOffset,
            point[2] + normalDirection[2] * pointOffset)

        return result
    }


    /**
     * project point on plane
     */
    fun projectOnPlane(
        glrs: glrs.InitOutput,
        normal: FloatArray,
        pointOnPlane: FloatArray,
        point: FloatArray): FloatArray? {
        val plane = glrs.plane_create(
            Float64Array(
                Array<Double>(pointOnPlane.size){pointOnPlane[it].toDouble()}),
            Float64Array(
                Array<Double>(point.size){ point[it].toDouble() })) 

        val result = projectOnPlane(glrs, plane, point)

        glrs.plane_release(plane)
        return result
    }

    /**
     * project point on plane
     */
    fun projectOnPlane(
        glrs: glrs.InitOutput,
        planeRef: Number,
        point: FloatArray): FloatArray? {
        
        val projPoint = glrs.plane_project(
            planeRef,
            Float64Array(Array<Double>(point.size) { point[it].toDouble() }))
        var result: FloatArray? = null
        if (projPoint != null) {
            result = FloatArray(projPoint.length) { projPoint[it].toFloat() }
        }
        return result
    }

    /**
     * create projection matrix
     */
    fun createProjectionMatrix(
        grid: Grid): Float32Array? {
        return grid.createCameraMatrix()
    }

    /**
     * calculate editing view table
     */
    fun calcLightEditViewTable(
        glrs: glrs.InitOutput,
        lightEditingTable: Array<FloatArray>,
        projectionMatrix: Float32Array, 
        viewport: IntArray): Array<Float32Array> {
         
        val result = Array<Float32Array>(lightEditingTable.size) {
            gl.Matrix.project(glrs, 
                lightEditingTable[it][0],
                lightEditingTable[it][1],
                lightEditingTable[it][2],
                modelMatrix,
                projectionMatrix,
                viewport)!!
        }
        return result 
    }

    /**
     * handle user input
     */
    fun handleUserInput(
        grid: Grid, 
        gl: WebGLRenderingContext,
        xw: Int, yw: Int, zw: Float) {
    }
         

    /**
     * get true if point in projected light edit table. 
     */
    fun isInTable(
        glrs: glrs.InitOutput,
        xw: Float, yw: Float): Boolean {

        var result = false
        val lightEditViewTable = this.lightEditViewTable
        if (lightEditViewTable != null) {
            for (idx in 0 .. lightEditViewTable.size - 1) {
            } 
        }
        return result
    }

    


}
// vi: se ts=4 sw=4 et:
