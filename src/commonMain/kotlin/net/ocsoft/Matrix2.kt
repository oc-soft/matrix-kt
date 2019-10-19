package net.ocsoft

import kotlin.math.*

/**
 * matrix 2 x 2
 */
class Matrix2(
    m00: Double = 1.0,
    m01: Double = 0.0,
    m10: Double = 0.0,
    m11: Double = 1.0) {

    /**
     * components
     */
    val components = arrayOf(
        doubleArrayOf(m00, m01),
        doubleArrayOf(m10, m11))
    
    /**
     * component at 0, 0
     */
    var m00: Double
        get() {
            return components[0][0]
        } 

        set(value) {
            components[0][0] = value
        }

    /**
     * component at 0, 1
     */
    var m01: Double
        get() {
            return components[0][1]
        } 

        set(value) {
            components[0][1] = value
        }

    /**
     * component at 1, 0
     */
    var m10: Double
        get() {
            return components[1][0]
        } 

        set(value) {
            components[1][0] = value
        }

    /**
     * component at 1, 1
     */
    var m11: Double
        get() {
            return components[1][1]
        } 

        set(value) {
            components[1][1] = value
        }

  
    /**
     * indexed operator
     */
    operator fun get(rowIndex: Int, colIndex: Int): Double {
        return components[rowIndex][colIndex]
    }

    operator fun set(rowIndex: Int, colIndex: Int, value: Double) {
        components[rowIndex][colIndex] = value
    }
   

    
    /**
     * mutiply
     */
    operator fun times(other: Matrix2): Matrix2  {
        val result = Matrix2()
        for (rowIdx in 0..1) {
            var comp = 0.0
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
    operator fun times(vector: DoubleArray): DoubleArray {
        var v1 = 0.0 
        var v2 = 0.0
        val srcVec = DoubleArray(2) {
            var cmp = 0.0
            if (vector.size > it) {
                cmp = vector[it]
            }
            cmp
        }

        // val otherMat = Matrix2(v1, 0, v2, 1)
        // val resMat = this * otherMat
        // val result = doubleArrayOf(
        //    resMat[0, 0],
        //    resMat[1, 0])
        val result = DoubleArray(2) {
            var cmp = 0.0
            for (cidx in 0..1) { 
                cmp += this[it, cidx] * srcVec[cidx]
            }
            cmp
        }
        return result
    }

    /**
     * mulitiply pair 
     */
    operator fun times(pt: Pair<Double, Double>): Pair<Double, Double> {
        val vecRes = this * doubleArrayOf(pt.first, pt.second)
        val result = Pair(vecRes[0], vecRes[1])
        return result
    }


    /**
     * cofactor
     */
    fun cofactor(rowIdx: Int, colIdx: Int): Double {
        val sign = -1f.pow(rowIdx + colIdx)
        val result = sign * this[(rowIdx + 1) % 2, (colIdx + 1) % 2] 
        return result
    }

    /**
     * determinant
     */
    fun determinant(): Double {
        var result = 0.toDouble() 
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
        if (det != 0.0 || det != -0.0) {
            result = Matrix2(
                cofactor(0, 0) / det, cofactor(1, 0) / det,
                cofactor(0, 1) / det, cofactor(1, 1) / det)
        }
        return result
    }
}
