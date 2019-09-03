package net.ocsoft.mswp


/**
 * web font loader configuration.
 */
class WebFontConfig(val activeCallBack: (()->Unit),
    val inactiveCallBack: (()->Unit),
    val textGlyphs : String) {
    @JsName("google")
    var google : dynamic = object {
        val families = arrayOf("M PLUS Rounded 1c")
        val text = textGlyphs
    } 

    @JsName("loading")
    val loading : (()->Unit) = {
        println("loading")
    }
    
    @JsName("active")
    val active : (()->Unit) = {
        println("active")
        activeCallBack()
    }
    @JsName("inactive")
    val inactive : (()->Unit) = {
        println("inactive")
        inactiveCallBack()
    }
    @JsName("fontloading")
    val fontloading : ((String, String)->Unit) = {
        familyName : String, fvd : String ->
        println("loading ${familyName}, ${fvd}")
    }
    @JsName("fontactive")
    val fontactive : ((String, String)->Unit) = {
        familyName : String, fvd : String ->
        println("active ${familyName}, ${fvd}")
    }
    @JsName("fontinactive")
    val fontinactive : ((String, String)->Unit) = {
        familyName : String, fvd : String ->
        println("inactive ${familyName}, ${fvd}")
    }

}
