package net.ocsoft.mswp


/**
 * operations about matrix
 */
class Matrix {


    companion object {
        /**
         * create diagonal matrix
         */
        fun createDiagonalMatrix(v : Float) : FloatArray {
            return FloatArray(16) {
                i -> if (i / 4 == i % 4) v else 0f
            }
        }

        /**
         * plus operation of 4 x 4 matrices
         */
        fun plus(a: FloatArray, b: FloatArray): FloatArray? {
            var result : FloatArray? = null
            if (a.size == 16 && b.size == 16) {
                result = FloatArray(16) {
                    i -> a[i] + b[i]
                }
            }
            return result
        }
        
        /**
         * calculate maltiplication of 4 x 4 matrices
         */
        fun multiply(a: FloatArray, b: FloatArray): FloatArray? {
            var result : FloatArray? = null
            if (a.size == 16 && b.size == 16) {
                result = FloatArray(16) {
                    i ->
                    val rowIdx = i / 4
                    val colIdx = i % 4    
                    var comp = 0f
                    
                    for (idx in 0..3) {
                        val aComp = getComponent(a, rowIdx, idx)
                        val bComp = getComponent(b, idx, colIdx)
                        comp += aComp * bComp
                    }
                    comp
                }
           }
           return result
        }
        /**
         * get a component in mat at row and col 
         */
        fun getComponent(mat: FloatArray, row: Int, col: Int) : Float {
            return mat[4 * row + col]
        }
    }
}


