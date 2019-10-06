package net.ocsoft

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.collections.ArrayList


class PathTest {

    @Test
    fun streamTest() {
        val data = "Hello world"
        val st = Path.Stream(data)
        data.forEachIndexed {
            idx, elem ->
            assertEquals(elem, st.nextChar!!)
        } 
    }
    @Test
    fun parseCoordiantePair() {
        val data = "123 321"
        var dataRes = "" 
        
        val res = Path.parseCoordinatePair(Path.Stream(data)) {
            dataRes = "${it.first} ${it.second}"
        }
        assertTrue(res)
        assertEquals(data, dataRes)
    }
    @Test
    fun parseWsp0() {
        val data = " \n\r\t"
        
        val st = Path.Stream(data)
        
        var state = false
        do {
            state = Path.parseWsp(st) 
        } while(state)
        assertEquals(st.index, data.length) 
          
    }
    @Test
    fun parseCoordinatePairSequence0() {
        val data = "123 321 234 567"
        var dataRes = "" 
        val coordinatePairs = ArrayList<Pair<Int, Int>>()
        val res = Path.parseCoordinatePairSequence(Path.Stream(data), 
            coordinatePairs)
        assertTrue(res)
        coordinatePairs.forEachIndexed {
            idx, elem ->
            dataRes += "${elem.first} ${elem.second}"      
            if (idx < coordinatePairs.size - 1) {
                dataRes += " "
            }
        }
        assertEquals(data, dataRes) 
    }

    @Test
    fun parseNumber0() {
        val data = "200"
        var number = 0
        val res = Path.parseNumber(Path.Stream(data),  { number = it }) 
        assertTrue(res)
        assertEquals(number.toString(), data)
    }
    @Test
    fun streamTest0() {
        val data = "  M"
        val st = Path.Stream(data)
        Path.parseWspZeroOrMore(st)
        val pc = st.peekChar
        var eqState = 'M' == pc!!
        assertTrue(eqState)
    }

    @Test
    fun parseMoveto0() {
        val data = arrayOf(
            "M200 100",
            "M200 100"
        )
        var strRes = ""
        var parseRes = false
        parseRes = Path.parseMoveto(Path.Stream(data[0]), {
            strRes += "${it.type}${it.data[0]} ${it.data[1]}"
            true
        }, null)
        assertTrue(parseRes)
        assertEquals(data[1], strRes) 
    }

    @Test
    fun parseTest0() {
        val data = arrayOf(
            "M200 100",
            "M200 100"
        )
        var dataRes = ""
        var errorData = -1 
        val res = Path.parse0(Path.Stream(data[0]), {
            dataRes = "${it.type}${it.data[0]} ${it.data[1]}"
            true
        }, {
            errorData = it
        })
        assertTrue(res)
        assertEquals(errorData, -1) 
        assertEquals(data[1], dataRes)
         
    }
    @Test
    fun parseDrawto() {
        val data = arrayOf(
            "M200 100",
            "M200 100"
        )
        var strRes = ""
        var parseRes = false
        parseRes = Path.parseDrawto(Path.Stream(data[0]), {
            strRes += "${it.type}${it.data[0]} ${it.data[1]}"
            true
        }, null)
        assertTrue(parseRes)
        assertEquals(data[1], strRes) 
    }

    
    @Test
    fun parse0() {
        parseWithTest0(
            "M100 200",
            "M100 200" 
        )   
    }
    @Test
    fun parse1() {
        val data = arrayOf(
            "M100 200",
            "M100 200" 
        )   
        var dataRes = ""
        var errorData = -1 
        val res = Path.parse(data[0], {
            dataRes = "${it.type}${it.data[0]} ${it.data[1]}"
            true
        }, {
            errorData = it
        })
        assertTrue(res)
        assertEquals(errorData, -1) 
        assertEquals(data[1], dataRes)
    }


    fun parseWithTest0(data: String, expected: String) {
        var strResArray = ArrayList<String>()
        var errorLoc = -1
        val parseRes = Path.parse(data, {
            strResArray.add("${it.type}" + it.data.joinToString(" ")) 
            true
        }, {
            errorLoc = it
        })
        val strRes = strResArray.joinToString(" ")
        assertEquals(errorLoc, -1)
        assertEquals(expected, strRes) 
        assertTrue(parseRes)
    }

    @Test
    fun parse3() {
        parseWithTest0(
            "M0 0 C100,100 250,100 250,200",
            "M0 0 C100 100 250 100 250 200")
    }

    @Test
    fun parse10() {
        parseWithTest0(
            "M100,200 C100,100 250,100 250,200 S400,300 400,200",
            "M100 200 C100 100 250 100 250 200 S400 300 400 200" 
        )   
 
    }

    @Test
    fun parseCoordinatePairTriplet0() {
        val data = arrayOf(
            "100,200 12 13 34 64",
            "100 200 12 13 34 64"
        )
        val error = -1
        val dataRes = ArrayList<Pair<Int, Int>>()
        val res = Path.parseCoordinatePairTriplet(Path.Stream(data[0]),
            dataRes)
        val indices = ArrayList<Int>()
        dataRes.forEach { indices.addAll(it.toList()) }
        assertEquals(data[1], indices.joinToString(" "))  
        assertTrue(res)
        assertEquals(error, -1)
    }
    @Test
    fun parseCoordinatePairTriplet1() {
        val data = arrayOf(
            "100,100 250,100 250,200",
            "100 100 250 100 250 200"
        )
        val error = -1
        val dataRes = ArrayList<Pair<Int, Int>>()
        val res = Path.parseCoordinatePairTriplet(Path.Stream(data[0]),
            dataRes)
        val indices = ArrayList<Int>()
        dataRes.forEach { indices.addAll(it.toList()) }
        assertEquals(data[1], indices.joinToString(" "))  
        assertTrue(res)
        assertEquals(error, -1)
    }
    @Test
    fun parseCurvetoCoordinateSequance() {
        val data = arrayOf(
            "100,100 250,100 250,200",
            "100 100 250 100 250 200"
        )
        val error = -1
        val dataRes = ArrayList<Pair<Int, Int>>()
        val res = Path.parseCurvetoCoordinateSequence(
            Path.Stream(data[0]),
            dataRes)
        val indices = ArrayList<Int>()
        dataRes.forEach { indices.addAll(it.toList()) }
        assertEquals(data[1], indices.joinToString(" "))  
        assertTrue(res)
        assertEquals(error, -1)
    }
    @Test
    fun parsecurveto0() {
        val data = arrayOf(
            "C100,100 250,100 250,200",
            "C100 100 250 100 250 200"
        )
        var dataRes = ""
        var error = -1
        val res = Path.parseCurveto(Path.Stream(data[0]), {
            dataRes = "${it.type}" + it.data.joinToString(" ")
            true
        }, {
            error = it
        })
        assertEquals(data[1], dataRes)
        assertTrue(res)
    }
    @Test
    fun parseSmoothCurvetoCoordinateSequence() {
        val data = arrayOf(
            "400,300 400,200",
            "400 300 400 200"
        )
        val dataRes = ArrayList<Pair<Int, Int>>() 
        val res = Path.parseSmoothCurvetoCoordinateSequence(
            Path.Stream(data[0]), dataRes)
        val dataStrList = ArrayList<String>()
        dataRes.forEach { 
            dataStrList.add(it.toList().joinToString(" "))
        }
        assertTrue(res)
        assertEquals(data[1], dataStrList.joinToString(" "))
    }
    @Test         
    fun parseSmoothCurveto() {
        val data = arrayOf(
            "S400,300 400,200",
            "S400 300 400 200"
        )
        var dataRes = ""
        var error = -1
        val res = Path.parseSmoothCurveto(Path.Stream(data[0]),
            {
                dataRes = "${it.type}" + it.data.joinToString(" ")
                true
            }, {
                error = it
            })
        assertTrue(res)
        assertEquals(data[1], dataRes)
     }

}
