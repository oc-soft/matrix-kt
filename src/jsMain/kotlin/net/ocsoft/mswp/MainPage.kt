package net.ocsoft.mswp

import jQuery
import net.ocsoft.mswp.ui.Grid
import net.ocsoft.mswp.ui.ShaderPrograms
import kotlin.js.Promise
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

/**
 * main page display
 */
actual class MainPage {
        
    companion object {
        val LoadCompletionClasses : Set<String> = HashSet<String>(arrayOf(
            "wf-active"
        ).toList())
    }
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
     * setup body
     */ 
    actual fun setupBody(model : Model, camera: Camera, 
        pointLight: PointLight) {
        
        attachMutationToHtml({ setupBodyI(model, camera, pointLight) })
    }
    /**
     * setup for html page
     */
    actual fun setup(settings: Settings) {
    }     
   
    fun setupBodyI(model : Model, camera: Camera, 
        pointLight: PointLight) {
        // WebFont.load(getWebfontConfig())
        jQuery({ 
            val grid = Grid()
            val shaders = arrayOf(
                "/prg/mswp/net/ocsoft/mswp/ui/vertex.gls", 
                "/prg/mswp/net/ocsoft/mswp/ui/fragment.gls")
            val imgs = arrayOf(
                "/img/skull-solid.svg")

            val shaderPromises = Array<Promise<Response>>(shaders.size) {
                window.fetch(shaders[it])
            }
            val imagePromises = Array<Promise<Image>>(imgs.size) {
                idx ->
                Promise({ resolve, reject ->
                    val img = Image()
                    img.onload = { 
                        evt -> 
                        resolve(img) 
                    }
                    img.onerror = {
                        e0, e1, e2, e3, e4 ->
                        reject(e0)
                    }
                    img.src = imgs[idx]
                })
            }
            var promises = Array<Promise<Any>>(shaders.size
                + imagePromises.size) {
                i -> 
                var promiseRes : Promise<Any>? = null 
                if (i < shaders.size) {
                    promiseRes = shaderPromises[i].then({ res -> res.text() })
                } else {
                    promiseRes = imagePromises[i - shaders.size]
                }
                promiseRes!!
            }
             
            Promise.all(promises).then({
                responses : Array<out Any> -> 
                var shaderPrograms = ShaderPrograms(
                    responses[0] as String, 
                    responses[1] as String)
                grid.bind("#game_grid", "#font_test",
                    model, camera, 
                    pointLight, shaderPrograms,
                    responses[2] as Image)
 
            })
        })
    }

    /**
     * attach mutation observer int html 
     */
    fun attachMutationToHtml(loadFinished:(()->Unit)) {
        loadFinished()
    }
     /**
     * attach mutation observer int html 
     */
    fun attachMutationToHtmlForFutureReleaase(loadFinished:(()->Unit)) {
        val topNodes = window.document.getElementsByTagName(
            "html")
        val aNode : Element = topNodes.item(0) as Element
        val classList = aNode.classList
                          
        var doLoad = true 
        for (idx in 0..classList.length - 1) {
            val className = classList.item(idx)
            if (className in LoadCompletionClasses) {
                doLoad = false 
            }
        }
        if (doLoad) { 
            attachMutationToHtmlI(loadFinished)
        } else {
            loadFinished()
        }
    }
    /**
     * attach mutation observer int html 
     */
    fun attachMutationToHtmlI(loadFinished:(()->Unit)) {
        val observer = MutationObserver({
            mtRecs, mtObj->
            mtRecs.forEach({
                if (it.type == "attributes") {
                    if (it.attributeName == "class") {
                        val topNodes = window.document.getElementsByTagName(
                            "html")
                        val aNode : Element = topNodes.item(0) as Element
                        val classList = aNode.classList
                          
                        for (idx in 0..classList.length - 1) {
                            val className = classList.item(idx)
                            if (className in LoadCompletionClasses) {
                                loadFinished()
                                mtObj.disconnect()
                            }
                        }
                    }
                }
            })
        })
        val topNodes = window.document.getElementsByTagName("html")
        val topNode : Node = topNodes.item(0) as Node 
        observer.observe(topNode, 
            object: MutationObserverInit {
                override var attributes: Boolean? = true 
        })
        
    }
}
