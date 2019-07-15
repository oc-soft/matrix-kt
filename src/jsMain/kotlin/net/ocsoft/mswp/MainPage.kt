package net.ocsoft.mswp

import jQuery
import net.ocsoft.mswp.ui.Grid
/**
 * main page display
 */
actual class MainPage {
		
	var grid : Grid? = null
		set(value) {
		}

    actual fun setupBody(model : Model, camera: Camera) {
		jQuery({ 
			val grid = Grid()
			grid.bind("#game_grid", model, camera)
    	})
    }
}
