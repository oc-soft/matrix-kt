package net.ocsoft.mswp.ui

import org.khronos.webgl.*
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.HashMap
import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.Float64Array
import org.khronos.webgl.ArrayBufferView

/**
 * manage textures
 */
class Textures {
    companion object {
        /**
         * mine button texture index
         */
        val ButtonTextureIndex = WebGLRenderingContext.TEXTURE0

        /**
         * light marker texture index
         */
        val LightMarkerTextureIndex = WebGLRenderingContext.TEXTURE1


        /**
         * shadow mapping texture index
         */
        val ShadowmappingTextureIndex = WebGLRenderingContext.TEXTURE2

        /**
         * matrix from projection coordinate to texture coordinate
         */
        val matrixFromProjectToTexture = Float64Array(arrayOf(
            0.5, 0.0, 0.0, 0.0,
            0.0, 0.5, 0.0, 0.0,
            0.0, 0.0, 0.5, 0.0,
            0.5, 0.5, 0.5, 1.0))
    }

    /**
     * number texture map
     */
    val numberImageBlankTextureMap : MutableMap<Int, WebGLTexture>
        = HashMap<Int, WebGLTexture>() 

    /**
     * ng texture
     */
    var ngImageTexture: WebGLTexture? = null

    /**
     * ok texture
     */
    var okImageTexture: WebGLTexture? = null
        

    /**
     * point light marker
     */
    var pointLightMarkerTexture: WebGLTexture? = null

    /**
     * to use for dummy
     */
    var blackTransparentTexture : WebGLTexture? = null

    /**
     * get number texture
     */
    fun getNumberImageBlankTexture(num : Int): WebGLTexture? {
        var numTexMap = this.numberImageBlankTextureMap
        var result: WebGLTexture?
        result = numTexMap[num]
        return result
    }
    
    /**
     * setup
     */
    fun setup(gl: WebGLRenderingContext, glyph: Glyph) {
        setupTransparentBlackTexture(gl)
        setupNumberImageBlankTexture(gl, glyph)
        setupNgImageTexture(gl, glyph)
        setupOkImageTexture(gl, glyph)
        setupPointLightMarkerTexture(gl, glyph)
    }
    /**
     * teardown
     */
    fun teardown(gl: WebGLRenderingContext) {
        teardownPointLightMarkerTexture(gl)
        teardownOkImageTexture(gl)
        teardownNgImageTexture(gl)
        teardownNumberImageBlankTexture(gl)
        teardownTransparentBlackTexture(gl) 
        teardownPointLightMarkerTexture(gl)
    }

    /**
     * setup transparent black texture
     */
    fun setupTransparentBlackTexture(gl: WebGLRenderingContext) {
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        val tex = gl.createTexture();
        
       
        if (tex != null) { 
            val transBlack = Uint8Array(Array<Byte>(4) { 0x00 })

            gl.bindTexture(
                WebGLRenderingContext.TEXTURE_2D,
                tex)
            gl.texImage2D(WebGLRenderingContext.TEXTURE_2D,
                0,
                WebGLRenderingContext.RGBA,
                1, 1, 0,
                WebGLRenderingContext.RGBA,
                WebGLRenderingContext.UNSIGNED_BYTE,
                transBlack as ArrayBufferView)
            this.blackTransparentTexture = tex
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)
    } 

    /**
     * tear down transparent black texture
     */
    fun teardownTransparentBlackTexture(gl: WebGLRenderingContext) {
        gl.deleteTexture(this.blackTransparentTexture)
        this.blackTransparentTexture = null
    }

    /**
     * setup number texture
     */
    fun setupNumberImageBlankTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        for (num in 0..9) {
            val numImage = glyph.getNumberImageBlank(num)
            if (numImage != null) {
                val tex = gl.createTexture();
                if (tex != null) {
                    gl.bindTexture(
                        WebGLRenderingContext.TEXTURE_2D,
                        tex)
                    gl.texImage2D(WebGLRenderingContext.TEXTURE_2D,
                        0,
                        WebGLRenderingContext.RGBA,
                        WebGLRenderingContext.RGBA,
                        WebGLRenderingContext.UNSIGNED_BYTE,
                        numImage)
                    gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D)
                    numberImageBlankTextureMap[num] = tex
                }
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)

    }
    /**
     * tear down number texture
     */
    fun teardownNumberImageBlankTexture(gl: WebGLRenderingContext) {
        for (num in 0..9) {
            val tex = numberImageBlankTextureMap[num] 
            if (tex != null) {
                gl.deleteTexture(tex)
            }
        }
        numberImageBlankTextureMap.clear()
    }

    /**
     * update number image blank texture
     */
    fun updateNumberImageBlankTexture(
        gl : WebGLRenderingContext,
        glyph : Glyph) {
        teardownNumberImageBlankTexture(gl)
        setupNumberImageBlankTexture(gl, glyph)
    }

    /**
     * setup ok image texture
     */
    fun setupOkImageTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        val okImage = glyph.okImageBlank
        if (okImage != null) {
            val tex = gl.createTexture();
            if (tex != null) {
                gl.bindTexture(
                    WebGLRenderingContext.TEXTURE_2D,
                    tex)
                gl.texImage2D(WebGLRenderingContext.TEXTURE_2D,
                    0,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.UNSIGNED_BYTE,
                    okImage)
                gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D)
                okImageTexture = tex
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)

    }


    /**
     * setup ng image texture
     */
    fun setupNgImageTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        val ngImage = glyph.mineImageBlank
        if (ngImage != null) {
            val tex = gl.createTexture();
            if (tex != null) {
                gl.bindTexture(
                    WebGLRenderingContext.TEXTURE_2D,
                    tex)
                gl.texImage2D(WebGLRenderingContext.TEXTURE_2D,
                    0,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.UNSIGNED_BYTE,
                    ngImage)
                gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D)
                ngImageTexture = tex
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)

    }

    
    /**
     * tear down ok image texture
     */
    fun teardownOkImageTexture(gl: WebGLRenderingContext) {
        val tex = okImageTexture
        if (tex != null) {
            gl.deleteTexture(tex)
        }
        this.okImageTexture = null
    }
 

    /**
     * tear down ng image texture
     */
    fun teardownNgImageTexture(gl: WebGLRenderingContext) {
        val tex = ngImageTexture
        if (tex != null) {
            gl.deleteTexture(tex)
        }
        this.ngImageTexture = null
    }

    /**
     * update ok image texture
     */
    fun updateOkImageTexture(gl: WebGLRenderingContext,
        glyph: Glyph) {
        teardownOkImageTexture(gl)
        setupOkImageTexture(gl, glyph)
    }
 
    /**
     * update ng image texture
     */
    fun updateNgImageTexture(gl: WebGLRenderingContext,
        glyph: Glyph) {
        teardownNgImageTexture(gl)
        setupNgImageTexture(gl, glyph)
    }
    /**
     * update ng image texture
     */
    fun updateNgImageTexture1(gl: WebGLRenderingContext,
        glyph: Glyph) {
        val mineImage = glyph.mineImageBlank
        if (mineImage != null) {
            if (ngImageTexture != null) {
                val savedTex = gl.getParameter(
                    WebGLRenderingContext.TEXTURE_BINDING_2D)

                gl.bindTexture(
                    WebGLRenderingContext.TEXTURE_2D,
                    ngImageTexture)
                gl.texImage2D(WebGLRenderingContext.TEXTURE_2D,
                    0,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.UNSIGNED_BYTE,
                    mineImage)
                gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D)

                gl.bindTexture(
                    WebGLRenderingContext.TEXTURE_2D,
                    savedTex as WebGLTexture?)
            }
        }
    }

    /**
     * setup light image texture
     */
    fun setupPointLightMarkerTexture(gl: WebGLRenderingContext,
        glyph: Glyph) {
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D) as WebGLTexture?

        val markerImage = glyph.lightMarkerImage
        if (markerImage != null) {
            val tex = gl.createTexture();
            if (tex != null) {
                gl.bindTexture(
                    WebGLRenderingContext.TEXTURE_2D,
                    tex)
                gl.texImage2D(WebGLRenderingContext.TEXTURE_2D,
                    0,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.RGBA,
                    WebGLRenderingContext.UNSIGNED_BYTE,
                    markerImage)
                gl.generateMipmap(WebGLRenderingContext.TEXTURE_2D)
                pointLightMarkerTexture = tex
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex)
    }

    /**
     * tear down point light marker texture
     */
    fun teardownPointLightMarkerTexture(
        gl: WebGLRenderingContext) {
        val tex = pointLightMarkerTexture
        if (tex != null) {
            gl.deleteTexture(tex)
        }
        this.pointLightMarkerTexture = null
    }
}

// vi: se ts=4 sw=4 et:
