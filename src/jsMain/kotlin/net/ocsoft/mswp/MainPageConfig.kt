package net.ocsoft.mswp
import net.ocsoft.mswp.ui.GridSettings
import net.ocsoft.mswp.ui.AppSettings
import net.ocsoft.mswp.ui.IconSelector
import net.ocsoft.mswp.ui.IconSetting
import net.ocsoft.mswp.ui.GameSettings

/**
 * main page configuration
 */
data class MainPageConfig(
    /** game grid settings */
    val gridSettings : GridSettings = GridSettings("#game_grid", 
            "#glyph_workarea",
            "#game_over_modal",
            "#player_won_modal",
            IconSetting()),
    /** splash pane */
    val splashPaneId : String = "#splash_pane",
    /** application settings */
    val appSettings : AppSettings.Option = AppSettings.Option(
        GameSettings.Option(
            GameSettings.Option.Queries(
                ".setting.menu .menu.item",
                ".setting.menu.contents",
                "#label-setting",
                "#lighting-setting"),
            GameSettings.Option.Menu(
                arrayOf(0, 20))),
        IconSelector.Option("#icon_list",
            "#icon_list .icons", 
            "#icon_list .icons li",
            "#icon_list .pagination.container",
            "#icon_list .ok",
            "#icon-kind-selector",
            "svg.svg-inline--fa",
            "#icon_item_tmpl",
            "#blank_icon_item_tmpl",
            "#icons_paginating_full_tmpl",
            "#icons_paginating_middle_tmpl",
            "#icons_paginating_simple_tmpl",
            "#icons_paginating_item_tmpl",
            "#synchronizing_icon",
            gridSettings.iconSetting))) {
}

// vi: se ts=4 sw=4 et: 
