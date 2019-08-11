package net.ocsoft.mswp
import kotlin.math.*

/**
 * polygon related functions
 */
class Polygon {
     
    companion object {
        /**
         * diveice square-2d by 2
         */
        fun divideSquare2d2(
            size : FloatArray,
            xDivider: Int, 
            yDivider: Int,
            reverse: Boolean = false): FloatArray {
            val diviededVertices3 = divideSquare2(
                size, xDivider, yDivider, reverse)
            val result = FloatArray((diviededVertices3.size / 3) * 2) {
                val coordIdx = it * 3 
                val xyIdx = it % 2
                diviededVertices3[coordIdx + xyIdx]
            }
            return result
        }
        /**
         * divide squere by 2
         */
        fun divideSquare2(
            size : FloatArray,
            xDivider: Int, 
            yDivider: Int,
            reverse: Boolean = false,
            zCoord: Float = 0f): FloatArray {
            val size3 = size.copyOf(3)

            val sizeD = floatArrayOf(
                size[0] / xDivider, 
                size[1] / yDivider,
                zCoord)

            val vertexArray = Array<FloatArray>(
                (xDivider + 1) * (yDivider + 1)) {
                i0 -> 
                val xyzIndices = intArrayOf(
                    i0 % (xDivider + 1),
                    i0 / (xDivider + 1),
                    1)
                FloatArray(xyzIndices.size) { j0 -> 
                    -size3[j0] / 2 + sizeD[j0] * xyzIndices[j0]
                }
            }
            val vertexTriArray = Array<FloatArray>(xDivider * yDivider * 2) {
                i1 ->
                val triPos = i1 % 2
                val rowIndex = (i1 / 2) / xDivider
                val colIndex = (i1 / 2) % xDivider 
                val indices = arrayOf(
                    rowIndex * (xDivider + 1) + colIndex,
                    rowIndex * (xDivider + 1) + colIndex + 1,
                    (rowIndex + 1) * (xDivider + 1) + colIndex + 1,
                    (rowIndex + 1) * (xDivider + 1) + colIndex)
                if (reverse) {
                    indices.reverse()
                }
                val quadVert = Array<FloatArray>(indices.size) { 
                   j1 -> vertexArray[indices[j1]]
                }
                FloatArray(3 * 3) { k1 ->
                    val groupIndex = k1 / 3
                    val coordIndex = k1 % 3
                    quadVert[(triPos * 2 + groupIndex) % 4][coordIndex]
                }
            }
            val result = FloatArray(
                vertexTriArray.size * vertexTriArray[0].size) {
                i2 ->
                val vertArray = vertexTriArray[i2 / vertexTriArray[0].size]
                vertArray[i2 % vertexTriArray[0].size]
            }
            return result
        }
    
        fun createNormalVectorsForTriangles(vertices: FloatArray): FloatArray {
            val normalVectors = Array<FloatArray>(vertices.size / 9) {
                i ->
                val groupIndex = i * 9
                  
                val points = Array<FloatArray>(3) {
                    j -> 
                    val k = j * 3
                    FloatArray(3) {
                       l -> 
                       vertices[groupIndex + k + l]
                    }
                }
                val vectors = Array<FloatArray>(2) { j ->
                    FloatArray(3) { k -> 
                        var coord = points[j + 1][k]
                        coord -= points[0][k]
                        coord
                    }
                }
                // calc cross product
                var crossVec = FloatArray(3) { l ->
                    var coord = vectors[0][(l + 1) % 3]
                    coord *= vectors[1][(l + 2) % 3]
                    coord -= (vectors[0][(l + 2) % 3] * vectors[1][(l + 1) % 3]) 
                    coord
                }
                var crossVecLen = sqrt(crossVec.fold(0f, {
                    lastVal, value ->
                    lastVal + value.pow(2f)
                }))
                crossVec = FloatArray(crossVec.size) {
                    j -> crossVec[j] / crossVecLen 
                }
                FloatArray(3 * crossVec.size) {
                    j -> crossVec[j % crossVec.size]
                } 
            }
            val result = FloatArray(
                normalVectors.size * normalVectors[0].size) {
                i -> normalVectors[i / normalVectors[0].size][
                    i % normalVectors[0].size]
            }
            return result
        }
    }
}
