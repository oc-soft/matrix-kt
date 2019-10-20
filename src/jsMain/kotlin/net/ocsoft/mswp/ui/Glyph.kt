package net.ocsoft.mswp.ui

import kotlin.browser.document
import kotlin.browser.window
import kotlin.collections.Map
import kotlin.collections.HashMap
import org.khronos.webgl.Uint8ClampedArray
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image
import org.w3c.dom.Path2D
import org.w3c.dom.*
import org.w3c.dom.svg.*

import jQuery
import kotlin.math.*
import net.ocsoft.mswp.ColorScheme
import fontawesome.SvgCore;

class Glyph(
    val numberColor: FloatArray = ColorScheme.colors[0].copyOf(),
    val mineColor: FloatArray = numberColor) {

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
     * mine image
     */
    var mineImage : ImageData? = null

    /**
     * mine blank image
     */
    var mineImageBlank : ImageData? = null
    /**
     * connect nodeid into this class
     */
    fun bind(nodeId: String, 
        iconSetting : IconSetting) {
        this.nodeId = nodeId     
        val canvas = jQuery(nodeId)[0] as HTMLCanvasElement
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        setup(ctx, iconSetting)
    }
    

    fun setup(ctx : CanvasRenderingContext2D, 
        iconSetting: IconSetting) {
        setupNumbers(ctx)
        setupMineImages(ctx, iconSetting)
    }
    /**
     * setup numbers
     */
    fun setupNumbers(ctx: CanvasRenderingContext2D) {
        setupNumbers0(ctx)
        setupNumbers1()
    }
    /**
     * setup mine image
     */
    fun setupMineImages(ctx: CanvasRenderingContext2D,
        iconSetting: IconSetting) {
        setupMineImage(ctx, iconSetting)
        setupMineImage1()
        // drawScal()
    }

    /**
     * setup numbers
     */
    fun setupNumbers0(ctx : CanvasRenderingContext2D) {
        val savedFont = ctx.font 
        val savedBaseline: CanvasTextBaseline = ctx.textBaseline
        val fontSize = round(this.buttonTextureSize * buttonTextRatio).toInt()
        val canvas = ctx.canvas
        val width = canvas.width
        val height = canvas.height
        fun FloatArray.toRgba():String { 
            val fltArray = this
            val rgb = IntArray(3) {
                max(min(round(fltArray[it] * 255), 255f), 0f).toInt()
            }
            return "rgba(${rgb[0]}, ${rgb[1]}, ${rgb[2]}, ${fltArray[3]})" 
        }
        var colorStr = numberColor.toRgba()
        ctx.textBaseline = CanvasTextBaseline.MIDDLE
        ctx.font = "${fontSize}px 'M PLUS Rounded 1c'"
        ctx.fillStyle = colorStr 
        val numberImageMap = HashMap<Int, ImageData>()


        for (number in 0..9) {
            val numStr = number.toString()
            val texMtx = ctx.measureText(numStr)
            var xcoord = .0
            
            xcoord += (this.buttonTextureSize - texMtx.width) / 2.0
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
     * setup mine image
     */
    fun setupMineImage(ctx : CanvasRenderingContext2D,
        mineImageSrc: Image) {
        val imgSize = round(this.buttonTextureSize * buttonTextRatio).toInt()
        val canvas = ctx.canvas
        val width = canvas.width
        val height = canvas.height
        fun FloatArray.toRgba():String { 
            val fltArray = this
            val rgb = IntArray(3) {
                max(min(round(fltArray[it] * 255), 255f), 0f).toInt()
            }
            return "rgba(${rgb[0]}, ${rgb[1]}, ${rgb[2]}, ${fltArray[3]})" 
        }
        val imageWidth = mineImageSrc.width.toFloat()
        val imageHeight  = mineImageSrc.height.toFloat()
        val imgScale = imgSize / imageHeight   
        val targetSize = floatArrayOf(imageWidth * imgScale,
            imageHeight * imgScale)
        val loc = floatArrayOf((buttonTextureSize - targetSize[0]) / 2,
            (buttonTextureSize - targetSize[1]) / 2)
  
        
        var colorStr = mineColor.toRgba()
        // mineImageSrc.style.color = colorStr
        // mineImageSrc.style.backgroundColor = colorStr
        // mineImageSrc.style.fill = colorStr

        ctx.drawImage(mineImageSrc, 
            loc[0].toDouble(), 
            loc[1].toDouble(), 
            targetSize[0].toDouble(),
            targetSize[1].toDouble()) 

        val mineImage = ctx.getImageData(0.0, 0.0,
            buttonTextureSize.toDouble(),
            buttonTextureSize.toDouble())
        ctx.clearRect(0.0, 0.0, 
            width.toDouble(),
            height.toDouble())
        this.mineImage = mineImage 
        // mineImageSrc.style.color = savedColor
    }
    /**
     * setup mine image-blank map
     */
    fun setupMineImage1() {
        val mineImage = this.mineImage
        if (mineImage != null) {
            val arr = Uint8ClampedArray(
                mineImage.width * mineImage.height * 2 * 4) 
            arr.set(mineImage.data, 0) 
                            
            this.mineImageBlank = ImageData(arr, mineImage.width)
        } 
    }
    /**
     * create image with font awesome icon
     */
    fun createFontawesomeIconDef(prefix: String,
        iconName : String) : fontawesome.IconDefinition {
        val imgSize = round(this.buttonTextureSize * buttonTextRatio).toInt()
        var fa = window.get("FontAwesome")
        val result : fontawesome.IconDefinition = fa.findIconDefinition(
            object {
                val prefix : String = prefix
                val iconName : String = iconName 
            })
        return result 
    }   

    /**
     * create scale matrix
     */
    fun createScaleMatrix(scale: Double): DOMMatrix  {
        val svgElem = document.createElementNS("http://www.w3.org/2000/svg", 
            "svg") as SVGSVGElement
        val result = svgElem.createSVGMatrix()
        // val result = DOMMatrix()
        result.a = scale
        result.b = 0.0
        result.c = 0.0
        result.d = scale
        result.e = 0.0
        result.f = 0.0
        return result
    }

    /**
     * multiply matricies
     */

    fun multiply(a: DOMMatrix, b: DOMMatrix): DOMMatrix {
        var res: DOMMatrix? = null
        if (js("typeof a.multiplySelf === 'function'")) {
            res = a.multiplySelf(b)
        } else {
            res = a.multiply(b)
        }
        return res!!
    }

    /**
     * create translate matrix
     */
    fun createTranslateMatrix(xDisp: Double, yDisp: Double): DOMMatrix {
        val svgElem = document.createElementNS("http://www.w3.org/2000/svg", 
            "svg") as SVGSVGElement
        val result = svgElem.createSVGMatrix()
        //  val result = DOMMatrix()
        result.e = xDisp 
        result.f = yDisp

        return result
    }

    /**
     * create image with icon setting
     */
    fun setupMineImage(ctx: CanvasRenderingContext2D,
        iconSetting : IconSetting) {
    
        setupMineImage(ctx, iconSetting.mineIcon) 
    }


    /**
     * create path from string
     */
    fun createPath(pathStr: String): Path2D {

        val ua = window.navigator.userAgent; 
        var result: Path2D? = null 
        if (ua.indexOf("Edge") == -1) {
            result = ms.Svg.createPath2D(pathStr)
            // result = Path2D(pathStr)
        } else {
            result = ms.Svg.createPath2D(pathStr)
        }

        return result!!
    }

    /**
     * create image with font awesome icon
     */
    fun setupMineImage(ctx: CanvasRenderingContext2D,
        mineIconRef : Persistence.Icon) {
        setupMineImage(ctx, createFontawesomeIconDef(
            mineIconRef.prefix, mineIconRef.iconName))
    }
        
    /**
     * create image with font awesome icon
     */
    fun setupMineImage(ctx: CanvasRenderingContext2D,
        iconDef: fontawesome.IconDefinition) {
        val imgSize = round(this.buttonTextureSize * buttonTextRatio).toInt()
        val iconWidth = iconDef.icon[0] as Int
        val iconHeight = iconDef.icon[1] as Int
        val scale = imgSize.toDouble() / max(iconWidth, iconHeight)
        val sizeDisplaying = arrayOf(scale * iconWidth, scale * iconHeight) 
        val displacement = (this.buttonTextureSize * (1 - buttonTextRatio)) / 2 
        val ms = createScaleMatrix(scale)
        val mt = createTranslateMatrix(displacement.toDouble(), 
            displacement.toDouble())
        val m = multiply(mt, ms)
        val pathSrc = createPath(iconDef.icon[4] as String)
        val path = pathSrc
        // val path = Path2D()
        // path.addPath(pathSrc, m)
        val canvas = ctx.canvas
        val width = canvas.width
        val height = canvas.height
        // val savedTrans = ctx.getTransform()
        ctx.save()
        // ctx.setTransform(m)
        ctx.setTransform(m.a, m.b, m.c, m.d, m.e, m.f)
        ctx.fill(path)
        ctx.restore()
        // ctx.setTransform(savedTrans)
        val img = ctx.getImageData(0.0, 0.0,
            buttonTextureSize.toDouble(),
            buttonTextureSize.toDouble())
        ctx.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
 
        this.mineImage = img
    }   

     
    /**
     * create image with font awesome icon
     */
    fun setupMineImage1(ctx: CanvasRenderingContext2D,
        iconDef : fontawesome.IconDefinition) {
        val imgSize = round(this.buttonTextureSize * buttonTextRatio).toInt()
        var fa = window.get("FontAwesome")
        var icon : fontawesome.Icon = fa.icon(iconDef)
        if (icon != null) {
            val abstract = icon.abstract
            if (abstract != null) {
                val svgElem = abstract.find( { it.tag == "svg" } )
                if (svgElem != null) {
                    val children = svgElem.children
                    val pathElem = children?.find( { it.tag == "path" })
                    if (pathElem != null) {
                        val canvas = ctx.canvas
                        val width = canvas.width
                        val height = canvas.height
 
                        val attr : fontawesome.Attributes
                            = pathElem.attributes as fontawesome.Attributes 
                        val dStr = attr["d"] as String
                        val pathSrc = Path2D(dStr)
                        val path = Path2D()
                        ctx.fill(path)
                        val img = ctx.getImageData(0.0, 0.0,
                            buttonTextureSize.toDouble(),
                            buttonTextureSize.toDouble())
                        ctx.clearRect(0.0, 0.0, 
                            width.toDouble(),
                            height.toDouble())
 
                        this.mineImage = img
                    }
                }
            }
        }
    }   

    /**
     * setup mine image
     */
    fun setupMineImageOld(ctx : CanvasRenderingContext2D,
        mineImageSrc: Image) {
        val savedFont = ctx.font 
        val savedBaseline: CanvasTextBaseline = ctx.textBaseline
        val fontSize = round(this.buttonTextureSize + buttonTextRatio).toInt()
        val canvas = ctx.canvas
        val width = canvas.width
        val height = canvas.height
        fun FloatArray.toRgba():String { 
            val fltArray = this
            val rgb = IntArray(3) {
                max(min(round(fltArray[it] * 255), 255f), 0f).toInt()
            }
            return "rgba(${rgb[0]}, ${rgb[1]}, ${rgb[2]}, ${fltArray[3]})" 
        }
        var colorStr = mineColor.toRgba()
        ctx.textBaseline = CanvasTextBaseline.MIDDLE
        ctx.font = "900 ${fontSize}px 'Font Awesome 5 Pro'"
        ctx.fillStyle = colorStr 
        // scal icon
        // val mineStr = "\uf02b"
        // val mineStr = String.fromCharCode(0xf26b)
        val mineStr = "\uf54c"
        val texMtx = ctx.measureText(mineStr)
        var xcoord = .0
        xcoord += (fontSize - texMtx.width) / 2.0
        val ycoord = buttonTextureSize * .5
        ctx.fillText(mineStr, xcoord, ycoord) 
        val mineImage = ctx.getImageData(0.0, 0.0,
            buttonTextureSize.toDouble(),
            buttonTextureSize.toDouble())
        ctx.clearRect(0.0, 0.0, 
            width.toDouble(),
            height.toDouble())
        this.mineImage = mineImage 
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
     * create mine image node
     */
    fun createMineImageNode() {
//        val svgNode = document.createElementNS(
//            "http://www.w3.org/2000/svg",
//            "svg") as SVGElement
//        svgNode.setAttributeNS("http://www.w3.org/1999/xlink", "href", "")
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
    fun drawScal() {
        val img = mineImage
        val canvas = jQuery(nodeId!!)[0] as HTMLCanvasElement
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        ctx.putImageData(img!!, 10.0, 10.0)         
    }
}
