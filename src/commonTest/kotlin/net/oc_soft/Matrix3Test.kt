package net.oc_soft

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * matrix 3 x 3 test
 */
class Matrix3Test {

    @Test
    fun multiplyTest0() {
        val mat1 = Matrix3()
        val mat2 = Matrix3(2, 0, 0, 0, 2, 0, 0, 0, 2)
        val res = mat1 * mat2
        assertEquals(mat2, res)
    }
    
    @Test
    fun mutilplyTest1() {
        val mat1 = Matrix3(3, 0, 0, 0, 4, 0, 0, 0, 5)
        val vec = doubleArrayOf(1.0, 1.0, 1.0)
        val res = mat1 * vec
        assertEquals(3.0, res[0])
        assertEquals(4.0, res[1])
        assertEquals(5.0, res[2])
    }


    @Test
    fun determinantTest0() {
        val mat1 = Matrix3(1, 3, 2, -3, -1, -3, 2, 3, 1)
        assertEquals(-15.0, mat1.determinant())
    }

    @Test
    fun cofactorTest0() {
        val mat1 = Matrix3(-5, 0, -1, 1, 2, -1, -3, 4, 1) 
        assertEquals(6.0, mat1.cofactor(0, 0))
        assertEquals(2.0, mat1.cofactor(0, 1))
        assertEquals(10.0, mat1.cofactor(0, 2))
    }


    @Test
    fun invertTest0() {
        val mat0 = Matrix3(0, -3, -2, 1, -4, -2, -3, 4, 1)
        val mat0Inv = mat0.inverse()
        assertNotNull(mat0Inv)
        val matE = mat0 * mat0Inv
        val testValues = arrayOf(
            matE.m00 - 1.0,
            matE.m01,
            matE.m02,

            matE.m10,
            matE.m11 - 1.0,
            matE.m12,

            matE.m20,
            matE.m21, 
            matE.m22 - 1.0)
        testValues.forEach {
            val comp = if (it < 0.0) { -it } else { it }
            assertTrue(comp < 1e-10)
        }
    }
}
// vi: se ts=4 sw=4 et:
