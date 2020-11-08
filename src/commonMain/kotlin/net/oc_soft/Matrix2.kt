package net.oc_soft

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
     * class instance
     */
    companion object {

        
        /**
         * create rotation matrix
         */
        fun rotate(radian: Double): Matrix2 {
            val cosValue = cos(radian)
            val sinValue = sin(radian)
            return Matrix2(cosValue, -sinValue, sinValue, cosValue)
        }
    }
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
     * row size
     */
    val rowSize: Int = 2
       

    /**
     * column size
     */
    val columnSize: Int = 2

    /**
     * constructor
     */
    constructor(m00: Int, m01: Int, m10: Int, m11: Int):
        this(m00.toDouble(), m01.toDouble(), m10.toDouble(), m11.toDouble()) {
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
     * this * M(radian)
     */
    fun rotate(radian: Double): Matrix2 {
        return this * Matrix2.rotate(radian) 
    }
    
    /**
     * mutiply
     */
    operator fun times(other: Matrix2): Matrix2  {
        val result = Matrix2()
        for (rowIdx in 0..1) {
            for (colIdx in 0..1) {
                var comp = 0.0
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
        val srcVec = DoubleArray(2) {
            var cmp = 0.0
            if (vector.size > it) {
                cmp = vector[it]
            }
            cmp
        }

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
        val sign = (-1f).pow(rowIdx + colIdx)
        val result = sign * this[(rowIdx + 1) % 2, (colIdx + 1) % 2] 
        return result
    }

    /**
     * determinant
     */
    fun determinant(): Double {
        var result = 0.0
        for (i in 0..1) {
            result += cofactor(0, i) * this[0, i]
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
    /**
     * calculate hash code
     */
    override fun hashCode(): Int {
        var result = 0
        
        for (r in 0 until rowSize) {
            for (c in 0 until columnSize) {
                result = result xor this[r, c].roundToInt() 
            }
        }

        return result
    }

    override fun equals(other: Any?): Boolean {
        var result = this === other
        if (!result) {
            result = other is Matrix2
            if (result) {
                val otherMat: Matrix2 = other as Matrix2
                rowLoop@
                for (r in 0 until rowSize) {
                    for (c in 0 until columnSize) {
                        result = this[r, c] == otherMat[r, c]
                        if (!result) {
                            break@rowLoop     
                        }
                    }
                }
            }
        }
        return result
    }


    /**
     * stringify this object
     */
    override fun toString(): String {
        val fields = Array<String>(4) {
            val rowIdx = it / 2
            val colIdx = it % 2
            "m${rowIdx}${colIdx} : ${this[rowIdx, colIdx]}"
        }
        return fields.joinToString()
    }
}
// vi: se ts=4 sw=4 et:
