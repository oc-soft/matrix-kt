package net.ocsoft.geom

import net.ocsoft.Path
import kotlin.math.*


import java.io.Reader
import java.io.FileReader
import java.io.FileNotFoundException
import javax.script.*


class EllipseCenter(
    var radii: Pair<Double, Double> = Pair(1.0, 1.0),
    var largeArc: Boolean = false,
    var sweep: Boolean = false,
    val points: Array<Pair<Double, Double>> =  arrayOf(
        Pair(1.0, 0.0), Pair(0.0, 1.0)),
    var axisAngleAsDegree: Double = 0.0) {

    val axisAngle get() = (axisAngleAsDegree / 180) * PI


    fun parseOption(rootObj: Map<String, Any>) {
          
        val radii = rootObj["radii"] as Map<String, Any>
        val radii0 = doubleArrayOf(this.radii.first, this.radii.second)
        for (i in 0..1) {
            val num = radii["${i}"]            
            if (num != null) {
                if (num is String) {
                    radii0[i] = num.toDouble()
                } else {
                    radii0[i] = (num as Number).toDouble()         
                }
            }
        } 
        this.radii = Pair(radii0[0], radii0[1])
        val points0 = doubleArrayOf(
            this.points[0].first, this.points[0].second,
            this.points[1].first, this.points[1].second) 

        val points = rootObj["points"] as Map<String, Any>
        for (i in 0..3) {
            val num = points["${i}"]            
            if (num != null) {
                if (num is String) {
                    points0[i] = num.toDouble()
                } else {
                    points0[i] = (num as Number).toDouble()
                }
            }
        }
        this.points[0] = Pair(points0[0], points0[1])
        this.points[1] = Pair(points0[2], points0[3])
        val axis = rootObj["axis"]
        if (axis != null) {
            var axisValue : Double
            if (axis is String) {
                val axisStr = axis as String
                axisValue = axisStr.toDouble() 
            } else {
                axisValue = (axis as Number).toDouble()
            }
            this.axisAngleAsDegree = axisValue
        }

        val largeArcObj = rootObj["largeArc"]
        if (largeArcObj != null) {
            if (largeArcObj is String) {
                this.largeArc = (largeArcObj as String).toBoolean()
            }
        }
        val sweepObj = rootObj["sweep"]
        if (sweepObj != null) {
            if (sweepObj is String) {
                this.sweep = (sweepObj as String).toBoolean()
            }
        }
  
    }
    fun parseOption(jsonStr : String) {
        val mgr = ScriptEngineManager()
        val eng = mgr.getEngineByName("JavaScript")
        var evalContent = "(${jsonStr})"
        val option = eng.eval(evalContent) as Map<String, Any>
        parseOption(option)
    }


    fun parseOption(args: Array<String>) {
        val optionsMap = HashMap<String, (Array<String>, IntArray)->Unit>()

        optionsMap["-f"] = { 
            args1, ioIndex ->
            handleJsonFile(args1, ioIndex)
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
                    var str: String
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

    fun run() {
        var largeArc = 0 
        if (this.largeArc) {
            largeArc = 1
        }
        var sweep = 0
        if (this.sweep) {
            sweep = 1
        }
        

        val param = Path.resolveArcParam(radii, 
                axisAngle,
                largeArc,
                sweep,
                points[0], points[1])!!

        println("${points[0]}, ${points[1]}")
        println(param)
    }
}


fun main(args: Array<String>){

    val app = EllipseCenter()
    app.parseOption(args)
    app.run()
  
}


