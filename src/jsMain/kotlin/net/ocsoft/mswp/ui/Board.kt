package net.ocsoft.mswp.ui

import org.khronos.webgl.*
import net.ocsoft.mswp.*

/**
 * game board
 */
class Board(
    var color: FloatArray  = ColorScheme.colors[4]) {

    /**
     * cache of vertices
     */
    private var verticesCache : FloatArray? = null
   
    val vertices : FloatArray
        get() {
            if (verticesCache == null) {
                verticesCache = 
                    Polygon.divideSquare2(size,
                        polygonFactor[0].toInt(), 
                        polygonFactor[1].toInt())
            }
            return verticesCache!!
        }
 
    val verticesAsFloat32 : Float32Array
        get() {
            val vertices = this.vertices
            val result = Float32Array(
                Array<Float>(vertices.size) { i -> vertices[i] })
            return result
        }

    /**
     * (left, bottom) - (right, top) on board
     */
    val bounds: Pair<FloatArray, FloatArray>
        get() {
            val vertices = this.vertices
            val vertexCount = vertices.size / 3
            val triangleCount = vertexCount / 3
            val lastTriangleStart = (triangleCount - 1) * 3
            val leftBottom = vertices.sliceArray(0..2)
            val topRight = vertices.sliceArray(
                lastTriangleStart..lastTriangleStart + 2)
            return Pair(leftBottom, topRight)
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
                normalVecCache = 
                    Polygon.createNormalVectorsForTriangles(vertices)
            }
            return normalVecCache!!
        }
    val normalVectorsAsFloat32 : Float32Array
        get() {
            val normalVectors = this.normalVectors 
            val result = Float32Array(Array<Float>(normalVectors.size) {
                i -> normalVectors[i]
            })
            return result 
        }

    /**
     * normal vector
     */ 
    val normalVector: FloatArray
        get() {
            return normalVectors.sliceArray(0..2)
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
     * board size
     */
    val size : FloatArray = floatArrayOf(1.0f, 1.0f)

    /**
     * polygon factor
     */
    val polygonFactor = shortArrayOf(1, 1)

    /**
     * drawing mode
     */
    val drawingMode = WebGLRenderingContext.TRIANGLES

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
                textureCoordinatesCache = createTextureCoordinates()
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
     * textures
     */
    var textures: Textures? = null

    /**
     * transpanent texture
     */
    val transparentTexture: WebGLTexture?
        get() {
            return getTransparentTexture()
        }
    /**
     * create vertices color
     */
    fun createVerticesColor(color: FloatArray) : Float32Array {
        val verticesColor = Array<Float>((vertices.size / 3) * color.size) {
            i ->color[i % color.size]
        }
        return Float32Array(verticesColor)
    }
    /**
     * create texture coordinates
     */
    fun createTextureCoordinates(): FloatArray {
        val result = Polygon.divideSquare2d2(floatArrayOf(1f, 1f), 
                polygonFactor[0].toInt(), 
                polygonFactor[1].toInt())
        return result
    }    
     
    /**
     * get transparent texture
     */
    fun getTransparentTexture() : WebGLTexture? {
        var textures = this.textures
        var result : WebGLTexture? = null
        if (textures != null) {
            result = textures.blackTransparentTexture 
        }
        return result
    }
}
// vi: se ts=4 sw=4 et:
