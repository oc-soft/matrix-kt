package net.ocsoft.mswp.ui
import jQuery
import JQueryEventObject

/**
 * editor for point light
 */
class PointLightSetting {

    /**
     * grid
     */
    var grid: Grid? = null


    /**
     * handler to back to main
     */
    var handleToBackToMain: ((JQueryEventObject, Any)-> Any)? = null


    /**
     * visible main menu
     */
    var visibleMainMenu: Boolean
        get() {
            var result = false
            val grid = this.grid
            if (grid != null) {
                val mainMenuItemQuery = grid.mainMenuItemQuery
                val mainMenuItem = jQuery(mainMenuItemQuery!!)
                result = mainMenuItem?.css("visibility") != "hidden"
            }
            return result
        }
        set(value) {
            if (visibleMainMenu != value) {
                val grid = this.grid
                if (grid != null) {
                    val mainMenuItemQuery = grid.mainMenuItemQuery
                    val mainMenuItem = jQuery(mainMenuItemQuery!!)
                    mainMenuItem?.css(
                        "visibility", 
                        if (value) "visible" else "hidden")
                       
                }
            }
        }

    /**
     * visible back to main
     */
    var visibleBackToMain: Boolean
        get() {
            var result = false
            val grid = this.grid
            if (grid != null) {
                val backToMainQuery = grid.backToMainQuery
                val backToMain = jQuery(backToMainQuery!!)
                result = backToMain?.`is`(":visible") 
            }
            return result
        }
        set(value) {
            if (visibleBackToMain != value) {
                val grid = this.grid
                if (grid != null) {
                    val backToMainQuery = grid.backToMainQuery
                    val backToMain = jQuery(backToMainQuery!!)
                    if (value) {
                        backToMain?.show()
                    } else {
                        backToMain?.hide()
                    }
                }
            }
        }

    
    /**
     * you get true if user edits point light location
     */
    var isEditing: Boolean
        get() {
            var result = false
            val grid = this.grid
            if (grid != null) {
                val backToMainQuery = grid.backToMainQuery
                val backToMain = jQuery(backToMainQuery!!)
                result = backToMain!!.data("editor") == "point-light"
            }
            return result
        }

        set(value) {
            if (isEditing != value) {
                val grid = this.grid
                if (grid != null) {
                    val backToMainQuery = grid.backToMainQuery
                    val backToMain = jQuery(backToMainQuery!!)
                    backToMain!!.data("editor", "point-light")
                }
            }
        }


    /**
     * visible editor
     */
    fun show() {
        val grid = this.grid
        if (grid != null) {
            val backToMainQuery = grid.backToMainQuery
            if (backToMainQuery != null) {
                val backToButton = jQuery(backToMainQuery)
                visibleBackToMain = true
                visibleMainMenu = false 
                isEditing = true
                handleToBackToMain = {
                    eventObject, args ->
                    onClickToBackToMain(eventObject, args) 
                }
                backToButton.on("click", handleToBackToMain!!)
            } 
        }
    }


    /**
     * handle click icon back to main
     */
    fun onClickToBackToMain(
        eventObject: JQueryEventObject,
        args: Any): Any {
        jQuery(eventObject.delegateTarget).off("click", handleToBackToMain!!);
        visibleBackToMain = false
        visibleMainMenu = true
        isEditing = false
        return false
    }
   
}

// vi: se ts=4 sw=4 et:
