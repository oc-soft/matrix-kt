package net.ocsoft.mswp


/**
 * run the program
 */
expect fun run(): Unit


/**
 * entry point
 */
fun main(args: Array<String>) {
    val model = Model() 
    val mainPage = MainPage()
    val boardCam = Camera()
    val pointLight = PointLight()
    mainPage.setupBody(model, boardCam, pointLight)
    mainPage.setup(Settings())
    Context.mainPage = mainPage 
    run()
}
// vi: se ts=4 sw=4 et:
    
