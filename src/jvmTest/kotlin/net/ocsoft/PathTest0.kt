package net.ocsoft

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.collections.ArrayList

class PathTest0 {
    @Test
    fun parseFloatNumber() {
        val data = doubleArrayOf(1.0, 201.0, -34.0)

        val strData = Array<String>(data.size) {
             "%.2f".format(data[it])
        }
        val dataRes = ArrayList<Double>()
        val res = Path.parseCoordinateSequence(
            Path.Stream(strData.joinToString(" ")),
            dataRes) 
        assertTrue(res)
        assertEquals(data.size, dataRes.size)
        dataRes.forEachIndexed {
            idx, elm -> 
            assertEquals(data[idx], elm)
        }
    }
}
