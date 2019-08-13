package net.ocsoft.mswp.ui

import org.khronos.webgl.*
import kotlin.collections.Map
import kotlin.collections.MutableMap
import kotlin.collections.HashMap
import org.khronos.webgl.Uint8ClampedArray


/**
 * manage textures
 */
class Textures {
    companion object {
        /**
         * mine button texture index
         */
        val ButtonTextureIndex = WebGLRenderingContext.TEXTURE0 
    }

    /**
     * number texture map
     */
    val numberImageBlankTextureMap : MutableMap<Int, WebGLTexture>
        = HashMap<Int, WebGLTexture>() 
       
    /**
     * to use for dummy
     */
    var blackTransparentTexture : WebGLTexture? = null

    /**
     * get number texture
     */
    fun getNumberImageBlankTexture(num : Int): WebGLTexture? {
        var numTexMap = this.numberImageBlankTextureMap
        var result: WebGLTexture? = null
        if (numTexMap != null) {
            result = numTexMap[num];
        } 
        return result
    }
    
    /**
     * setup
     */
    fun setup(gl: WebGLRenderingContext, glyph: Glyph) {
        setupTransparentBlackTexture(gl)
        setupNumberImageBlankTexture(gl, glyph)
    }
    /**
     * teardown
     */
    fun teardown(gl: WebGLRenderingContext) {
        teardownNumberImageBlankTexture(gl)
        teardownTransparentBlackTexture(gl) 
    }

    /**
     * setup transparent black texture
     */
    fun setupTransparentBlackTexture(gl: WebGLRenderingContext) {
        val savedTex = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D)

        val tex = gl.createTexture();
        
       
        if (tex != null) { 
            val transBlack = Uint8ClampedArray(Array<Byte>(4) { 0x00 })

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
                    numberImageBlankTextureMap[num] = tex!!
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
}
