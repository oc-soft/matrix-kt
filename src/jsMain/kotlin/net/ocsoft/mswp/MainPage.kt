package net.ocsoft.mswp

import jQuery
import net.ocsoft.mswp.ui.Grid
/**
 * main page display
 */
actual class MainPage {
		
	var grid : Grid? = null

    actual fun setupBody(model : Model, camera: Camera, 
        pointLight: PointLight) {
		jQuery({ 
			val grid = Grid()
			grid.bind("#game_grid", model, camera, pointLight)
    	})
    }
}
