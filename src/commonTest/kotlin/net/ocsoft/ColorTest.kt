package net.ocsoft

import kotlin.text.Regex
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class ColorTest {

    @Test
    fun numberParse0() {
        val match = Regex(
            "rgb\\(\\s*((${Color.numberPattern})%?)\\s*,"
            + "\\s*((${Color.numberPattern})%?)\\s*\\)").find("rgb(10.0, 20%)")
        assertTrue(match != null)
    }

    @Test
    fun rgbParse0() {
        val data = "rgb( 20, 80%, 1.2)"
        assertTrue(Color.rgbPattern.find(data) != null)
    }


    @Test
    fun rgbParse1() {
        val data = "rgb( 20, 80%, 1.2)"
        val color = Color.parse(data)
        assertTrue(color != null)
        assertTrue(color[0].intValue == 20)
        assertTrue(color[1].floatValue == 80f / 100f)
        assertTrue(color[2].intValue == 0xff)
    }

    @Test
    fun rgbaParse0() {
        val data = "rgba( 20, 80%, 1.2, 200)"
        assertTrue(Color.rgbaPattern.find(data) != null)
    }


    @Test
    fun rgbaParse1() {
        val data = "rgb( 20, 80%, 1.2, 60%)"
        val color = Color.parse(data)
        assertTrue(color != null)
        assertTrue(color[0].intValue == 20)
        assertTrue(color[1].floatValue == 80f / 100f)
        assertTrue(color[2].intValue == 0xff)
        assertTrue(color[3].floatValue == 60f / 100f)
    }

    @Test
    fun rgbaL4Parse0() {
        val data = "rgba( 20  80%  1.2 / 200)"
        assertTrue(Color.rgbaPatternl4.find(data) != null)
    }


    @Test
    fun rgbaL4Parse1() {
        val data = "rgb(20 80% 1.2/ 60%)"
        val color = Color.parse(data)
        assertTrue(color != null)
        assertTrue(color[0].intValue == 20)
        assertTrue(color[1].floatValue == 80f / 100f)
        assertTrue(color[2].intValue == 0xff)
        assertTrue(color[3].floatValue == 60f / 100f)
    }

    @Test
    fun hexParse0() {
        val data = "#23659a"
        assertTrue(Color.hexPattern.find(data) != null)
    }


    @Test
    fun hexParse1() {
        val data = "#90Fb3424"
        val color = Color.parse(data)
        assertTrue(color != null)
        assertTrue(color[0].intValue == 0x90)
        assertTrue(color[1].intValue == 0xfb)
        assertTrue(color[2].intValue == 0x34)
        assertTrue(color[3].intValue == 0x24)
    }

    @Test
    fun hex2Parse0() {
        val data = "#269"
        assertTrue(Color.hexPattern2.find(data) != null)
    }


    @Test
    fun hex2Parse1() {
        val data = "#9F34"
        val color = Color.parse(data)
        assertTrue(color != null)
        assertTrue(color[0].intValue == 0x99)
        assertTrue(color[1].intValue == 0xff)
        assertTrue(color[2].intValue == 0x33)
        assertTrue(color[3].intValue == 0x44)
    }

    
}


// vi: se ts=4 sw=4 et:
