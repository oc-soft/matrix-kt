package net.ocsoft.mswp.ui

import org.khronos.webgl.*
import net.ocsoft.mswp.*

/**
 * button to cover mine
 */
class MineButton(
    frontColor: FloatArray = ColorScheme.colors[1],
    backColor: FloatArray = floatArrayOf(0f, 1f, 1f, 1f),
    buttonSize : FloatArray = floatArrayOf(1f, 1f),
    thickness: Float = 0.1f) {
    
    /**
     * cache of vertices
     */
    private var verticesCache : FloatArray? = null
   
    /**
     * vertices
     */
    val vertices : FloatArray
        get() {
            if (verticesCache == null) {
                verticesCache = createPolygons(
                    polygonFactor[0].toInt(), polygonFactor[1].toInt())
            }
            return verticesCache!!
        }
    
    val verticesAsFloat32 : Float32Array
        get() {
            val vertices = this.vertices 
            val result = Float32Array(Array<Float>(vertices.size) {
                i -> vertices[i]
            })
            return result
        }
    
    /**
     * normal vector's cache
     */
    private var normalVecCache : FloatArray? = null
    
    /**
     * normal vectors
     */
    val normalVectors : FloatArray
        get() {
            if (normalVecCache == null) {
                normalVecCache = createNormalVectors()
            }
            return normalVecCache!!
        }
    
    val normalVectorsAsFloat32 : Float32Array
        get() {
            val normalVectors = this.normalVectors
            return Float32Array(
                Array<Float>(normalVectors.size) { 
                   i -> normalVectors[i] 
                })
        } 

    /**
     * vertex colors
     */
    val verticesColor : FloatArray
        get() {
            val vertices = this.vertices
            val colors = arrayOf(this.frontColor, this.backColor)
           
            return FloatArray(
                (vertices.size / 3) * (colors.size * colors[0].size)) {
                i -> 
                val colorIdx = i / ((vertices.size / 3) * colors[0].size)
                colors[colorIdx][i % colors[0].size] 
            }
        }
    val verticesColorAsFloat32 : Float32Array
        get() {
            val verticesColor = this.verticesColor
            val result = Float32Array(Array<Float>(verticesColor.size) {
                i -> verticesColor[i]
            })
            return result 
        }

    /**
     * drawing mode
     */
    val drawingMode = WebGLRenderingContext.TRIANGLES
    
    /**
     * polygon factor
     */
    val polygonFactor = shortArrayOf(1, 1)
    /**
     * button size
     */
    val buttonSize : FloatArray = floatArrayOf(1f, 1f)
    /**
     * thicness of button
     */
    val thickness : Float = thickness
    /**
     * front face color
     */
    var frontColor : FloatArray = frontColor
    /**
     * back bace color
     */
    var backColor : FloatArray = backColor


    fun createPolygons(xDivider: Int, yDivider: Int): FloatArray {
        val thickness = buttonSize.min()!! * this.thickness
        val frontV = Polygon.divideSquare2(buttonSize, 
            xDivider, yDivider, false, thickness)
        val backV = Polygon.divideSquare2(buttonSize, 
            xDivider, yDivider, true) 
        val result = frontV.copyOf(frontV.size + backV.size)
        backV.forEachIndexed({ i, elem -> result[frontV.size + i] = elem })   
        return result
    }
    
    fun createNormalVectors(): FloatArray {
        return Polygon.createNormalVectorsForTriangles(vertices)
    }

    
    
}
