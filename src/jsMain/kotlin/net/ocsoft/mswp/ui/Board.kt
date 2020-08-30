package net.ocsoft.mswp.ui

import org.khronos.webgl.*
import net.ocsoft.mswp.*
import kotlin.math.min

/**
 * game board
 */
class Board(
    colorScheme: ColorScheme,
    var color: FloatArray = colorScheme[ColorScheme.Board]) {

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
     * (left, bottom) - (right, bottom) - (right, top) - (left, top)  on board
     */
    val bounds: Array<FloatArray>
        get() {
            return arrayOf(
                floatArrayOf(
                    - size[0] / 2.0f, - size[1] / 2.0f, 0.0f),
                floatArrayOf(
                    size[0] / 2.0f, - size[1] / 2.0f, 0.0f),
                floatArrayOf(
                    size[0] / 2.0f, size[1] / 2.0f, 0.0f),
                floatArrayOf(
                    - size[0] / 2.0f, size[1] / 2.0f, 0.0f)) 
        }

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
     * update color scheme
     */
    fun updateColorScheme(colorScheme: ColorScheme) {
        val color = colorScheme[ColorScheme.Board]
        for (i in 0 until min(color.size, this.color.size)) {
            this.color[i] = color[i]
        }
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
