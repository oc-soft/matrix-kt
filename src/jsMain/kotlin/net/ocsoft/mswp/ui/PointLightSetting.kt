package net.ocsoft.mswp.ui
import jQuery
import JQueryEventObject
import kotlin.collections.ArrayList

/**
 * editor for point light
 */
class PointLightSetting(val option: Option) {

    /**
     * option for setting instance
     */
    data class Option(val syncIconTemplateQuery: String)
    /**
     * grid
     */
    var grid: Grid? = null
    

    /**
     * point lighting
     */
    val pointLight: net.ocsoft.mswp.PointLight?
        get() {
            var result: net.ocsoft.mswp.PointLight? = null
            val grid = this.grid
            if (grid != null) { 
                result = grid.pointLight
            }
            return result
        }
 
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
     * back to main contents
     */
    var backToMainContents: MutableList<String>? = null 

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
                result = backToMain.data("editor") == "point-light"
            }
            return result
        }

        set(value) {
            if (isEditing != value) {
                val grid = this.grid
                if (grid != null) {
                    val backToMainQuery = grid.backToMainQuery
                    val backToMain = jQuery(backToMainQuery!!)
                    if (value) {
                        backToMain.data("editor", "point-light")
                    } else {
                        backToMain.removeData("editor")
                    }
                }
            }
        }


    /**
     * visible editor
     */
    fun show(handleClosed: (()->Unit)? = null) {
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
                    if (handleClosed != null) {
                        handleClosed()
                    }
                    onClickToBackToMain(eventObject, args) 
                }
                backToButton.on("click", handleToBackToMain!!)
            } 
        }
    }


    /**
     * handle click icon back to main
     */
    @Suppress("UNUSED_PARAMETER")
    fun onClickToBackToMain(
        eventObject: JQueryEventObject,
        args: Any): Any {
        jQuery(eventObject.delegateTarget).off("click", handleToBackToMain!!);
        doSave() 
        return false
    }

    /**
     * save point light setting
     */
    fun doSave() {
        val pointLight = this.pointLight           
        if (pointLight != null) {
            changeBackToMainToSync()
            Persistence.savePointLight(pointLight).then({
                restoreToBackToMain()
                visibleBackToMain = false
                visibleMainMenu = true
                isEditing = false
            }, {
                restoreToBackToMain()
                visibleBackToMain = false
                visibleMainMenu = true
                isEditing = false
            })
        }
    }

    /**
     * change back to main icon to synchronizing icon
     */
    fun changeBackToMainToSync() {
        val syncHtml = jQuery(option.syncIconTemplateQuery).html()
        val grid = this.grid
        if (grid != null) {
            val backToMainQuery = grid.backToMainQuery
            backToMainContents = ArrayList<String>()
            jQuery(backToMainQuery!!).html({
                _, oldHtml ->
                backToMainContents?.add(oldHtml)
                syncHtml 
            })
        }
    }

    /**
     * restore back to main icon from sychronzing icon
     */
    fun restoreToBackToMain() {
        val backToMainContents = this.backToMainContents
        if (backToMainContents != null) {
            val grid = this.grid
            if (grid != null) {
                val backToMainQuery = grid.backToMainQuery
                jQuery(backToMainQuery!!).html({
                    idx, _ ->
                    backToMainContents[idx.toInt()] 
                })
            }
            this.backToMainContents = null
        }
    }

}

// vi: se ts=4 sw=4 et:
