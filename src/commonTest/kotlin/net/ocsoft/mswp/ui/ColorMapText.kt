package net.ocsoft.mswp.ui

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertEquals


class ColorMapTest {
    @Test
    fun checkRegisteration() {
        val colorMap = ColorMap()

        val colors = Array<FloatArray>(3) {
            i ->
            floatArrayOf(ColorMap.displacement * i.toFloat(), 0f, 0f)    
        } 
        
        colors.forEachIndexed({ i, value ->
            colorMap.register(value, i) 
        })

        colors.forEachIndexed({ i, value ->
            val ivalue = colorMap.getValue(value) as Int?
            assertNotNull(ivalue, 
                "colorMap must have the key(${value})")
            assertEquals(ivalue, i,
                "colorMap must have the value ${i} with ${value}")
              
        })
    }
}
