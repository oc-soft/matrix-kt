package net.ocsoft.gen.img

import net.ocsoft.Path
import net.ocsoft.Matrix2

import kotlin.math.*
import kotlin.collections.HashMap
import kotlin.collections.ArrayList
import kotlin.collections.MutableList


import javax.script.*
import jdk.nashorn.api.scripting.JSObject
import java.util.Map
import java.io.Reader
import java.io.FileReader
import java.io.BufferedReader
import java.io.FileNotFoundException


operator fun Pair<Double, Double>.plus(
    value: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(this.first + value.first, this.second + value.second)
}

operator fun Pair<Double, Double>.minus(
    value: Pair<Double, Double>): Pair<Double, Double> {
    return Pair(this.first - value.first, this.second - value.second)
}


class CenterFromPoints( 
    var radii: Pair<Double, Double> = Pair(200.0, 100.0),
    var points: Array<Pair<Double, Double>> = 
        arrayOf(Pair(50.0, 0.0), Pair(200.0, 10.0)),
    var axisAngleAsDegree: Double = 30.0,
    var axisLeading: Double = 30.0,
    var arrowHeadSize: Double = 10.0,
    var globalAxis: Array<Pair<Double, Double>> = arrayOf(
            Pair(-10.0, 400.0),
            Pair(-10.0, 300.0)),
    val ellipseVisibility: BooleanArray = booleanArrayOf(
        true, true, true, true),
    val ellipseAxisVisibility: BooleanArray = booleanArrayOf(
        true, true, false, false)) {

    companion object {
        fun joinMultilines(mapObj : Map<String, Any?>): String {
            val strList = ArrayList<String>()
            for (i in 0 .. mapObj.size() - 1) {
                val elm = mapObj["${i}"]
                if (elm is String) {
                    strList.add(elm as String) 
                }
            }
            return strList.joinToString("") 
        }
        fun parseDoubleList(idxMap: Map<String, Any?>): List<Double> {
            val result = ArrayList<Double>() 
            for (i in 0 .. idxMap.size()  -  1) {
                val elm = idxMap["${i}"]
                if (elm != null) {
                    if (elm is Number) {
                        result.add((elm as Number).toDouble())
                    } else if (elm is String) {
                        result.add((elm as String).toDouble()) 
                    }
                }
            }
            return result 
        }
        fun parseBooleanList(idxMap: Map<String, Any?>): List<Boolean> {
            val result = ArrayList<Boolean>() 
            for (i in 0 .. idxMap.size()  -  1) {
                val elm = idxMap["${i}"]
                if (elm != null) {
                    if (elm is Number) {
                        val tmpVal = (elm as Number).toDouble() 
                        result.add(tmpVal !=  0.0 && tmpVal != -0.0)
                    } else if (elm is String) {
                        result.add((elm as String).toBoolean()) 
                    }
                }
            }
            return result 
        }
   }


    val axisAngle get() = (axisAngleAsDegree / 180) * PI

    val pointsLineAngle: Double
        get() {
            val vec = points[1] - points[0]
            return Path.calcAngle(Pair(1.0, 0.0), vec) 
        }

    var svgHeader = "<svg width=\"12cm\" height=\"5.25cm\" " +
        "viewBox=\"0 0 1200 400\"\n" +
        "xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">"
              
    
    fun createPathData(largeArcFlag: Int, sweepFlag: Int): String {
        
        return createPathData(
            points[0], points[1],
            radii.first, radii.second,
            axisAngleAsDegree, largeArcFlag, sweepFlag) 
    } 

    fun createPathData(
        point0: Pair<Double, Double>,
        point1: Pair<Double, Double>,
        xAxis: Double, yAxis: Double,
        axisAngleAsDegree: Double,
        largeArcFlag: Int, sweepFlag: Int): String {
        return "M ${point0.first} ${point0.second} " +
            "A ${xAxis} ${yAxis} ${axisAngleAsDegree} " +
            "${largeArcFlag} ${sweepFlag} " +
            "${point1.first} ${point1.second}"

    } 

    fun createCircle(
        center: Pair<Double, Double>,
        radius: Double): String {
        val pt = arrayOf(
            Pair(center.first - radius, center.second),
            Pair(center.first + radius, center.second))
        return createPathData(pt[0], pt[1], radius, radius, 0.0, 0, 0) + " " +
            createPathData(pt[1], pt[0], radius, radius, 180.0, 1, 1)

    }

    fun createAxis(center: Pair<Double, Double>,
        radii: Pair<Double, Double>): String {
        return createAxis(center, axisAngle, radii)
    }


    fun createEllipseParamMatrices(ellipseParam: Path.EllipseParam): 
        Array<Matrix2> {
        val cosValue0 = cos(ellipseParam.rotation)
        val sinValue0 = sin(ellipseParam.rotation)
        val cosValue1 = cos(ellipseParam.startAngle)
        val sinValue1 = sin(ellipseParam.startAngle)
        val cosValue2 = cos(ellipseParam.endAngle)
        val sinValue2 = sin(ellipseParam.endAngle)

        return arrayOf(
            Matrix2(cosValue0, -sinValue0, sinValue0, cosValue0),
            Matrix2(cosValue1, -sinValue1, sinValue1, cosValue1),
            Matrix2(cosValue2, -sinValue2, sinValue2, cosValue2))
    }


    fun createVisualEllipseParam(
        ellipseParam: Path.EllipseParam): String {
        val radii = doubleArrayOf(ellipseParam.radiusX, ellipseParam.radiusY)
        val ct = doubleArrayOf(ellipseParam.x, ellipseParam.y)
        
        var p1 = calcEllipsePoint(ellipseParam.x, ellipseParam.y,
            ellipseParam.radiusX, ellipseParam.radiusY,
            ellipseParam.rotation, ellipseParam.startAngle)
        var p2 = calcEllipsePoint(ellipseParam.x, ellipseParam.y,
            ellipseParam.radiusX, ellipseParam.radiusY,
            ellipseParam.rotation, ellipseParam.endAngle)
        val result = createLine(Pair(ct[0], ct[1]), Pair(p1[0], p1[1])) +
            " " + createLine(Pair(ct[0], ct[1]), Pair(p2[0], p2[1]))
        return result
    }

    fun calcEllipsePoint(
        centerX: Double,
        centerY: Double,
        xAxis: Double,
        yAxis: Double,
        rotation: Double,
        theta: Double) : DoubleArray {

        val cosValue0 = cos(rotation)
        val sinValue0 = sin(rotation)
        val mat0 = Matrix2(cosValue0, -sinValue0, sinValue0, cosValue0)
         
        val cosValue1 = cos(theta)
        val sinValue1 = sin(theta)
        
        var p1 = doubleArrayOf(xAxis * cosValue1, yAxis * sinValue1)
        p1 = mat0 * p1 
        return doubleArrayOf(p1[0] + centerX, p1[1] + centerY)
    } 

    fun createEllipseSegment(centerX: Double,
        centerY: Double,
        xAxis: Double,
        yAxis: Double,
        rotation: Double,
        theta0: Double,
        theta1: Double): String {
        val pt = arrayOf(
            calcEllipsePoint(centerX, centerY,
                xAxis, yAxis, rotation, theta0),
            calcEllipsePoint(centerX, centerY,
                xAxis, yAxis, rotation, theta1))
        return createLine(Pair(pt[0][0], pt[0][1]),
            Pair(pt[1][0], pt[1][1]))
            
    }
    
   
    fun createArrowHeadPoints(
        point: Pair<Double, Double>,
        direction: Pair<Double, Double>,
        radius: Double): Array<Pair<Double, Double>> {
       
        var dirCos = 0.0
        var dirSign = 1.0
        val dirLen = sqrt(direction.first.pow(2.0) + direction.second.pow(2.0)) 
        if (dirLen > 0) {
            dirCos = direction.first / dirLen
            dirCos = max(min(dirCos, 1.0), -1.0)
            if (direction.second != 0.0 && direction.second != -0.0) {
                dirSign = sign(direction.second)
            }
        }
        val startAngle = acos(dirCos) * dirSign           
        val points = Array<Pair<Double, Double>>(3) {
            val rad = (2 * PI * ((it + 2) % 3)) / 3.0 + startAngle
            Pair(radius * cos(rad), radius * sin(rad))
        }
        val disp = point - points[1]  
        for (i in 0 .. points.size - 1) {
            points[i] = Pair(points[i].first + disp.first,
                points[i].second + disp.second)
        }
        
        val result = points
        return result
    }
    fun createArrowHeadPoly(point: Pair<Double, Double>,
        direction: Pair<Double, Double>,
        radius: Double): String {
        val points = createArrowHeadPoints(point, direction, radius)
        val flatPoints = DoubleArray(points.size * 2) {
            val idx = it / 2
            val elmIdx = it % 2
            points[idx].toList()[elmIdx]
        }

        val ptStr = flatPoints.joinToString(" ")
        val result = "<polyline points=\"${ptStr}\" />"
        return result
    }



    fun createGlobalAxis(): String {
        
        return createArrowAxis(
            Pair(0.0, 0.0), 
            0.0, globalAxis[0], globalAxis[1],
            arrowHeadSize) 
        
    }

    fun createArrowAxis(
        center: Pair<Double, Double>,
        axisAngle: Double,
        xRange: Pair<Double, Double>,
        yRange: Pair<Double, Double>,
        arrowHeadSize: Double) : String {
        val axisPathD = createAxis(center, axisAngle, 
            xRange, yRange)      

        val pathDStr = "<path d=\"${axisPathD}\" />"
 
        val mRot = Matrix2().rotate(axisAngle)

        var arrowPt = mRot * Pair(xRange.second, 0.0)
        arrowPt += center
        val xArrowHead = createArrowHeadPoly(
            arrowPt,
            mRot * Pair(1.0, 0.0),
            arrowHeadSize)
        val rotMat = Matrix2.rotate(axisAngle)
        arrowPt =  mRot * Pair(0.0, yRange.second)
        arrowPt += center
        val yArrowHead = createArrowHeadPoly(
            arrowPt,
            mRot * Pair(0.0, 1.0),
            arrowHeadSize)

        val result = "<g fill=\"none\" stroke=\"#0f0f0f\" >" + 
            pathDStr + xArrowHead + yArrowHead + "</g>"
        
        return result 
    }    
     
    
    fun createAxis(
        center: Pair<Double, Double>,
        axisAngle: Double,
        radii: Pair<Double, Double>): String {
        return createAxis(center, axisAngle, radii, axisLeading)
    } 
     
    fun createAxis(
        center: Pair<Double, Double>,
        axisAngle: Double,
        radii: Pair<Double, Double>,
        axisLeading: Double): String {
   
        
        val result = createAxis(center, axisAngle,
            Pair(-radii.first - axisLeading, radii.first + axisLeading),
            Pair(-radii.second - axisLeading, radii.second + axisLeading))
        return result
    }

    fun createAxis(
        center: Pair<Double, Double>,
        axisAngle: Double,
        xRange: Pair<Double, Double>,
        yRange: Pair<Double, Double>): String {
    
        val cosValue = cos(axisAngle) 
        val sinValue = sin(axisAngle)

        val rotMat = Matrix2(cosValue, -sinValue, sinValue, cosValue)
        
        
        val sePoints = arrayOf(
            Pair(xRange.first, 0.0),
            Pair(xRange.second, 0.0),
            Pair(0.0, yRange.first),
            Pair(0.0, yRange.second))
        for (i in 0 .. sePoints.size - 1) {
            sePoints[i] = rotMat * sePoints[i]
            sePoints[i] += center
        }   
        return "M ${sePoints[0].first} ${sePoints[0].second} " +
            "L ${sePoints[1].first} ${sePoints[1].second} " +
            "M ${sePoints[2].first} ${sePoints[2].second} " +
            "L ${sePoints[3].first} ${sePoints[3].second}"
    }

 
    fun createLine(point0: Pair<Double, Double>,
        point1: Pair<Double, Double>): String {
        return "M ${point0.first} ${point0.second} " +
            "L ${point1.first} ${point1.second}"
    } 
    

    fun generate() : String {
        val largeArcAndSweeps = arrayOf(
            intArrayOf(0, 0),
            intArrayOf(0, 1),
            intArrayOf(1, 0),
            intArrayOf(1, 1))
        val ellipseParams = Array<Path.EllipseParam>(largeArcAndSweeps.size) {
            Path.resolveArcParam(radii, 
                axisAngle,
                largeArcAndSweeps[it][0],
                largeArcAndSweeps[it][1],
                points[0], points[1])!!
        }
        val svgBodyParts0 = Array<String>(largeArcAndSweeps.size) {
            val ls = largeArcAndSweeps[it]
            var strRes = ""
            if (ellipseVisibility[it]) {
                strRes = "<path d=\"${createPathData(ls[0], ls[1])}\" " +
                    "fill=\"none\" stroke=\"#0f0f0f\" />"  
            }
            strRes
        }

        val svgBodyParts1 = Array<String>(svgBodyParts0.size) {
            val ct = Pair(ellipseParams[it].x, ellipseParams[it].y)
            val radii = Pair(ellipseParams[it].radiusX,
                ellipseParams[it].radiusY)
            var strRes = ""
            if (ellipseAxisVisibility[it]) {
                strRes = "<path d=\"${createAxis(ct, radii)}\" " +
                    "fill=\"none\" stroke=\"#0f0f0f\" />"
            }
            strRes
        }

        

        val svgBody0 = svgBodyParts0.joinToString("\n")
        val svgBody1 = svgBodyParts1.joinToString("\n")
        var result = "${svgHeader} \n" +
            "${createGlobalAxis()} \n" +
            "${svgBody0} \n" +
            "${svgBody1} \n" +
            "</svg>"
        return result
    }

    fun parseOption(rootObj: Map<String, Object>) {
        val radii = rootObj["radii"] as Map<String, Object>
        var res = "" 
        val radii0 = doubleArrayOf(this.radii.first, this.radii.second)
        for (i in 0..1) {
            val num = radii["${i}"]            
            if (num != null) {
                if ((num as Any?) is String) {
                    radii0[i] = (num as String).toDouble()
                } else {
                    radii0[i] = (num as java.lang.Number).doubleValue()         
                }
            }
        } 
        this.radii = Pair(radii0[0], radii0[1])
        val points0 = doubleArrayOf(
            this.points[0].first, this.points[0].second,
            this.points[1].first, this.points[1].second) 

        val points = rootObj["points"] as Map<String, Object>
        for (i in 0..3) {
            val num = points["${i}"]            
            if (num != null) {
                if ((num as Any?) is String) {
                    points0[i] = (num as String).toDouble()
                } else {
                    points0[i] = (num as java.lang.Number).doubleValue()
                }
            }
        }
        this.points[0] = Pair(points0[0], points0[1])
        this.points[1] = Pair(points0[2], points0[3])
        val axis = rootObj["axis"]
        if (axis != null) {
            var axisValue = 0.0
            if ((axis as Any?) is String) {
                val axisStr = axis as String
                axisValue = axisStr.toDouble() 
            } else {
                axisValue = (axis as java.lang.Number).doubleValue()
            }
            this.axisAngleAsDegree = axisValue
        }
        val axisLeading = rootObj["axisLeading"]
        if (axisLeading != null) {
            var leadingValue = 0.0
            if ((axisLeading as Any?) is String) {
                val leadingStr = axisLeading as String
                leadingValue = leadingStr.toDouble()
            } else {
                leadingValue = (axisLeading as java.lang.Number).doubleValue()
            }
            this.axisLeading = leadingValue
        }
 
        val svgHeader = rootObj["header"]  
        if (svgHeader != null) {
            if ((svgHeader as Any?) is String) {
                this.svgHeader = svgHeader as String
            } else if ((svgHeader as Any?) is Map<*, *>) {
                this.svgHeader = joinMultilines(svgHeader as Map<String, Any?>)
            }
        }

        val axisKeys = arrayOf("xAxisRange", "yAxisRange")
        axisKeys.forEachIndexed {
            idx, elem ->
            val axisRangeObj = rootObj[elem]
            if (axisRangeObj != null) {
                if (axisRangeObj is Map<*, *>) {
                    val idxMap = axisRangeObj as Map<String, Any?>
                    val numberList = parseDoubleList(idxMap)
                    if (numberList.size > 1) {
                        globalAxis[idx] = Pair(numberList[0], numberList[1]) 
                    }
                }
            }
        }
        var visibilityObj = rootObj["visibility"]
        if (visibilityObj != null) {
            if (visibilityObj is Map<*, *>) {
                val ellipseVisibility = visibilityObj as Map<String, Any?>
                val boolList = parseBooleanList(ellipseVisibility) 
                boolList.forEachIndexed {
                    idx, elm ->
                    this.ellipseVisibility[idx] = elm 
                }
            } 
        }
        visibilityObj = rootObj["axisVisibility"]
        if (visibilityObj != null) {
            if (visibilityObj is Map<*, *>) {
                val visibility = visibilityObj as Map<String, Any?>
                val boolList = parseBooleanList(visibility) 
                boolList.forEachIndexed {
                    idx, elm ->
                    this.ellipseAxisVisibility[idx] = elm 
                }
            } 
        }
    }

    fun parseOption(jsonStr : String) {
        val mgr = ScriptEngineManager()
        val eng = mgr.getEngineByName("JavaScript")
        var evalContent = "eval(${jsonStr})"
        var rootObj = eng.eval(evalContent) as Map<String, Object>
        parseOption(rootObj)

    }

    override fun toString(): String {
        return generate()
    }


    fun parseOption(args: Array<String>) {
        val optionsMap = HashMap<String, (Array<String>, IntArray)->Unit>()

        optionsMap["-f"] = { 
            args, ioIndex ->
            handleJsonFile(args, ioIndex)
        }

        var i = 0
        while (i < args.size) {
            val hdlr = optionsMap[args[i]] 
            if (hdlr != null) {
                val ioIdx = intArrayOf(i + 1)
                hdlr(args, ioIdx)  
                i = ioIdx[0]
            } else {
                i++
            }
        }
    } 
    
    fun handleJsonFile(args: Array<String>,
        ioIndex: IntArray) {
        if (ioIndex[0] < args.size) {
            val filePath = args[ioIndex[0]]
            val strContents = loadContents(filePath)
            ioIndex[0]++
            if (strContents.isNotEmpty()) {
                parseOption(strContents)
            }
        }
    } 
    
    fun loadContents(filePath: String): String {
        var fr: Reader? = null   
        var buffer = CharArray(1024)
        var strList = ArrayList<String>()
        try {
            fr = FileReader(filePath)
            do {
                val sizeRead = fr.read(buffer, 0, buffer.size)
                if (sizeRead > 0) {
                    var str = ""
                    if (sizeRead < buffer.size) {
                        str = String(buffer.sliceArray(0..sizeRead - 1))
                    } else {
                        str = String(buffer)
                    }
                    strList.add(str)
                }
                if (sizeRead <= 0) {
                    break
                }
            } while (true)
        } catch (ex :FileNotFoundException) {
            println(ex)
        } finally {
            if (fr != null) {
                fr.close()
            }
        }
        return strList.joinToString("")     
    }
}


/**
 * entry point
 */
fun main(args: Array<String>) {
    val app = CenterFromPoints()

    app.parseOption(args)

    println(app)
}
