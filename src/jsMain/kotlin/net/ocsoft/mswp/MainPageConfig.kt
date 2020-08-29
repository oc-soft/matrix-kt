package net.ocsoft.mswp
import net.ocsoft.mswp.ui.GridSettings
import net.ocsoft.mswp.ui.AppSettings
import net.ocsoft.mswp.ui.IconSelector
import net.ocsoft.mswp.ui.ColorSelector
import net.ocsoft.mswp.ui.PointLightSetting
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
            "#back-to-main",
            ".setting.menu .menu.item",
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
                "#color-setting",
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
            "#synchronizing-icon",
            gridSettings.iconSetting),
        ColorSelector.Option(
            "#color-selector",
            "#color-selector .oc-color-circle",
            "#color-seletor .ok",
            ".colors.container",
            ".color-item-0",
            ".color-item-1",
            ".color-item-3",
            ".color-item-4",
            ".color-item-env-0",
            ".color-item-env-1",
            "color-scheme",
            ".description"),
        PointLightSetting.Option("#synchronizing-icon-2"))) {
}

// vi: se ts=4 sw=4 et: 
