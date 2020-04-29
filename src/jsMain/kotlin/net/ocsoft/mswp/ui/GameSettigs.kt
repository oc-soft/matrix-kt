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

        labelSettingItemHandler = {
            evt, args ->
            onClickOnLabelSettingItem(evt, args)
        }
        // settingItem.on("click", settingItemHandler!!)
        dropdown.bind(option.queries.setting, option.queries.dropdown)
        labelSettingItem?.on("click", labelSettingItemHandler!!)
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
        return false 
    }
}
// vi: se ts=4 sw=4 et: 
