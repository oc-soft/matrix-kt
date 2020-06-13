package net.ocsoft.mswp.ui
import org.khronos.webgl.Float64Array
import org.khronos.webgl.get
import net.ocsoft.mswp.ui.grid.Display



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
     * setup projection plane for editting light point
     */
    fun setupLighUiPlane(display: Display): Unit {
        val renderingCtx = display.renderingCtx
        val glrs = renderingCtx.glrs 

        pointOnPlane = null
        normalVector = null
        if (glrs != null) {
            val board = display.board
            val buttonsCoord = display.calcButtonsCoordinate() 
             
            val normal = board.normalVector  
            val (leftBottom, topRight)  = board.bounds
            val planeRef = glrs.plane_create(
                Float64Array(
                    Array<Double>(normal.size) 
                        { normal[it].toDouble() }), 
                Float64Array(
                    Array<Double>(leftBottom.size)
                        { leftBottom[it].toDouble() }))
            
            val distanceIdxRef = glrs.plane_sort_points(
                planeRef, buttonsCoord) 

            val distancesRef = glrs.distance_indices_get_distances(
                distanceIdxRef) 
            var farPtFromBoard: FloatArray? = null
            if (glrs.distances_size(distancesRef).toInt() > 0) {
                val distanceRef = glrs.distances_get(
                    distancesRef,
                    glrs.distances_size(distancesRef).toInt() - 1)
                
                val indices = glrs.distance_indices_get_indices(
                    distanceIdxRef, distanceRef)
                if (indices != null && indices.length > 0) {
                    farPtFromBoard = buttonsCoord[indices[0]]
                }

                glrs.distance_release(distanceRef)
            }
            if (farPtFromBoard != null) {
                pointOnPlane = calcPointOffset(farPtFromBoard, normal) 
                normalVector = normal  
            }

            glrs.distances_release(distancesRef)
            glrs.distance_indices_release(distanceIdxRef)
            glrs.plane_release(planeRef)
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
    /*
    fun projectOnPlane(
        glrs: glrs.InitOutput,
        normal: FloatArray,
        pointOnPlane: FloatArray,
        point: FloatArray): FloatArray {
    }


    fun projectOnPlane(
        glrs: glrs.InitOutput,
        planeRef: Number,
        point: FloatArray): FloatArray {
        
    }
    */
}

// vi: se ts=4 sw=4 et:
