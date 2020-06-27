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

/**
 * glyph setting
 */
class Glyph(
    val numberColor: FloatArray = ColorScheme.colors[0].copyOf(),
    val mineColor: FloatArray = numberColor) {

    /**
     * this class is used for generating texture image
     */
    data class ImageParameter(
        val textureSize: Int,
        val contentsRatio: Float) 

    /**
     * button texture pixel size
     */
    var buttonTextureSize : Int = 0x80
    
    /**
     * text size scale in button
     */
    var buttonTextRatio : Float = .8f

    /**
     * light marker texture pixel size
     */
    var lightMarkerTextureSize: Int = 0x40
    
    /**
     * light marker texture image in point sprite
     */
    var lightMarkerImageRatio: Float = .8f 


    /**
     * default texture size
     */
    val defaultTextureSize: Int
        get() {
            return this.buttonTextureSize
        }
    /**
     * defaultTextureRatio
     */
    val defaultTextureRatio: Float
        get() {
            return buttonTextRatio
        }
    /**
     * special image parameter map
     */
    val specialImageParameterMap : Map<String, ImageParameter> 
        get() {
            val result = mapOf(
                IconSetting.LIGHT_MARKER to
                    ImageParameter(
                        lightMarkerTextureSize,
                        lightMarkerImageRatio))
            return result
        }
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
     * special image map
     */
    var specialImageMap : MutableMap<String, ImageData>? = null
    
    /**
     * special image blank map
     */
    var specialImageBlankMap : MutableMap<String, ImageData>? = null

    /**
     * ok image
     */
    val okImage : ImageData?
        get() {
            val imgMap = this.specialImageMap
            var result : ImageData? = null
            if (imgMap != null) {
                if (IconSetting.OK_ICON in imgMap) {
                    result = imgMap[IconSetting.OK_ICON]
                }
            }
            return result
        }

    /**
     * ok blank image
     */
    val okImageBlank : ImageData?
        get() {
            val imgMap = this.specialImageBlankMap
            var result : ImageData? = null
            if (imgMap != null) {
                if (IconSetting.OK_ICON in imgMap) {
                    result = imgMap[IconSetting.OK_ICON]
                }
            }
            return result
        } 

 
    /**
     * mine image
     */
    val mineImage : ImageData?
        get() {
            val imgMap = this.specialImageMap
            var result : ImageData? = null
            if (imgMap != null) {
                if (IconSetting.NG_ICON in imgMap) {
                    result = imgMap[IconSetting.NG_ICON]
                }
            }
            return result
        }

    /**
     * mine blank image
     */
    val mineImageBlank : ImageData?
        get() {
            val imgMap = this.specialImageBlankMap
            var result : ImageData? = null
            if (imgMap != null) {
                if (IconSetting.NG_ICON in imgMap) {
                    result = imgMap[IconSetting.NG_ICON]
                }
            }
            return result
        } 
    /**
     * light marker image
     */
    val lightMarkerImage : ImageData?
        get() {
            val imgMap = this.specialImageMap
            var result : ImageData? = null
            if (imgMap != null) {
                if (IconSetting.LIGHT_MARKER in imgMap) {
                    result = imgMap[IconSetting.LIGHT_MARKER]
                }
            }
            return result
        }

 
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
        setupSpecialImages(ctx, iconSetting)
    }
    /**
     * setup numbers
     */
    fun setupNumbers(ctx: CanvasRenderingContext2D) {
        setupNumbers0(ctx)
        setupNumbers1()
    }
    /**
     * update special image with specied icon setting.
     */
    fun updateSpecialImage(
        iconSetting: IconSetting) {
        val nodeId = this.nodeId     
        if (nodeId != null) {
            val canvas = jQuery(nodeId)[0] as HTMLCanvasElement
            val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
     
            updateSpecialImage(ctx, iconSetting)
        }
    }


    /**
     * update special image with specied icon setting.
     */
    fun updateSpecialImage(
        ctx: CanvasRenderingContext2D,
        iconSetting: IconSetting) {
        setupSpecialImages(ctx, iconSetting)
    }

    /**
     * setup special images
     */
    fun setupSpecialImages(ctx: CanvasRenderingContext2D,
        iconSetting: IconSetting) {
        specialImageBlankMap = HashMap<String, ImageData>()  
        specialImageMap = HashMap<String, ImageData>()
        val iconMap = iconSetting.icons
        val paramMap =  specialImageParameterMap
        iconMap.forEach { 
            var textureSize: Int = defaultTextureSize
            var textureRatio: Float = defaultTextureRatio
            if (it.key in paramMap) {
                val param = paramMap[it.key]
                textureSize = param!!.textureSize
                textureRatio = param!!.contentsRatio 
            } 
            val imgSrc = createImage(ctx, it.value,
                textureSize, textureRatio)
            val imgBlank = createImageBlank(imgSrc)
            specialImageMap!![it.key] = imgSrc
            specialImageBlankMap!![it.key] = imgBlank
             
        } 
        
       // drawScal()
    }

    /**
     * setup numbers
     */
    fun setupNumbers0(
        ctx : CanvasRenderingContext2D,
        textureSize: Int = defaultTextureSize,
        textureRatio: Float = defaultTextureRatio) {
        val savedFont = ctx.font 
        val savedBaseline: CanvasTextBaseline = ctx.textBaseline
        val fontSize = round(textureSize * textureRatio).toInt()
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
            
            xcoord += (textureSize - texMtx.width) / 2.0
            val ycoord = buttonTextureSize * .5
            ctx.fillText(numStr, xcoord, ycoord) 
            val img = ctx.getImageData(0.0, 0.0,
               textureSize.toDouble(),
               textureSize.toDouble())
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
     * create image and blank data
     */
    fun createImageBlank(img: ImageData): ImageData {
        val arr = Uint8ClampedArray(
            img.width * img.height * 2 * 4) 
        arr.set(img.data, 0) 
        return ImageData(arr, img.width) 
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
     * create image from persistence icon
     */
    fun createImage(ctx: CanvasRenderingContext2D,
        icon : Persistence.Icon,
        textureSize: Int,
        textureRatio: Float): ImageData {
        return createImage(ctx, 
            createFontawesomeIconDef(
                icon.prefix, icon.iconName),
                textureSize, textureRatio)
    }

    /**
     * create image data from fontawesome icon data.
     */
    fun createImage(ctx: CanvasRenderingContext2D,
        iconDef: fontawesome.IconDefinition,
        textureSize: Int = defaultTextureSize,
        textureRatio: Float = defaultTextureRatio): ImageData {
       
        val imgSize = textureSize.toDouble() * textureRatio
        val iconWidth = iconDef.icon[0] as Int
        val iconHeight = iconDef.icon[1] as Int
        val scale = imgSize / max(iconWidth, iconHeight)
        val sizeDisplaying = arrayOf(scale * iconWidth, scale * iconHeight) 
        val displacement = (textureSize * (1 - textureRatio)) / 2 
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
        val result = ctx.getImageData(0.0, 0.0,
            textureSize.toDouble(),
            textureSize.toDouble())
        ctx.clearRect(0.0, 0.0, width.toDouble(), height.toDouble())
 
        return result
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
    fun drawScal() {
        val img = mineImage
        val canvas = jQuery(nodeId!!)[0] as HTMLCanvasElement
        val ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        ctx.putImageData(img!!, 10.0, 10.0)         
    }
}

// vi: se ts=4 sw=4 et:
