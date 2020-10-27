package net.ocsoft.svg

import jQuery
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D
import kotlinx.browser.window

/**
 * test runner for svg
 */
class Test1 {

    /**
     * class instance
     */
    companion object {
        @JsName("run")
        fun run(setting: dynamic) {
            val test1 = Test1()
            test1.bind(setting)
            test1.run()
        }
    } 

    /**
     * test data
     */
    var testData: Array<Pair<String, Double>>? = null


    /**
     * test result list query
     */
    var resultListQuery: String? = null


    /**
     * result item 
     */
    var resultItemTemplateQuery: String? = null

    /**
     * you can debug in chrome if true
     */
    var debugInChrome: Boolean = false
    /**
     * bind test setting 
     */
    fun bind(setting: dynamic) {
        if (setting.pathData != null) {
            val dataLen = setting.pathData.length
            val pathData = setting.pathData
            this.testData = Array<Pair<String, Double>>(dataLen) {
                var strData: String
                var scale = 1.0
                val pathItem = pathData[it]
                if (js("typeof pathItem") == "object") {
                    val elm = pathItem
                    strData = elm[0] as String
                    if (elm.length > 1) {
                        val scaleItem = elm[1]
                        if (js("typeof scaleItem") == "string") {
                            scale = scaleItem.toDouble()
                        } else {
                            scale = scaleItem
                        }
                    }
                } else {
                    strData = pathData[it] 
                }
                Pair(strData, scale)      
            }
        }
        this.resultListQuery =
            setting.resultListQuery as String?
        this.resultItemTemplateQuery =
            setting.resultItemTemplateQuery as String?
        if (setting.debugInChrome != null) {
            debugInChrome = setting.debugInChrome as Boolean
        }
    }

    /**
     * run test
     */
    fun run() {
        val testData = this.testData
        if (testData != null) {
            testData.forEachIndexed {
                idx, elem ->
                addTestResult(elem.first, idx, elem.second)
            }
        }
    }


    /**
     * add test result
     */
    fun addTestResult(pathData: String, id: Int, scale: Double) {
        val resultListQuery = this.resultListQuery
        val resultItemTemplateQuery = this.resultItemTemplateQuery
        if (resultListQuery != null
            && resultItemTemplateQuery != null) {
            val str = jQuery(resultItemTemplateQuery).html()
            val node = jQuery.parseHTML(str)   
            val testItems = jQuery(resultListQuery)
            testItems.append(node)  
            val testDataClass = "res-${id}"
            jQuery(node).addClass(testDataClass)
            assignPathDataTitle(pathData,
                "${resultListQuery} .${testDataClass} div") 
            addPath(pathData, scale,
                "${resultListQuery} .${testDataClass} canvas")
        }
    }

    /**
     * add path data title
     */
    fun assignPathDataTitle(pathData: String,
        itemTitleQuery: String) {
        jQuery(itemTitleQuery).text(pathData) 
    }


    /**
     * add path into canvas
     */
    fun addPath(pathData: String,
        scale: Double,
        canvasQuery: String) {
        val canvas = jQuery(canvasQuery)
        val ua = window.navigator.userAgent; 
        var path: Path2D?
        if (ua.indexOf("Edge") == -1 && !debugInChrome) {
            path = Path2D(pathData)
        } else {
            path = ms.Svg.createPath2D(pathData)
        }
        val canvasElem = canvas[0] as HTMLCanvasElement
        val ctx = canvasElem.getContext("2d") as CanvasRenderingContext2D? 
        ctx?.save()
        ctx?.setTransform(scale, 0.0, 0.0, scale, 0.0, 0.0)
        // ctx?.fill(path)
        ctx?.stroke(path) 
        ctx?.restore()
    }
}

