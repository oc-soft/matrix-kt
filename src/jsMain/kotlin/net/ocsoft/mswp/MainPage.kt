package net.ocsoft.mswp

import jQuery
import net.ocsoft.mswp.ui.Grid
import net.ocsoft.mswp.ui.ShaderPrograms
import kotlin.js.Promise
import kotlin.js.Json
import org.w3c.fetch.*
import org.w3c.dom.MutationObserver
import org.w3c.dom.MutationObserverInit
import org.w3c.dom.MutationRecord
import org.w3c.dom.Node
import org.w3c.dom.Element
import org.w3c.dom.Image
import kotlin.browser.window
import kotlin.collections.Set
import kotlin.collections.HashSet
import net.ocsoft.mswp.ui.GridSettings
import net.ocsoft.mswp.ui.AppSettings
import net.ocsoft.mswp.ui.Persistence

/**
 * main page display
 */
actual class MainPage {
        
    companion object {
    }
    /**
     * configuration
     */
    val config: MainPageConfig = MainPageConfig()

    /**
     * game grid
     */
    var grid : Grid? = null
    /**
     * model
     */
    var model: Model? = null

    /**
     * camera
     */
    var camera: Camera? = null

    /**
     * light
     */
    var pointLight: PointLight? = null
   
    /**
     * start gaming
     */
    var runPlayground : (()->Unit)? = null


    /**
     * setting
     */
    var appSettings: AppSettings = AppSettings(config.appSettings)

    /**
     * setup body
     */ 
    actual fun setupBody(model : Model, camera: Camera, 
        pointLight: PointLight) {
        this.model = model
        this.camera = camera
        this.pointLight = pointLight        
    }
    /**
     * setup for html page
     */
    actual fun setup(settings: Settings) {
    }     
    /**
     * run program
     */ 
    @JsName("run")
    fun run(settings: Any) {
        var settingObj : dynamic? = null 
        if (settings != null) {
            settingObj = settings
        } else {
            settingObj = Settings()
        }
        val promise = loadFont(settingObj.textureText)
        promise.then({ 
            setupBodyI(model!!, 
            camera!!, 
            pointLight!!,
            settingObj!!.rootDir,
            settingObj!!.ui) 
        })
    }
    fun setupBodyI(model : Model, 
        camera: Camera, 
        pointLight: PointLight,
        rootDir: String,
        uiSetting: Json) {
        appSettings.runtimeConfig = uiSetting
        jQuery({ 
            val grid = Grid()
            val shaders = arrayOf(
                "${rootDir}/prg/mswp/net/ocsoft/mswp/ui/vertex.gls", 
                "${rootDir}/prg/mswp/net/ocsoft/mswp/ui/fragment.gls")

            val shaderPromises = Array<Promise<Response>>(shaders.size) {
                window.fetch(shaders[it])
            }
            val iconPromises = arrayOf(
                Persistence.loadIcon().then({ 
                    if (it != null) {
                        config.gridSettings.iconSetting.mineIcon = it!!
                    }
                    Unit
                })) 
                    
            var promises = Array<Promise<Any>>(shaders.size
                + iconPromises.size) {
                i -> 
                var promiseRes : Promise<Any>? = null 
                if (i < shaders.size) {
                    promiseRes = shaderPromises[i].then({ res -> res.text() })
                } else {
                    promiseRes = iconPromises[i - shaders.size]
                }
                promiseRes!!
            }
             
            Promise.all(promises).then({
                responses : Array<out Any> -> 
                var shaderPrograms = ShaderPrograms(
                    responses[0] as String, 
                    responses[1] as String)
                grid.bind(config.gridSettings,
                    model, camera, 
                    pointLight, shaderPrograms)
                appSettings.bind()
                readyToPlay() 
 
            })
        })
    }

    /**
     * the program is ready to play now
     */
    fun readyToPlay() {
        jQuery(".loading", config.splashPaneId).hide()   
        jQuery(config.splashPaneId).height(0)
    }
    /**
     * load font
     */
    fun loadFont(textToLoad : String) : Promise<Unit> {
        val result = Promise<Unit>({
            resolve, reject -> Unit  
            val activeCallback = {
                resolve(Unit) 
            }
            val inactiveCallback = {
                reject(Error("failed"))
            }
            val config : dynamic = WebFontConfig(activeCallback, 
                inactiveCallback,
                textToLoad)
            WebFont.load(config)
        })
        return result
    }
}
