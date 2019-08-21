package net.ocsoft.mswp
import net.ocsoft.mswp.ui.GridSettings

data class MainPageConfig(
    val gridSettings : GridSettings = GridSettings("#game_grid", 
            "#font_test",
            "#game_over_modal",
            "#player_won_modal"),
    val splashPaneId : String = "#splash_pane") {
}
