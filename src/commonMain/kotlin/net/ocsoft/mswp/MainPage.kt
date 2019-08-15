package net.ocsoft.mswp

/**
 * main page displaying protocol
 */
expect class MainPage() {
 
    /**
     * setup body page
     */
    fun setupBody(model: Model, camera: Camera, pointLight: PointLight)


    /**
     * setup settings
     */
    fun setup(settings: Settings)
}

