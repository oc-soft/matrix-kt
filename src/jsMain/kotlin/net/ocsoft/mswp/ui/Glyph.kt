package net.ocsoft.mswp.ui

import kotlin.browser.document
import kotlin.collections.Map
import kotlin.collections.HashMap
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.*

import jQuery
import kotlin.math.*

class Glyph {

    /**
     * button texture pixel size
     */
    var buttonTextureSize : Int = 0x80
    
    /**
     * text size scale in button
     */
    var buttonTextRatio : Float = .8f

    /**
     * node id to draw glyph
     */
    var nodeId : String? = null
    
    /**
     * number image data map
     */
    var numberImageMap: Map<Int, ImageData>? = null
    /**
     * number image and blank data map
     */
    var numberImageBlankMap : Map<Int, ImageData>? = null

    /**
     * connect nodeid into this class
     */
    fun bind(nodeId: String) {
        this.nodeId = nodeId     
        val canvas = jQuery(nodeId)[0] as HTMLCanvasElement
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        setup(ctx)
    }
    

    fun setup(ctx : CanvasRenderingContext2D) {
        setupNumbers(ctx)
    }
    /**
     * setup numbers
     */
    fun setupNumbers(ctx: CanvasRenderingContext2D) {
        setupNumbers0(ctx)
        setupNumbers1()
    }
    /**
     * setup numbers
     */
    fun setupNumbers0(ctx : CanvasRenderingContext2D) {
        val savedFont = ctx.font 
        val savedBaseline: CanvasTextBaseline = ctx.textBaseline
        val fontSize = round(this.buttonTextureSize + buttonTextRatio).toInt()
        val canvas = ctx.canvas
        val width = canvas.width
        val height = canvas.height
        ctx.textBaseline = CanvasTextBaseline.MIDDLE
        ctx.font = "${fontSize}px sans-serif"
        val numberImageMap = HashMap<Int, ImageData>()


        for (number in 0..9) {
            val numStr = number.toString()
            val texMtx = ctx.measureText(numStr)
            var xcoord = .0
            xcoord += (fontSize - texMtx.width) / 2.0
            val ycoord = buttonTextureSize * .5
            ctx.fillText(numStr, xcoord, ycoord) 
            val img = ctx.getImageData(0.0, 0.0,
               buttonTextureSize.toDouble(),
               buttonTextureSize.toDouble())
            numberImageMap[number] = img
            ctx.clearRect(0.0, 0.0, 
                width.toDouble(),
                height.toDouble())
        }
        this.numberImageMap = numberImageMap
        ctx.textBaseline = savedBaseline
        ctx.font = savedFont
    }
    /**
     * setup number image-blank map
     */
    fun setupNumbers1() {
       val numberImageMap = this.numberImageMap
        if (numberImageMap != null) {
            val numberImageBlankMap = numberImageMap.mapValues({ 
                val arr = Uint8ClampedArray(
                    it.value.width * it.value.height * 2 * 4) 
                arr.set(it.value.data, 0) 
                ImageData(arr, it.value.width)
            })
            this.numberImageBlankMap = numberImageBlankMap
        } 
        
    }

    /**
     * get an image related number.
     */
    fun getNumberImage(number : Int): ImageData? {
        val numberImageMap = this.numberImageMap
        var result : ImageData? = null
        if (numberImageMap != null) {
            result = numberImageMap[number] 
        }
        return result
    }
    /**
     * get an image-blank related number.
     */
    fun getNumberImageBlank(number : Int): ImageData? {
        val numberImageMap = this.numberImageBlankMap
        var result : ImageData? = null
        if (numberImageMap != null) {
            result = numberImageMap[number] 
        }
        return result
    }


    /**
     * simple test
     */ 
    fun draw3() {
        val img = getNumberImage(3)!!
        val canvas = jQuery(nodeId!!)[0] as HTMLCanvasElement
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        ctx.putImageData(img, 10.0, 10.0)         
    }
}
