package net.ocsoft

import kotlin.test.Test
import kotlin.test.assertTrue

class IntExtensonTest {

    @Test
    fun toString1() {
        val tVal = -12

        assertTrue(
            tVal.toString(10, 0) == "-12",
            "can work convert minus digit number to string")      
       
    }     

    @Test
    fun toString2() {
        val tVal = 12

        assertTrue(
            tVal.toString(12, 0) == "10",
            "can work convert digit number to base 12 string")      
       
    }     

    @Test
    fun toString3() {
        val tVal = -122

        assertTrue(
            tVal.toString(16, 0) == "-7a",
            "can work convert digit number to base 12 string")      
       
    }     
    @Test
    fun toString5() {
        val tVal = -122

        assertTrue(
            tVal.toString(16, 5) == "-007a",
            "can work convert digit number to base 12 string")      
       
    }     
}


// vi: se ts=4 sw=4 et:
