package net.ocsoft.mswp.ui

import org.khronos.webgl.*
import net.ocsoft.mswp.*

/**
 * button to cover mine
 */
class MineButton(
    frontColor: FloatArray = ColorScheme.colors[1],
    backColor: FloatArray = ColorScheme.colors[3],
    buttonSize : FloatArray = floatArrayOf(1f, 1f),
    thickness: Float = 0.001f) {
    
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
     * texture coordinates
     */
    var textureCoordinatesCache : FloatArray? = null

    /**
     * texture coordinates
     */
    val textureCoordinates : FloatArray
        get() {
            if (textureCoordinatesCache == null) {
                textureCoordinatesCache = createTextureCoordinates(
                    polygonFactor[0].toInt(), polygonFactor[1].toInt())
            }
            return textureCoordinatesCache!!
        }

    val textureCoordinatesAsFloat32 : Float32Array
        get() {
            val textureCoodinates = this.textureCoordinates
            val result = Float32Array(Array<Float>(textureCoordinates.size) {
                textureCoordinates[it]
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
            var faceVertexCount = vertices.size / 2 
            faceVertexCount /= 3
            val faceColorCompCount = faceVertexCount * colors[0].size 
            return FloatArray(faceColorCompCount * colors.size) {
                i -> 
                val colorIdx = i / faceColorCompCount
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
     * texture index
     */ 
    val textureIndex0 : Int
        get() {
            return Textures.ButtonTextureIndex 
        }
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

    /**
     * create texture coordinates
     */
    fun createTextureCoordinates(xDivider : Int, yDivider : Int): FloatArray {
        val texSize = floatArrayOf(1f, .5f)
        val frontT =  Polygon.divideSquare2d2(texSize, xDivider, yDivider)
        val backT = Polygon.divideSquare2d2(texSize, xDivider, yDivider)
        val result = frontT.copyOf(frontT.size + backT.size)
        val offsetBackT = floatArrayOf(0f, .5f)
        backT.forEachIndexed({ 
            i, elem -> 
            result[frontT.size + i] = elem + offsetBackT[i % 2]
        })   
        return result
    }    
    
    fun createNormalVectors(): FloatArray {
        return Polygon.createNormalVectorsForTriangles(vertices)
    }

    
    
}
