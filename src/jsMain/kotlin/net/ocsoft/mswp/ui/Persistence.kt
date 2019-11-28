package net.ocsoft.mswp.ui
import jQuery
import JQuery
import JQueryAjaxSettings
import kotlin.js.Promise
import kotlin.collections.Map
import kotlin.collections.HashMap
import kotlin.browser.window
import org.w3c.fetch.RequestInit
import org.w3c.fetch.Response
import org.w3c.xhr.FormData

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
            val result = Promise<Map<String, Icon>?>({
                resolve, reject ->  

                 val body = FormData()
                 body.append("icon", "read")
                 var prm = window.fetch("persistence.php",
                    RequestInit(
                        method = "POST",
                        body = body)) 
                 prm.then(
                    {
                        res ->
                        res.json()
                    }).then(
                    {
                        res ->
                        val res0: dynamic = res 
                        var iconData: Map<String, Icon>? = null
                        if (res0.data != null) { 
                            val tmpIconData = JSON.parse<dynamic>(res0.data)
                            iconData = dynamicToIconMap(tmpIconData)
                       }
                       resolve(iconData) 
                    }).catch( 
                    {
                        reason ->
                        reject(Throwable(reason))
                    })
            }) 
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
         * convert 
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
