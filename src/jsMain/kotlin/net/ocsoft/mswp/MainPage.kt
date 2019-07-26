package net.ocsoft.mswp

import jQuery
import net.ocsoft.mswp.ui.Grid
import net.ocsoft.mswp.ui.ShaderPrograms
import kotlin.js.Promise
import org.w3c.fetch.*
import kotlin.browser.window

/**
 * main page display
 */
actual class MainPage {
        
    var grid : Grid? = null

    actual fun setupBody(model : Model, camera: Camera, 
        pointLight: PointLight) {
        jQuery({ 
            val grid = Grid()
            val shaders = arrayOf(
                "/prg/mswp/net/ocsoft/mswp/ui/vertex.gls", 
                "/prg/mswp/net/ocsoft/mswp/ui/fragment.gls")
            val shaderPromises =  Array<Promise<Response>>(shaders.size) { 
                i -> 
                window.fetch(shaders[i])
            }
             
            Promise.all(shaderPromises).then({
                responses : Array<out Response> ->
                val res = Array<Promise<String>>(responses.size) {
                    i -> responses[i].text() 
                }
                
                Promise.all(res);
            }).then({responses : Array<out String> -> 
                var shaderPrograms = ShaderPrograms(
                    responses[0], 
                    responses[1])
                grid.bind("#game_grid", model, camera, 
                    pointLight, shaderPrograms)
 
            })
        })
    }
}
