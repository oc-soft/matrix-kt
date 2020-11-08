package net.oc_soft

import kotlin.math.*

/**
 * matrix 3 x 3
 */
class Matrix3(
    m00: Double = 1.0,
    m01: Double = 0.0,
    m02: Double = 0.0,
    m10: Double = 0.0,
    m11: Double = 1.0,
    m12: Double = 0.0,
    m20: Double = 0.0,
    m21: Double = 0.0,
    m22: Double = 1.0) {

    /**
     * components
     */
    val components = arrayOf(
        doubleArrayOf(m00, m01, m02),
        doubleArrayOf(m10, m11, m12),
        doubleArrayOf(m20, m21, m22))
    
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
     * component at 0, 2
     */
    var m02: Double
        get() {
            return components[0][2]
        } 

        set(value) {
            components[0][2] = value
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
     * component at 1, 2
     */
    var m12: Double
        get() {
            return components[1][2]
        } 

        set(value) {
            components[1][2] = value
        }
   
    /**
     * component at 2, 0
     */
    var m20: Double
        get() {
            return components[2][0]
        } 

        set(value) {
            components[2][0] = value
        }

    /**
     * component at 2, 1
     */
    var m21: Double
        get() {
            return components[2][1]
        } 

        set(value) {
            components[2][1] = value
        }

    /**
     * component at 2, 2
     */
    var m22: Double
        get() {
            return components[2][2]
        } 

        set(value) {
            components[2][2] = value
        }

    /**
     * constructor
     */
    constructor(
        m00: Int,
        m01: Int,
        m02: Int,
        m10: Int,
        m11: Int,
        m12: Int,
        m20: Int,
        m21: Int,
        m22: Int):
            this(m00.toDouble(), m01.toDouble(), m02.toDouble(),
                m10.toDouble(), m11.toDouble(), m12.toDouble(),
                m20.toDouble(), m21.toDouble(), m22.toDouble()) 
    

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
    operator fun times(other: Matrix3): Matrix3  {
        val result = Matrix3()
        for (rowIdx in 0..2) {
            for (colIdx in 0..2) {
                var comp = 0.0
                for (idx in 0..2) {
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
     * multiply vector and get vector
     */
    operator fun times(vec: DoubleArray): DoubleArray {
        val result = DoubleArray(3)
        val vec0 = DoubleArray(3) {
            if (it < vec.size) { vec[it] } else { 0.0 }
        }
        
        for (ridx in 0 until 3) {
            var comp = 0.0
            for (cidx in 0 until 3) {
                comp += this[ridx, cidx] * vec0[cidx]
            }
            result[ridx] = comp
        }
        return result
    }

    /**
     * cofactor matrix
     */
    fun cofactorMatrix(rowIdx: Int, colIndex: Int): Matrix2 {
        val result = Matrix2()
        var rowIdx2 = 0
        for (rowIdx3 in 0..2) {
            if (rowIdx3 != rowIdx) {
                var colIdx2 = 0
                for (colIdx3 in 0..2) {
                    if (colIdx3 != colIndex) {
                        result[rowIdx2, colIdx2] = 
                            this[rowIdx3, colIdx3]  
                        colIdx2++
                    } 
                }
                rowIdx2++
            }
        } 
        return result
    }

    /**
     * cofactor
     */
    fun cofactor(rowIdx: Int, colIndex: Int): Double {
        val sign = (-1f).pow(rowIdx + colIndex)
        val det2 = cofactorMatrix(rowIdx, colIndex).determinant() 
        val result = sign * det2
        return result
    }
 
    /**
     * determinant
     */
    fun determinant(): Double {
        var result = 0.0
        for (i in 0..2) {
            result += this[0, i] * cofactor(0, i) 
        }            
        return result
    }
    

    /**
     * inverse
     */
    fun inverse(): Matrix3? {
        val det = determinant()
        var result: Matrix3? = null
        if (det != 0.0 || det != -0.0) {
            result = Matrix3(
                cofactor(0, 0) / det,
                cofactor(1, 0) / det,
                cofactor(2, 0) / det,
                cofactor(0, 1) / det, 
                cofactor(1, 1) / det,
                cofactor(2, 1) / det,
                cofactor(0, 2) / det, 
                cofactor(1, 2) / det,
                cofactor(2, 2) / det)
                  
        }
        return result
    }


    override fun hashCode(): Int {
        var result = 0 
        for (ridx in 0 until 3) {
            for (cidx in 0 until 3) {
                result = result xor this[ridx, cidx].roundToInt()
            }
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        var result = this === other
        if (!result) {
            result = other is Matrix3
            if (result) {
                val otherMat: Matrix3 = other as Matrix3
                for (ridx in 0 until 3) {
                    for (cidx in 0 until 3) {
                        result = this[ridx, cidx] == otherMat[ridx, cidx]
                    }
                }
            }
        }
        return result
    }

    override fun toString(): String {
        val fields = Array<String>(9) {
            val rowIdx = it / 3 
            val colIdx = it % 3
            "m${rowIdx}${colIdx} : ${this[rowIdx, colIdx]}"
        }
        return fields.joinToString()   
    }
}
// vi: se ts=4 sw=4 et:
