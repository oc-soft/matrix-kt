package net.ocsoft.mswp

/**
 * entry point
 */
fun main(args: Array<String>) {
   	val model = Model() 
    val mainPage = MainPage()
	val boardCam = Camera()
    mainPage.setupBody(model, boardCam)
}

	
