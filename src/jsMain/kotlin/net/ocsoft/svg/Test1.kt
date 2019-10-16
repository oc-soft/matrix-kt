package net.ocsoft.svg

import jQuery
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Path2D
import kotlin.browser.window

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
    var testData: Array<String>? = null


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
            this.testData = Array<String>(setting.pathData.length) {
                setting.pathData[it] as String
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
                addTestResult(elem, idx)
            }
        }
    }


    /**
     * add test result
     */
    fun addTestResult(pathData: String, id: Int) {
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
            addPath(pathData,
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
        canvasQuery: String) {
        val canvas = jQuery(canvasQuery)
        val ua = window.navigator.userAgent; 
        var path: Path2D? = null
        if (ua.indexOf("Edge") == -1 && !debugInChrome) {
            path = Path2D(pathData)
        } else {
            path = ms.Svg.createPath2D(pathData)
        }
        if (canvas != null && path != null) {
            val canvasElem = canvas[0] as HTMLCanvasElement
            val ctx = canvasElem.getContext("2d") as CanvasRenderingContext2D? 
            ctx?.stroke(path) 
        }
    }
}

