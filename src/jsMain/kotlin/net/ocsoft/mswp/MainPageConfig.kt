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
            "#icon_list .icons", 
            "#icon_list .icons li",
            "#icon_item_tmpl",
            "#blank_icon_item_tmpl",
            "#icon_list .pagination.container",
            "#icon_list .ok",
            "#icons_paginating_full_tmpl",
            "#icons_paginating_middle_tmpl",
            "#icons_paginating_simple_tmpl",
            "#icons_paginating_item_tmpl",
            "#synchronizing_icon"))) {
}
