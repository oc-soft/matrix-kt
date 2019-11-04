package net.ocsoft.gen.img

import java.io.File
import java.io.FileWriter
import java.io.FileReader
import java.io.BufferedReader
import java.io.Writer
import java.io.Reader
import java.io.IOException
import java.io.FileNotFoundException

import java.util.Map

import javax.script.*


class Svg {

    var option: Map<String, Object>? = null


    fun run() {
        var svgOption = option?.get("svg") as Map<String, Object>?
        var destOption = svgOption?.get("dest") as Map<String, Object>?
        val templateOption = destOption?.get("template") as Map<String, Object>?
        val rootDir = destOption?.get("root") as String?
        val imgList = ArrayList<Pair<File, String>>()
        if (svgOption != null  
            && rootDir != null && templateOption != null) {
            val destDirFile = File(rootDir)
            var ellipseOptionObj = option?.get("ellipseCenter")
            var ellipseOption = ellipseOptionObj as Map<String, Object>?
            if (ellipseOption != null) {
                val ftmpObj = templateOption["ellipseCenter"] 
                val fileTemplate = ftmpObj as String?
                if (fileTemplate != null) {
                    for (idx in 0 .. ellipseOption?.size() - 1) {
                        val paramObj = ellipseOption["${idx}"]
                        var param = paramObj as Map<String, Object>?
                        if (param != null) {
                            val imgGen = CenterFromPoints()
                            imgGen.parseOption(param!!)
                            val fName = String.format(fileTemplate, 
                                idx as Any?)
                            val f = File(destDirFile, fName)
                            imgList.add(Pair(f, imgGen.generate()))
                        }
                    }
                }
            }      
        }
        imgList.forEach {
            var fw : Writer? = null
            try {
                fw = FileWriter(it.first)
                fw?.write(it.second) 
            } catch (ex: IOException) {
            } finally {
                if (fw != null) {
                    fw.close()
                }
            }
        }
    }

    fun parseOption(jsonStr : String) {
        val mgr = ScriptEngineManager()
        val eng = mgr.getEngineByName("JavaScript")
        var evalContent = "eval(${jsonStr})"
        option = eng.eval(evalContent) as Map<String, Object>

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

fun main(args: Array<String>){

    val app = Svg()
    app.parseOption(args)
    app.run()
  
}


