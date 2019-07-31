package net.ocsoft.mswp

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals



/**
 * operations about matrix
 */
class MatrixTest {

    @Test
    fun multiplyTest1() {
        val matA = floatArrayOf(
            1f, 2f, 0f, 0f,
            3f, 4f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f)
        val matB = floatArrayOf(
            10f, 20f, 0f, 0f,
            30f, 40f, 0f, 0f,
             0f,  0f, 1f, 0f,
             0f,  0f, 0f, 1f) 
        
        val matExpect = floatArrayOf(
            1f * 10f + 3f * 20f, 2f * 10f + 4f * 20f, 0f, 0f,
            1f * 30f + 3f * 40f, 2f * 30f + 4f * 40f, 0f, 0f,
                             0f,                  0f, 1f, 0f,
                             0f,                  0f, 0f, 1f)

        val matRes = Matrix.multiply(matA, matB)
        assertNotNull(matRes, "matrix multiply operation must be done.")
        if (matRes != null) {
            matRes.forEachIndexed({ idx, elem ->
                assertEquals(elem, matExpect[idx],
                    "multiplied component value must be ${matExpect} but"
                    + " ${elem}.") 
            })
        }
    }    

}
