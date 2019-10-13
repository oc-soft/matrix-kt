package net.ocsoft

import kotlin.math.*

/**
 * matrix 2 x 2
 */
class Matrix2(
    m00: Float = 1f,
    m01: Float = 0f,
    m10: Float = 0f,
    m11: Float = 1f) {

    /**
     * components
     */
    val components = arrayOf(
        floatArrayOf(m00, m01),
        floatArrayOf(m10, m11))
    
    /**
     * component at 0, 0
     */
    var m00: Float
        get() {
            return components[0][0]
        } 

        set(value) {
            components[0][0] = value
        }

    /**
     * component at 0, 1
     */
    var m01: Float
        get() {
            return components[0][1]
        } 

        set(value) {
            components[0][1] = value
        }

    /**
     * component at 1, 0
     */
    var m10: Float
        get() {
            return components[1][0]
        } 

        set(value) {
            components[1][0] = value
        }

    /**
     * component at 1, 1
     */
    var m11: Float
        get() {
            return components[1][1]
        } 

        set(value) {
            components[1][1] = value
        }

  
    /**
     * indexed operator
     */
    operator fun get(rowIndex: Int, colIndex: Int): Float {
        return components[rowIndex][colIndex]
    }

    operator fun set(rowIndex: Int, colIndex: Int, value: Float) {
        components[rowIndex][colIndex] = value
    }
   

    
    /**
     * mutiply
     */
    operator fun times(other: Matrix2): Matrix2  {
        val result = Matrix2()
        for (rowIdx in 0..1) {
            var comp = 0f
            for (colIdx in 0..1) {
                for (idx in 0..1) {
                    val aComp = this[rowIdx, idx]
                    val bComp = other[idx, colIdx]
                    comp += aComp * bComp
                }
                result[rowIdx, colIdx] = comp
            }
        }
        return result
    }

    /**
     * mulitiply vector
     */
    operator fun times(vector: FloatArray): FloatArray {
        var v1 = 0f 
        var v2 = 0f
        if (vector.size > 0) {
            v1 = vector[0]
        }
        if (vector.size > 1) {
            v1 = vector[1]
        } 
        val otherMat = Matrix2(v1, 0f, 0f, v2)
        val resMat = this * otherMat
        val result = floatArrayOf(
            resMat[0, 0],
            resMat[1, 1])
        return result
    }

    /**
     * mulitiply pair 
     */
    operator fun times(pt: Pair<Float, Float>): Pair<Float, Float> {
        val vecRes = this * floatArrayOf(pt.first, pt.second)
        val result = Pair(vecRes[0], vecRes[1])
        return result
    }


    /**
     * cofactor
     */
    fun cofactor(rowIdx: Int, colIdx: Int): Float {
        val sign = -1f.pow(rowIdx + colIdx)
        val result = sign * this[(rowIdx + 1) % 2, (colIdx + 1) % 2] 
        return result
    }

    /**
     * determinant
     */
    fun determinant(): Float {
        var result = 0.toFloat() 
        for (i in 0..1) {
            result += cofactor(0, i)
        }
        return result
    }

    /**
     * inverse
     */
    fun inverse(): Matrix2? {
        val det = determinant()
        var result: Matrix2? = null
        if (det != 0f) {
            result = Matrix2(
                cofactor(0, 0) / det, cofactor(1, 0) / det,
                cofactor(0, 1) / det, cofactor(1, 1) / det)
        }
        return result
    }
}
