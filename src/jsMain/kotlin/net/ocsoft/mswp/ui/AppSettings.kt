package net.ocsoft.mswp.ui

import jQuery
import JQueryEventObject

/**
 * setting user interface
 */
class AppSettings(val option : Option) {
    /**
     * option
     */
    data class Option(val settingItemNode : String,
        val iconOption : IconSelector.Option)

    /**
     * handle click event on setting item
     */
    var settingItemHandler : ((JQueryEventObject, Any) -> Any)? = null

    /**
     * icon selector
     */
    var iconSelector : IconSelector? = null
     
    /**
     * bind to html
     */
    fun bind() {
        unbind()
        val settingItem = jQuery(option.settingItemNode)
        settingItemHandler = { evt, args -> onClickOnSettingItem(evt, args) }
        settingItem.on("click", settingItemHandler!!)
        iconSelector = IconSelector(option.iconOption)
        iconSelector?.bind()
    }

    /**
     * unbind from html
     */
    fun unbind() {
        val settingItem = jQuery(option.settingItemNode)
        settingItem.off("click", settingItemHandler!!)
        if (iconSelector != null) {
            iconSelector!!.unbind()
            iconSelector = null 
        }
    }

    /**
     * handle setting item
     */
    fun onClickOnSettingItem(eventObj : JQueryEventObject, args: Any) : Any {
        iconSelector?.show()
        return false 
    }

    

    
}
