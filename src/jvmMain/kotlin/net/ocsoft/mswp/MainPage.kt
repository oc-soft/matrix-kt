package net.ocsoft.mswp

import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.annotation.WebServlet

/**
 * main page display
 */
@WebServlet(name = "Home", value = ["/hello"])
actual class MainPage : HttpServlet() {

    override fun doGet(req: HttpServletRequest, res: HttpServletResponse) {
        res.writer.write("This is java server page.")
    }


   actual fun setupBody(model : Model, camera: Camera, 
        pointLight: PointLight) {
   }
}
