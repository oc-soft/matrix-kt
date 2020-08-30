package net.ocsoft.mswp

/**
 * main page displaying protocol
 */
expect class MainPage() {
 
    /**
     * setup body page
     */
    fun setupBody(
        model: Model, 
        camera: Camera, 
        pointLight: PointLight,
        colorScheme: ColorScheme)

    /**
     * setup settings
     */
    fun setup(settings: Settings)
}
// vi: se ts=4 sw=4 et:
