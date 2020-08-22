package net.ocsoft.mswp.ui

import jQuery
import JQueryEventObject
import kotlin.js.Json

/**
 * setting user interface
 */
class AppSettings(val option : Option) {
    /**
     * option
     */
    data class Option(
        val gameSettingsOption: GameSettings.Option, 
        val iconOption : IconSelector.Option,
        val colorOption: ColorSelector.Option,
        val pointLightSettingOption: PointLightSetting.Option)

    /**
     * runtime configuration
     */
    var runtimeConfig : dynamic = null;

    /**
     * icon selector
     */
    var iconSelector : IconSelector? = null


    /**
     * color selector
     */
    var colorSelector: ColorSelector? = null

    /**
     * game setting
     */
    var gameSettings: GameSettings? = null

    /**
     * handle to edit lighting
     */
    var handleToEditLighting: (()->Unit)? = null 

     
    /**
     * bind to html
     */
    fun bind() {
        unbind()
        gameSettings = GameSettings(option.gameSettingsOption)
        gameSettings?.bind(this)
        iconSelector = IconSelector(option.iconOption)
        iconSelector?.runtimeConfig = runtimeConfig
        iconSelector?.bind()

        colorSelector = ColorSelector(option.colorOption)
        
    }

    /**
     * unbind from html
     */
    fun unbind() {
        gameSettings?.unbind()
        gameSettings = null
        iconSelector?.unbind()
        iconSelector = null
        colorSelector = null     
    }


    /**
     * edit point light
     */
    fun editPointLight() {
        val handler = handleToEditLighting       
        if (handler != null) {
            handler()
        }
    }
}
// vi: se ts=4 sw=4 et: 
