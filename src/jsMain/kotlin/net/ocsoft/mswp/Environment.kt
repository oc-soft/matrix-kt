package net.ocsoft.mswp

import jQuery

/**
 * manipulate user inteface for  menu, background
 */
class Environment(val option: Option,
    colorScheme: ColorScheme) {

    /**
     * option
     */
    data class Option(
        val mainPlaygroundQuery: String,
        val backgroundQuery: String,
        val menuTextQuery: String,
        val backToMainIconQuery: String)


    /**
     * color scheme
     */
    var colorScheme: ColorScheme = colorScheme
        set(value) {
            if (field != value) {
                field = value
                syncWithColorScheme()
            }
        }


    /**
     * synchronize environment with color scheme
     */
    fun syncWithColorScheme() {
        syncMainPlaygroundWithColorScheme()
        syncBackgroundWithColorScheme()
        syncMenuTextWithColorScheme()
        syncBacktoMainWithColorScheme()
    }

    /**
     * synchronize main play-ground with color scheme
     */
    fun syncMainPlaygroundWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Background)
        
        jQuery(option.mainPlaygroundQuery).css(
            "background-color", ColorScheme.toHtmlRgb(color!!))

    }

    /**
     * synchronize background with color scheme
     */
    fun syncBackgroundWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Background)
        
        jQuery(option.backgroundQuery).css(
            "background-color", ColorScheme.toHtmlRgb(color!!))

    }

    /**
     * synchronize menu text with color scheme
     */
    fun syncMenuTextWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Foreground)
        
        jQuery(option.menuTextQuery).css(
            "color", ColorScheme.toHtmlRgb(color!!))

    }
    /**
     * synchronize icon to back to main with color scheme
     */
    fun syncBacktoMainWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Foreground)
        
        jQuery(option.backToMainIconQuery).css(
            "color", ColorScheme.toHtmlRgb(color!!))

    }
    
}

// vi: se ts=4 sw=4 et:
