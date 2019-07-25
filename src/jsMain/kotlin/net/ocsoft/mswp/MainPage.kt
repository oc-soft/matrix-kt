package net.ocsoft.mswp

import jQuery
import net.ocsoft.mswp.ui.Grid
import net.ocsoft.mswp.ui.ShaderPrograms

/**
 * main page display
 */
actual class MainPage {
		
	var grid : Grid? = null

    actual fun setupBody(model : Model, camera: Camera, 
        pointLight: PointLight) {
		jQuery({ 
			val grid = Grid()
            var shaderPrograms = ShaderPrograms("", "")
			grid.bind("#game_grid", model, camera, pointLight, shaderPrograms)
    	})
    }
}
