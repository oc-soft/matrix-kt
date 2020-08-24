package net.ocsoft.mswp.ui

import org.khronos.webgl.*
import net.ocsoft.mswp.*

/**
 * button to cover mine
 */
@Suppress("UNUSED_PARAMETER")
class MineButton(
    colorScheme: ColorScheme,
    frontColor: FloatArray = colorScheme[ColorScheme.ButtonFront],
    backColor: FloatArray = colorScheme[ColorScheme.ButtonBack],
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
            val textureCoordinates = this.textureCoordinates
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
        val textures = arrayOf(
            Polygon.divideSquare2d2(texSize, xDivider, yDivider),
            Polygon.divideSquare2d2(texSize, xDivider, yDivider, true))
       

        val offset = arrayOf(
            floatArrayOf(.5f, .5f + .25f),
            floatArrayOf(.5f, .25f))


        val result = FloatArray(textures[0].size + textures[1].size) {
            val texIdx0 = it / textures[0].size
            val texIdx1 = it % textures[0].size
            val offsetIdx = it % offset[0].size
            textures[texIdx0][texIdx1] + offset[texIdx0][offsetIdx]  
        }

        return result
    }    
    
    fun createNormalVectors(): FloatArray {
        return Polygon.createNormalVectorsForTriangles(vertices)
    }
}
// vi: se ts=4 sw=4 et:
