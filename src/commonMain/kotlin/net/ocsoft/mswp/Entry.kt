package net.ocsoft.mswp

/**
 * entry point
 */
fun main(args: Array<String>) {
   	val model = Model() 
    val mainPage = MainPage()
	val boardCam = Camera()
    val pointLight = PointLight()
    mainPage.setupBody(model, boardCam, pointLight)
}

	
