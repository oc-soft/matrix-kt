package net.ocsoft.mswp.ui

import jQuery
import JQueryEventObject
import popper.Popper
import net.ocsoft.ui.Dropdown

/**
 * settings for user interface
 */
class GameSettings(val option: Option) {
    /**
     * handle click event on setting item
     */
    var labelSettingItemHandler : ((JQueryEventObject, Any) -> Any)? = null


    /**
     * handle click event on color setting item
     */
    var colorSettingItemHandler: ((JQueryEventObject, Any) -> Any)? = null

    /**
     * handle click event on editing light origin
     */
    var lightEditItemHandler: ((JQueryEventObject, Any) -> Any)? = null

    /**
     * dropdown handler
     */
    var dropdown = Dropdown()

    /**
     * popper instance
     */
    var popperInstance : popper.Instance? = null

    /**
     * option for game settings
     */
    data class Option(
        val queries: Queries,
        val menu: Menu) {
        /**
         * html dom queries 
         */
        data class Queries(
            val setting: String,
            val dropdown: String,
            val labelSetting: String,
            val colorSetting: String,
            val lightSetting: String)   

        /**
         * menu setting
         */
        data class Menu(
            val offset: Array<Int>) 
    } 
     
    /**
     * application settings
     */
    var appSettings: AppSettings? = null
	
    /**
     * attach this object to html dom.
     */
    fun bind(appSettings: AppSettings) {
        this.appSettings = appSettings
        val labelSettingItem = jQuery(option.queries.labelSetting)
        val lightSettingItem = jQuery(option.queries.lightSetting)
        val colorSettingItem = jQuery(option.queries.colorSetting)
        labelSettingItemHandler = {
            evt, args ->
            onClickOnLabelSettingItem(evt, args)
        }
        lightEditItemHandler = {
            evt, args ->
            onClickOnLightEditItem(evt, args)
        }
        colorSettingItemHandler = {
            evt, args ->
            onClickOnColorSetting(evt, args)
        }
        // settingItem.on("click", settingItemHandler!!)
        dropdown.bind(option.queries.setting, option.queries.dropdown)
        labelSettingItem?.on("click", labelSettingItemHandler!!)
        lightSettingItem?.on("click", lightEditItemHandler!!) 
        colorSettingItem?.on("click", colorSettingItemHandler!!)
        popperInstance = Popper.createPopper(labelSettingItem!![0],
            jQuery(option.queries.dropdown)!![0]!!,
            object {
                val modifiers = arrayOf(
                    object {
                        val name = "offset"
                        val options = object {
                            val offset = option.menu.offset
                        }
                    })
                })
    }

    /**
     * detatch this object from html dom.
     */
    fun unbind() {
        if (labelSettingItemHandler != null) {
            val labelSettingItem = jQuery(option.queries.labelSetting)
            labelSettingItem?.off("click", labelSettingItemHandler!!)
            labelSettingItemHandler = null
        }
        if (colorSettingItemHandler != null) {
            val colorSettingItem = jQuery(option.queries.colorSetting)
            colorSettingItem?.off("click", colorSettingItemHandler!!)
            colorSettingItemHandler = null
        }
        if (lightEditItemHandler != null) {
            val lightSettingItem = jQuery(option.queries.lightSetting)
            lightSettingItem?.off("click", lightEditItemHandler!!)
            lightEditItemHandler = null
        }

        if (popperInstance != null) {
            popperInstance!!.destroy()
            popperInstance = null
        }
        dropdown.unbind()
        this.appSettings = null
    }
    /**
     * handle setting item
     */
    fun onClickOnLabelSettingItem(
        eventObj : JQueryEventObject, args: Any) : Any {
        appSettings?.iconSelector?.show()
        hideDropdown()
        return false 
    }


    /**
     * handle click event for color setting
     */
    fun onClickOnColorSetting(eventObj: JQueryEventObject, args: Any): Any {
        appSettings?.colorSelector?.show()
        hideDropdown()
        return false 
    }

    /**
     * handle setting item for light editing
     */
    fun onClickOnLightEditItem(
        eventObj: JQueryEventObject, args: Any) : Any {
        hideDropdown()
        appSettings?.editPointLight()
        return false
    }

    /**
     * hide dropdown
     */
    fun hideDropdown() {
        dropdown.visibledDropdown = false
    }
}
// vi: se ts=4 sw=4 et: 
