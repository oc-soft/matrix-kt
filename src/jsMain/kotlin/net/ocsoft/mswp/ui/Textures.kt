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
    val numberBlankTextureMap : MutableMap<Int, WebGLTexture>
        = HashMap<Int, WebGLTexture>() 
    /**
     * number flag texture map
     */
    val numberFlagTextureMap : MutableMap<Int, WebGLTexture>
        = HashMap<Int, WebGLTexture>() 


    /**
     * ng texture
     */
    var ngTexture: WebGLTexture? = null

    /**
     * ng flag texture
     */
    var ngFlagTexture: WebGLTexture? = null


    /**
     * ok texture
     */
    var okTexture: WebGLTexture? = null
        
    /**
     * ok flag texture
     */
    var okFlagTexture: WebGLTexture? = null
 
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
    fun getNumberBlankTexture(num : Int): WebGLTexture? {
        var numTexMap = this.numberBlankTextureMap
        var result: WebGLTexture?
        result = numTexMap[num]
        return result
    }
    /**
     * get number flag texture
     */
    fun getNumberFlagTexture(num : Int): WebGLTexture? {
        var numTexMap = this.numberFlagTextureMap
        var result: WebGLTexture?
        result = numTexMap[num]
        return result
    }
     
    /**
     * setup
     */
    fun setup(gl: WebGLRenderingContext, glyph: Glyph) {
        setupTransparentBlackTexture(gl)
        setupNumberBlankTexture(gl, glyph)
        setupNumberFlagTexture(gl, glyph)
        setupNgTexture(gl, glyph)
        setupOkTexture(gl, glyph)
        setupNgFlagTexture(gl, glyph)
        setupOkFlagTexture(gl, glyph)
         
        setupPointLightMarkerTexture(gl, glyph)
    }
    /**
     * teardown
     */
    fun teardown(gl: WebGLRenderingContext) {
        teardownPointLightMarkerTexture(gl)
        teardownOkFlagTexture(gl)
        teardownNgFlagTexture(gl)
        teardownOkTexture(gl)
        teardownNgTexture(gl)
        teardownNumberFlagTexture(gl)
        teardownNumberBlankTexture(gl)
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
                transBlack)
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
    fun setupNumberBlankTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        for (num in 0..9) {
            val numImage = glyph.getNumberBlank(num)
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
                    numberBlankTextureMap[num] = tex
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
    fun teardownNumberBlankTexture(gl: WebGLRenderingContext) {
        for (num in 0..9) {
            val tex = numberBlankTextureMap[num] 
            if (tex != null) {
                gl.deleteTexture(tex)
            }
        }
        numberBlankTextureMap.clear()
    }
    /**
     * setup number flag texture
     */
    fun setupNumberFlagTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        for (num in 0..9) {
            val numImage = glyph.getNumberFlag(num)
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
                    numberFlagTextureMap[num] = tex
                }
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)

    }
    /**
     * tear down number flag texture
     */
    fun teardownNumberFlagTexture(gl: WebGLRenderingContext) {
        for (num in 0..9) {
            val tex = numberFlagTextureMap[num] 
            if (tex != null) {
                gl.deleteTexture(tex)
            }
        }
        numberFlagTextureMap.clear()
    }
  
    /**
     * update number image blank texture
     */
    fun updateNumberBlankTexture(
        gl : WebGLRenderingContext,
        glyph : Glyph) {
        teardownNumberBlankTexture(gl)
        setupNumberBlankTexture(gl, glyph)
    }

    /**
     * update number flag texture
     */
    fun updateNumberFlagTexture(
        gl : WebGLRenderingContext,
        glyph : Glyph) {
        teardownNumberFlagTexture(gl)
        setupNumberFlagTexture(gl, glyph)
    }

 
    /**
     * setup ok image texture
     */
    fun setupOkTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        val okImage = glyph.okBlank
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
                okTexture = tex
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)

    }

    /**
     * setup ok flag texture
     */
    fun setupOkFlagTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        val okImage = glyph.okFlag
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
                okFlagTexture = tex
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)

    }



    /**
     * setup ng image texture
     */
    fun setupNgTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        val ngImage = glyph.mineBlank
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
                ngTexture = tex
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)

    }

    /**
     * setup ng flag image texture
     */
    fun setupNgFlagTexture(gl : WebGLRenderingContext,
        glyph : Glyph) {
        
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        val ngImage = glyph.mineFlag
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
                ngFlagTexture = tex
            }
        }
        gl.bindTexture(
            WebGLRenderingContext.TEXTURE_2D,
            savedTex as WebGLTexture?)

    }

    /**
     * tear down ok image texture
     */
    fun teardownOkTexture(gl: WebGLRenderingContext) {
        val tex = okTexture
        if (tex != null) {
            gl.deleteTexture(tex)
        }
        this.okTexture = null
    }
 

    /**
     * tear down ng image texture
     */
    fun teardownNgTexture(gl: WebGLRenderingContext) {
        val tex = ngTexture
        if (tex != null) {
            gl.deleteTexture(tex)
        }
        this.ngTexture = null
    }

    /**
     * tear down ok flag texture
     */
    fun teardownOkFlagTexture(gl: WebGLRenderingContext) {
        val tex = okFlagTexture
        if (tex != null) {
            gl.deleteTexture(tex)
        }
        this.okFlagTexture = null
    }
 

    /**
     * tear down ng flag texture
     */
    fun teardownNgFlagTexture(gl: WebGLRenderingContext) {
        val tex = ngFlagTexture
        if (tex != null) {
            gl.deleteTexture(tex)
        }
        this.ngFlagTexture = null
    }


    /**
     * update ok texture
     */
    fun updateOkTexture(gl: WebGLRenderingContext,
        glyph: Glyph) {
        teardownOkTexture(gl)
        setupOkTexture(gl, glyph)
    }
 
    /**
     * update ng texture
     */
    fun updateNgTexture(gl: WebGLRenderingContext,
        glyph: Glyph) {
        teardownNgTexture(gl)
        setupNgTexture(gl, glyph)
    }

    /**
     * update ok flag texture
     */
    fun updateOkFlagTexture(gl: WebGLRenderingContext,
        glyph: Glyph) {
        teardownOkFlagTexture(gl)
        setupOkFlagTexture(gl, glyph)
    }
 
    /**
     * update ng image texture
     */
    fun updateNgFlagTexture(gl: WebGLRenderingContext,
        glyph: Glyph) {
        teardownNgFlagTexture(gl)
        setupNgFlagTexture(gl, glyph)
    }

 
    /**
     * update point light marker texture
     */
    fun updatePointLightMarkerTexture(
        gl: WebGLRenderingContext,
        glyph: Glyph) {
        teardownPointLightMarkerTexture(gl)
        setupPointLightMarkerTexture(gl, glyph)
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
