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
    var axisLeading: Double = 30.0) {

    val axisAngle get() = (axisAngleAsDegree / 180) * PI

    val pointsLineAngle: Double
        get() {
            val vec = points[1] - points[0]
            return Path.calcAngle(Pair(1.0, 0.0), vec) 
        }

    val svgHeader = "<svg width=\"12cm\" height=\"5.25cm\" " +
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
    
    
     
    fun createAxis(
        center: Pair<Double, Double>,
        axisAngle: Double,
        radii: Pair<Double, Double>): String {
    
        val cosValue = cos(axisAngle) 
        val sinValue = sin(axisAngle)

        val rotMat = Matrix2(cosValue, -sinValue, sinValue, cosValue)
        
        val sePoints = arrayOf(
            Pair(-radii.first - axisLeading, 0.0),
            Pair(radii.first + axisLeading, 0.0),
            Pair(0.0, -radii.second - axisLeading),
            Pair(0.0, radii.second + axisLeading))
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
            intArrayOf(1, 1),
            intArrayOf(1, 0),
            intArrayOf(1, 1))
        val ellipseParams = Array<Path.EllipseParam>(largeArcAndSweeps.size) {
            Path.resolveArcParam(radii, 
                axisAngle,
                largeArcAndSweeps[it][0],
                largeArcAndSweeps[it][1],
                points[0], points[1])!!
        }
        val svgBodyParts0 = Array<String>(1) {
            val ls = largeArcAndSweeps[it]
            "<path d=\"${createPathData(ls[0], ls[1])}\" " +
            "fill=\"none\" stroke=\"#0f0f0f\" />"  
        }

        val svgBodyParts1 = Array<String>(svgBodyParts0.size) {
            // println(ellipseParams[it])
            val ct = Pair(ellipseParams[it].x, ellipseParams[it].y)
            val radii = Pair(ellipseParams[it].radiusX,
                ellipseParams[it].radiusY)
            //"<path d=\"${createAxis(ct, radii)}\" " +
            // "fill=\"none\" stroke=\"#0f0f0f\" />"
            ""
        }

        var ct = Pair((points[0].first + points[1].first) / 2.0,
            (points[0].second + points[1].second) / 2.0)

        
        var axisStr = createAxis(Pair(100.0, 0.0),
            0.0, radii)
        var svgBody2 = "<path d=\"${axisStr}\" " +
            "fill=\"none\" stroke=\"#0f0f0f\" />"
        svgBody2 = "" 

        var ct0 = Pair(ellipseParams[0].x, ellipseParams[0].y)
        var rad = 20.0
        var centerBody0 = "<path d=\"${createCircle(ct0, rad)}\" " +
            "fill=\"none\" stroke=\"#0f0f0f\" />"
        centerBody0 = ""
        val ellipXaxis = Path.EllipseParam( 
            ellipseParams[0].x, ellipseParams[0].y,
            ellipseParams[0].radiusX,
            ellipseParams[0].radiusY,
            ellipseParams[0].rotation,
            ellipseParams[0].startAngle,
            ellipseParams[0].endAngle,
            // 0.0, PI / 2,
            ellipseParams[0].anticlockwise) 
        var ellVPathd = createEllipseSegment(
            ellipseParams[0].x,
            ellipseParams[0].y,
            ellipseParams[0].radiusX,
            ellipseParams[0].radiusY,
            ellipseParams[0].rotation,
            // ellipseParams[0].startAngle + 1.0 / 2.0,
            // ellipseParams[0].endAngle
            ellipseParams[0].startAngle,
            (ellipseParams[0].startAngle * 1.0 + 
                ellipseParams[0].endAngle * 9.0) / 10.0
            )
        var ellVBody = "<path d=\"${ellVPathd}\" " +
            "fill=\"none\" stroke=\"#0f0f0f\" /> \n"

        //ellVPathd = createVisualEllipseParam(ellipXaxis)
        //ellVBody += "<path d=\"${ellVPathd}\" " +
        //    "fill=\"none\" stroke=\"#0f0f0f\" />"

        // var ellVPathd = createVisualEllipseParam(ellipseParams[0])
        //var ellVBody = "<path d=\"${ellVPathd}\" " +
        //    "fill=\"none\" stroke=\"#0f0f0f\" /> \n"

        //ellVPathd = createVisualEllipseParam(ellipXaxis)
        // ellVBody += "<path d=\"${ellVPathd}\" " +
        //    "fill=\"none\" stroke=\"#0f0f0f\" />"

        


        val svgBody0 = svgBodyParts0.joinToString("\n")
        val svgBody1 = svgBodyParts1.joinToString("\n")
        var result = "${svgHeader} \n" +
            "${centerBody0} \n" +
            "${svgBody0} \n" +
            "${svgBody1} \n" +
            "${svgBody2} \n" +
            "${ellVBody} \n" +
            "</svg> \n" +
            "<!-- center: ${ct0.first}, ${ct0.second} --> \n" +
            "<!-- ${axisAngle} ${axisAngleAsDegree} -->\n" +
            "<!-- ${radii} ${points[0]} ${points[1]} " +
            "${axisAngleAsDegree} --> \n"
            "<!-- ${ellipseParams[0]} -->"

        return result
    }

    fun parseOption(jsonStr : String) {
        val mgr = ScriptEngineManager()
        val eng = mgr.getEngineByName("JavaScript")
        var evalContent = "(${jsonStr})"
        var rootObj = eng.eval(evalContent) as Map<String, Object>

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
