package net.ocsoft.mswp.ui

import jQuery
import JQuery
import JQueryAjaxSettings
import kotlin.js.Promise
import kotlin.collections.Map
import kotlin.collections.HashMap
import kotlin.browser.window
import kotlin.text.toIntOrNull
import kotlin.math.roundToInt
import kotlin.math.min
import kotlin.math.max
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import org.w3c.xhr.FormData

import net.ocsoft.mswp.ColorScheme

/**
 * manage user interface persistence data.
 */
class Persistence {

   
    /**
     * class instance
     */
    companion object {
        
        /**
         * load icon
         */
        fun loadIcon(): Promise<Map<String, Icon>?> {
            val result = Promise<Map<String, Icon>?> {
                resolve, reject ->  

                val body = FormData()
                body.append("icon", "read")
                var prm = window.fetch("persistence.php",
                    RequestInit(
                        method = "POST",
                        body = body)) 
                prm.then(
                    { it.json() }
                ).then({
                    res ->
                    val res0: dynamic = res 
                    var iconData: Map<String, Icon>? = null
                    if (res0.data != null) { 
                        val tmpIconData = JSON.parse<dynamic>(res0.data)
                        iconData = dynamicToIconMap(tmpIconData)
                    }
                    resolve(iconData) 
                }).catch({
                    reason ->
                    reject(Throwable(reason))
                })
            } 
            return result 
        }

        /**
         * save icon setting
         */
        fun saveIcon(data: Map<String, Icon>): Promise<Any?> {
            val strData = JSON.stringify(iconMapToDynamic(data)) 
            val result = Promise<Any?>({
                resolve, reject ->
                val body = FormData()
                body.append("icon", "write")
                body.append("data", strData)

                window.fetch("persistence.php",
                    RequestInit(
                        method = "POST",
                        body = body)).then({
                        res ->
                        res.json()
                    }).then({
                        res ->
                        resolve(res) 
                    }).catch({
                        reason ->
                        reject(Throwable(reason))
                    })
            })
            return result
        }

        /**
         * save color scheme 
         */
        fun saveColorScheme(data: ColorScheme): Promise<Any?> {
            val strData = JSON.stringify(colorSchemeToDynamic(data)) 
            val result = Promise<Any?>({
                resolve, reject ->
                val body = FormData()
                body.append("color-scheme", "write")
                body.append("data", strData)

                window.fetch("persistence.php",
                    RequestInit(
                        method = "POST",
                        body = body)).then({
                        res ->
                        res.json()
                    }).then({
                        res ->
                        resolve(res) 
                    }).catch({
                        reason ->
                        reject(Throwable(reason))
                    })
            })
            return result
        }


        /**
         * save point light
         */
        fun savePointLight(
            pointLight: net.ocsoft.mswp.PointLight): Promise<Any?> {

            val strData = JSON.stringify(pointLightToDynamic(pointLight)) 
            val result = Promise<Any?>({
                resolve, reject ->
                val body = FormData()
                body.append("point-light", "write")
                body.append("data", strData)

                window.fetch("persistence.php",
                    RequestInit(
                        method = "POST",
                        body = body)).then(
                    {
                        res ->
                        res.json()
                    }).then(
                    {
                        res ->
                        resolve(res) 
                    }).catch( 
                    {
                        reason ->
                        reject(Throwable(reason))
                    })
            })
            return result
        }
         
        /**
         * convert icon map to dynamic object
         */
        fun iconMapToDynamic(iconMap : Map<String, Icon>): dynamic {
            val result: dynamic = object {}
            val icons: dynamic = object {}
            iconMap.forEach({
                icons[it.key] = it.value 
            })
            result["version"] = "0.1"
            result["icons"] = icons
            return result
        }
        /**
         * convert dynamic object to icon map
         */
        fun dynamicToIconMap(iconData: dynamic): Map<String, Icon>? {
            val verstr = iconData["version"] as String?
            var result : Map<String, Icon>? = null
            if ("0.1" == verstr) {
                val icons = iconData["icons"] 
                if (icons != null) {
                    val strIconMap = HashMap<String, Icon>()
                    val keys = arrayOf<String>(
                        IconSetting.NG_ICON,
                        IconSetting.OK_ICON)

                    keys.forEach {
                        val item = icons[it]
                        
                        val prefix = item["prefix"]
                        val iconName = item["iconName"]
                        if (prefix != null && iconName != null) {
                            strIconMap[it] =
                                Icon(prefix, iconName)    
                        }
                    } 
                    result = strIconMap
                }
            } else {
                val prefix = iconData["prefix"] as String?
                val iconName = iconData["iconName"] as String?
                if (prefix != null && iconName != null) {
                    result = createIconMapFallback(Icon(prefix, iconName))
                }
                
            }
            return result
        }

        /**
         * convert dynamic object to color scheme
         */
        fun dynamicToColorScheme(
            colorScheme: dynamic) : ColorScheme? {
            var result: ColorScheme? = null
            val colorSchemeObj: dynamic = colorScheme["color-scheme"]
            val colorsSource = arrayOf(
                colorSchemeObj["colors"], 
                colorSchemeObj["environment"])
            val defaultColor = arrayOf(
                ColorScheme.colors,
                ColorScheme.envColors)

            var colorsParam: Array<Array<FloatArray>?> = arrayOf(
                null,
                null)
            for (idx0 in 0 until colorsParam.size) {
                if (colorsSource[idx0] != null) {
                    val colors0 = defaultColor[idx0]
                    val colorSource = colorsSource[idx0]
                    var replaced = false
                    if (colorSource != null) {
                        for (idx1 in 0 until colors0.size) {
                            val colorItem = colorSource["${idx1}"]
                            if (colorItem != null) {
                                for(idx2 in 0 until colors0[idx1].size) {
                                    val colorVal = colorItem["${idx2}"]
                                    if (colorVal != null) {
                                        val strColor = colorVal.toString()
                                        var intColor = strColor.toIntOrNull()
                                        if (intColor != null) {
                                            intColor = min(0xff, intColor)
                                            intColor = max(0, intColor)   
                                            colors0[idx1][idx2] = 
                                                intColor.toFloat() 
                                            colors0[idx1][idx2] /= 
                                                0xff.toFloat()
                                            replaced = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (replaced) {
                        colorsParam[idx0] = colors0 
                    } 
                }
            }
            if (colorsParam[0] != null
                || colorsParam[1] != null) {
                for (idx in 0 until colorsParam.size) {
                    if (colorsParam[idx] == null) {
                        colorsParam[idx] = defaultColor[idx]
                    }
                }
                result = ColorScheme(
                    colorsParam[0]!!,
                    colorsParam[1]!!)
            }
            return result
        }

        /**
         * convert color scheme to dynamic object
         */
        fun colorSchemeToDynamic(colorScheme: ColorScheme): dynamic {
            val result: dynamic = object {
            }
            val colorSchemeObj: dynamic = object {
            }
            colorSchemeObj["colors"] = Array<IntArray>(colorScheme.size) {
                val color = colorScheme[it] 
                IntArray(color.size) {
                    max(min(
                        (color[it] * 0xff.toFloat()).roundToInt(), 0xff), 0)
                }
            }
            colorSchemeObj["environment"] = Array<IntArray>(
                colorScheme.envColorSize) {
                val color = colorScheme.getEnvironment(it)!!
                IntArray(color.size) {
                    max(min(
                        (color[it] * 0xff.toFloat()).roundToInt(), 0xff), 0)
                }
            }
            result["version"] = 0
            result["color-scheme"] = colorSchemeObj
            return result
        } 

        /**
         * convert dynamic object to pointLight
         */
        fun dynamicToPointLight(
            pointLightData: dynamic) : net.ocsoft.mswp.PointLight? {
            var result : net.ocsoft.mswp.PointLight? = null
            val pointObj = pointLightData["point"] 
            if (pointObj != null) {
                if (pointObj.length > 2) {
                    val point = FloatArray(3) { 0f }
                    var valid = true 
                    for (idx in 0..2) {
                        val numStr = pointObj[idx]
                        if (numStr != null) {
                            try {
                                point[idx] = numStr.toString().toFloat()
                            } catch (ex: NumberFormatException) {
                                valid = false
                            }
                        }
                        if (!valid) {
                            break
                        }
                    }
                    if (valid) {
                        result = net.ocsoft.mswp.PointLight(point)
                    }
                }
            }
            return result
        }

        /**
         * point light to dynamic object
         */
        fun pointLightToDynamic(
            pointLight: net.ocsoft.mswp.PointLight): dynamic {
            val result: dynamic = object { } 
            result["point"] = pointLight.point 
            return result
        }
 
        /**
         * create icon map in backward compatibility
         */
        fun createIconMapFallback(icon: Icon) : Map<String, Icon> {
            val result = HashMap<String, Icon>()
            result[IconSetting.NG_ICON] = icon

            return result
        }

        /**
         * load icon map from json encoded string
         */
        fun loadIconMap(str: String) : Map<String, Icon>? {
            var result: Map<String, Icon>? = null
            val tmpIconDataMap = JSON.parse<HashMap<String, Any>>(str)

            if (tmpIconDataMap.size > 0) {
                result = HashMap<String, Icon>()

                tmpIconDataMap.forEach {
                    if (it.value is Map<*, *>) {
                        val iconData : Map<String, Any> =
                            it.value as Map<String, Any>
                        if ("prefix" in iconData
                            && "iconName" in iconData) {
                            result[it.key] = Icon(
                                iconData["prefix"] as String,
                                iconData["iconName"] as String)
                        }
                    }
                }
            }
            return result
        }
       
        /**
         * load point light location.
         */
        fun loadPointLight() : Promise<net.ocsoft.mswp.PointLight?> {
            val result = Promise<net.ocsoft.mswp.PointLight?> {
                resolve, reject ->

                val body = FormData()
                body.append("point-light", "read") 
                var prm = window.fetch("persistence.php",
                   RequestInit(
                    method = "POST",
                    body = body)) 
                prm.then({ it.json() }
                ).then({
                    val res: dynamic = it
                    var pointLight : net.ocsoft.mswp.PointLight?
                    pointLight = null
                    if (res.data != null) {
                        val pointLightData = JSON.parse<dynamic>(res.data) 
                        pointLight = dynamicToPointLight(pointLightData)
                    } 
                    resolve(pointLight)
                }).catch({
                    reason ->
                    reject(Throwable(reason))
                })
                
            }
            return result
        }

        /**
         * load color scheme
         */
        fun loadColorScheme(): Promise<ColorScheme?> {
            val result = Promise<ColorScheme?> {
                resolve, reject ->
                val body = FormData()
                body.append("color-scheme", "read")
                var prm = window.fetch("persistence.php", 
                    RequestInit(
                        method = "POST",
                        body = body))
                prm.then({
                    it.json()
                }).then({  
                    val res0: dynamic = it
                    var colorScheme: ColorScheme? = null
                    if (res0.data != null) {
                        val tmpColorScheme = JSON.parse<dynamic>(res0.data)
                        colorScheme = dynamicToColorScheme(tmpColorScheme)
                    }
                    resolve(colorScheme)
                }).catch({
                    reject(Throwable(it))
                })
            } 
            return result
        }
    }

    /**
     * icon data
     */
    data class Icon(
        @JsName("prefix")
        val prefix: String,
        @JsName("iconName")
        val iconName: String)   

}

// vi: se ts=4 sw=4 et: 
