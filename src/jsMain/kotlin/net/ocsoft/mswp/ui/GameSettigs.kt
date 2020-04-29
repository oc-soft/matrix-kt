package net.ocsoft.mswp.ui

import jQuery
import JQueryEventObject
import popper.Popper
import net.ocsoft.ui.Dropdown

/**
 * settings for user interface
 */
class GameSettings(val queries: Queries) {
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
     * html dom queries 
     */
    data class Queries(
        val setting: String,
        val dropdown: String,
        val labelSetting: String,
        val lightSetting: String)   
     
    /**
     * application settings
     */
    var appSettings: AppSettings? = null
	
    /**
     * attach this object to html dom.
     */
    fun bind(appSettings: AppSettings) {
        this.appSettings = appSettings
        val labelSettingItem = jQuery(queries.labelSetting)

        labelSettingItemHandler = {
            evt, args ->
            onClickOnLabelSettingItem(evt, args)
        }
        // settingItem.on("click", settingItemHandler!!)
        dropdown.bind(queries.setting, queries.dropdown)
        labelSettingItem?.on("click", labelSettingItemHandler!!)
        popperInstance = Popper.createPopper(labelSettingItem!![0],
            jQuery(queries.dropdown)!![0]!!,
            object {
            })
    }

    /**
     * detatch this object from html dom.
     */
    fun unbind() {
        if (labelSettingItemHandler != null) {
            val labelSettingItem = jQuery(queries.labelSetting)
            labelSettingItem?.off("click", labelSettingItemHandler!!)
            labelSettingItemHandler = null
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
