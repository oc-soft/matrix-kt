package net.ocsoft.mswp.ui

import org.khronos.webgl.*
import net.ocsoft.mswp.*

/**
 * button to cover mine
 */
class MineButton(
    var color: FloatArray = ColorScheme.colors[1]) {
    
    /**
     * cache of vertices
     */
    private var verticesCache : FloatArray? = null
   
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
            val color = this.color
            return FloatArray((vertices.size / 3) * color.size) {
                i -> color[i % color.size] 
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
    val polygonFactor = shortArrayOf(3, 3)
    /**
     * button size
     */
    val buttonSize : FloatArray = floatArrayOf(1f, 1f)

    fun createPolygons(xDivider: Int, yDivider: Int): FloatArray {
        return Polygon.divideSquare2(buttonSize, xDivider, yDivider)
    }
    
    fun createNormalVectors(): FloatArray {
        return Polygon.createNormalVectorsForTriangles(vertices)
    }
    
}
