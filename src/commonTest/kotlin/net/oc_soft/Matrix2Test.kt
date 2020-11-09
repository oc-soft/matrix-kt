package net.oc_soft

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * matrix 2 x 2 test
 */
class Matrix2Test {

    @Test
    fun multiplyTest0() {
        val mat1 = Matrix2()
        val mat2 = Matrix2(2, 0, 0, 2)
        val res = mat1 * mat2
        assertEquals(mat2, res)
     
    }
    



    @Test
    fun rotateTest0() {
        val mat1 = Matrix2.rotate(kotlin.math.PI / 3)  
        assertEquals(mat1.m00, kotlin.math.cos(kotlin.math.PI / 3))
        assertEquals(mat1.m01, - kotlin.math.sin(kotlin.math.PI / 3))
        assertEquals(mat1.m10, kotlin.math.sin(kotlin.math.PI / 3))
        assertEquals(mat1.m11, kotlin.math.cos(kotlin.math.PI / 3))
     }

    @Test
    fun determinantTest0() {
        val mat1 = Matrix2(2, 4, 2, 3)
        assertEquals(-2.0, mat1.determinant())
    }

    @Test
    fun cofactorTest0() {
        val mat1 = Matrix2(2, 4, 2, 3)
        assertEquals(3.0, mat1.cofactor(0, 0))
        assertEquals(2.0, mat1.cofactor(1, 1))
        assertEquals(-2.0, mat1.cofactor(0, 1))
        assertEquals(-4.0, mat1.cofactor(1, 0))
    }


    @Test
    fun invertTest0() {
        val mat0 = Matrix2(3, 4, 10, 6)
        val mat0Inv = mat0.inverse()
        assertNotNull(mat0Inv)
        val matE = mat0 * mat0Inv
        val testValues = arrayOf(
            matE.m00 - 1.0,
            matE.m01,
            matE.m10,
            matE.m11 - 1.0)
        testValues.forEach {
            val comp = if (it < 0.0) { -it } else { it }
            assertTrue(comp < 1e-10)
        }
    }
}
// vi: se ts=4 sw=4 et:
