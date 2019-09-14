package net.ocsoft.mswp
import net.ocsoft.mswp.ui.GridSettings
import net.ocsoft.mswp.ui.AppSettings
import net.ocsoft.mswp.ui.IconSelector
/**
 * main page configuration
 */
data class MainPageConfig(
    /** game grid settings */
    val gridSettings : GridSettings = GridSettings("#game_grid", 
            "#font_test",
            "#game_over_modal",
            "#player_won_modal"),
    /** splash pane */
    val splashPaneId : String = "#splash_pane",
    /** application settings */
    val appSettings : AppSettings.Option = AppSettings.Option(
        ".setting.menu",
        IconSelector.Option("#icon_list",
            "#icon_list .icon.grid", 
            "#icon_item_tmpl"))) {
}
