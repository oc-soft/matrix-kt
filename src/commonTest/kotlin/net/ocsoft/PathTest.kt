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
            assertEquals(it.first, it.first.toInt().toDouble())
            assertEquals(it.second, it.second.toInt().toDouble())
            dataRes = "${it.first.toInt()} ${it.second.toInt()}"
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
        val coordinatePairs = ArrayList<Pair<Double, Double>>()
        val res = Path.parseCoordinatePairSequence(Path.Stream(data), 
            coordinatePairs)
        assertTrue(res)
        coordinatePairs.forEachIndexed {
            idx, elem ->
            assertEquals(elem.first, elem.first.toInt().toDouble())
            assertEquals(elem.second, elem.second.toInt().toDouble())
            dataRes += "${elem.first.toInt()} ${elem.second.toInt()}"      
            if (idx < coordinatePairs.size - 1) {
                dataRes += " "
            }
        }
        assertEquals(data, dataRes) 
    }

    @Test
    fun parseNumber0() {
        val data = "200"
        var number = 0.0
        val res = Path.parseNumber(Path.Stream(data),  { number = it }) 
        assertTrue(res)
        assertEquals(number, number.toInt().toDouble())
        assertEquals(number.toInt().toString(), data)
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
            assertEquals(it.data[0], it.data[0].toInt().toDouble())
            assertEquals(it.data[1], it.data[1].toInt().toDouble())
            strRes += "${it.type}${it.data[0].toInt()} ${it.data[1].toInt()}"
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
            assertEquals(it.data[0], it.data[0].toInt().toDouble())
            assertEquals(it.data[1], it.data[1].toInt().toDouble())
            dataRes = "${it.type}${it.data[0].toInt()} ${it.data[1].toInt()}"
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
            assertEquals(it.data[0], it.data[0].toInt().toDouble())
            assertEquals(it.data[1], it.data[1].toInt().toDouble())
            strRes += "${it.type}${it.data[0].toInt()} ${it.data[1].toInt()}"
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
            assertEquals(it.data[0], it.data[0].toInt().toDouble())
            assertEquals(it.data[1], it.data[1].toInt().toDouble())
            dataRes = "${it.type}${it.data[0].toInt()} ${it.data[1].toInt()}"
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
            var strDataList = ArrayList<String>()
            it.data.forEach {
                assertEquals(it, it.toInt().toDouble()) 
                strDataList.add("${it.toInt()}")
            }

            strResArray.add("${it.type}" + strDataList.joinToString(" ")) 
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
        val dataRes = ArrayList<Pair<Double, Double>>()
        val res = Path.parseCoordinatePairTriplet(Path.Stream(data[0]),
            dataRes)
        val indices = ArrayList<Int>()
        dataRes.forEach { 
            assertEquals(it.first, it.first.toInt().toDouble())
            assertEquals(it.second, it.second.toInt().toDouble())
            
            indices.add(it.first.toInt())
            indices.add(it.second.toInt())
        }
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
        val dataRes = ArrayList<Pair<Double, Double>>()
        val res = Path.parseCoordinatePairTriplet(Path.Stream(data[0]),
            dataRes)
        val indices = ArrayList<Int>()
        dataRes.forEach {
            assertEquals(it.first, it.first.toInt().toDouble())
            assertEquals(it.second, it.second.toInt().toDouble())
            indices.add(it.first.toInt())
            indices.add(it.second.toInt())
        }
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
        val dataRes = ArrayList<Pair<Double, Double>>()
        val res = Path.parseCurvetoCoordinateSequence(
            Path.Stream(data[0]),
            dataRes)
        val indices = ArrayList<Int>()
        dataRes.forEach {
            assertEquals(it.first, it.first.toInt().toDouble())
            assertEquals(it.second, it.second.toInt().toDouble())
            indices.add(it.first.toInt())
            indices.add(it.second.toInt())
        }
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
            val dataArray = ArrayList<Int>()
            it.data.forEach {
                assertEquals(it, it.toInt().toDouble())
                dataArray.add(it.toInt())
            }
            dataRes = "${it.type}" + dataArray.joinToString(" ")
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
        val dataRes = ArrayList<Pair<Double, Double>>() 
        val res = Path.parseSmoothCurvetoCoordinateSequence(
            Path.Stream(data[0]), dataRes)
        val dataStrList = ArrayList<String>()
        dataRes.forEach { 
            val dataArray = ArrayList<Int>()
            assertEquals(it.first, it.first.toInt().toDouble())
            assertEquals(it.second, it.second.toInt().toDouble())
            dataArray.add(it.first.toInt())
            dataArray.add(it.second.toInt())
            dataStrList.add(dataArray.joinToString(" "))
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
                val dataArray = ArrayList<Int>()
                it.data.forEach {
                    assertEquals(it, it.toInt().toDouble())
                    dataArray.add(it.toInt())
                }            
                dataRes = "${it.type}" + dataArray.joinToString(" ")
                true
            }, {
                error = it
            })
        assertTrue(res)
        assertEquals(data[1], dataRes)
    }
   

    @Test
    fun parseRelativeCurvetoSequence() { 
        val data = "c0 70.1 36.9 132.6 94.5 173.7 9.6 6.9 15.2 18.1 13.5 29.9"
        val coordData = doubleArrayOf(
            0.0, 70.1, 36.9, 132.6, 94.5, 173.7,
            9.6, 6.9, 15.2, 18.1, 13.5, 29.9
        )
        val res = Path.parseDrawto(Path.Stream(data), {
            assertEquals(it.type, Path.ElementType.c)   
            it.data.forEachIndexed {
                idx, elm ->
                assertEquals(coordData[idx], elm)
            }
            true
        }) {
            assertEquals(it, -1)
        }
        assertTrue(res)
        
    }
    @Test
    fun parseCurvetoSequence1() {
        val data = "C114.6 0 0 100.3 0 224" +
            "c0 70.1 36.9 132.6 94.5 173.7" +
                " 9.6 6.9 15.2 18.1 13.5 29.9" +
            "l-9.4 66.2"
        val resData = arrayOf(
            doubleArrayOf(
                114.6, 0.0, 0.0, 100.3, 0.0, 224.0),
            doubleArrayOf(
                0.0, 70.1, 36.9, 132.6, 94.5, 173.7, 9.6, 6.9, 15.2, 
                18.1, 13.5, 29.9),
            doubleArrayOf(
                -9.4, 66.2))
        val elemData = Array<List<Double>>(resData.size) {
            val dataSrc = resData[it]
            val list = ArrayList<Double>()
            dataSrc.forEach {
                list.add(it)
            }
            list
        }
        val elemTypes = arrayOf(Path.ElementType.C,
            Path.ElementType.c,
            Path.ElementType.l)
        val resElements = Array<Path.Element>(elemTypes.size) {
            Path.Element(elemTypes[it], elemData[it])
        }
        val resList = ArrayList<Path.Element>()
        val stream = Path.Stream(data)
        do { 
            val parseRes = Path.parseDrawto(stream, {
                resList.add(it)        
            }, {
                assertEquals(it, -1)
            })
            assertTrue(parseRes)
        } while(stream.peekChar != null)
        
        assertEquals(resList.size, resElements.size)
        resList.forEachIndexed {
            idx0, elm ->
            assertEquals(resElements[idx0].type, elm.type)
            elm.data.forEachIndexed {
                idx1, elmData ->
                assertEquals(resElements[idx0].data[idx1], elmData)
            }
        }  

    }
}
/* vi: se ts=4 sw=4 et: */
