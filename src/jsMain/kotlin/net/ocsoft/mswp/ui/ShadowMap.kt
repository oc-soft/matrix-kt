package net.ocsoft.mswp.ui

import kotlin.collections.sort
import kotlin.collections.last
import kotlin.math.pow
import kotlin.math.sqrt

import org.w3c.dom.HTMLCanvasElement

import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLTexture
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLRenderbuffer
import org.khronos.webgl.Int32Array
import org.khronos.webgl.Float64Array
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get

import net.ocsoft.mswp.Camera
import net.ocsoft.mswp.Orthographic
import net.ocsoft.mswp.ui.grid.Buttons
import net.ocsoft.mswp.ui.grid.Display

class ShadowMap {

    /**
     * orthographic parameter
     */
    var orthoGraphic: Orthographic? = null
 
       
    /**
     * set up 
     */
    fun setup(
        grid: Grid,
        gl: WebGLRenderingContext) {
        
        val viewport = gl.getParameter(
            WebGLRenderingContext.VIEWPORT) as Int32Array?
        val canvas = gl.canvas as HTMLCanvasElement 
        val texture = createShadowDepthTexture(gl, canvas.width, canvas.height)
        val depthBuffer = createRenderDepthBuffer(gl,
            canvas.width, canvas.height)

        val renderingCtx = grid.renderingCtx
        renderingCtx.shadowDepthTexture = texture
        renderingCtx.depthForShadowDepth = depthBuffer
        renderingCtx.shadowDepthFramebuffer =
            createFrameBufferForShadowDepth(gl, texture!!, depthBuffer!!)

        orthoGraphic = calcOrtho(grid)
    } 


    
    /**
     * draw shadow depth
     */
    fun drawScene(
        grid: Grid,
        gl: WebGLRenderingContext) {

        val savedFramebuffer = gl.getParameter(
            WebGLRenderingContext.FRAMEBUFFER_BINDING) as WebGLFramebuffer?
        gl.bindFramebuffer(
            WebGLRenderingContext.FRAMEBUFFER,
            grid.renderingCtx.shadowDepthFramebuffer)
        beginEnv(grid, gl)
        drawSceneI(grid, gl)
        endEnv(grid, gl)
        gl.bindFramebuffer(
            WebGLRenderingContext.FRAMEBUFFER,
            savedFramebuffer)
    }

    /**
     * draw shadow depth
     */
    fun drawSceneI(
        grid: Grid,
        gl: WebGLRenderingContext) {

        val savedProgram = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        gl.useProgram(grid.renderingCtx.shadowMapShaderProgram) 
        updateProjectionMatrix(grid, gl)
        drawButtons(grid, gl)
        gl.useProgram(savedProgram) 
    }

    /**
     * draw buttons
     */
    fun drawButtons(
        grid: Grid,
        gl: WebGLRenderingContext) {
        val savedArrayBuffer = gl.getParameter(
                WebGLRenderingContext.ARRAY_BUFFER_BINDING) as WebGLBuffer?

        for (rowIndex in 0 until grid.rowCount) {
            for (colIndex in 0 until grid.columnCount) { 
                grid.display.drawButtonI(gl, rowIndex, colIndex)
            }
        }
        gl.bindBuffer(
            WebGLRenderingContext.ARRAY_BUFFER,
            savedArrayBuffer)
    }

    /**
     * setup env
     */
    fun beginEnv(grid: Grid,
        gl: WebGLRenderingContext) {
        gl.clearColor(0f, 0f, 0f, 0f)
        grid.setupEnv(gl) 
        gl.disable(WebGLRenderingContext.BLEND) 
    }

    /**
     * restore gl 
     */
    fun endEnv(grid: Grid,
        gl: WebGLRenderingContext) {
    }

    /**
     * update projection matrix
     */
    fun updateProjectionMatrix(
        grid: Grid,
        gl: WebGLRenderingContext) {
         val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val uProjMat = gl.getUniformLocation(shaderProg, 
                "uProjectionMatrix")    
            if (uProjMat != null) { 
                val projMat = getOrthoGraphicMatrix(grid)
                gl.uniformMatrix4fv(uProjMat, false, projMat!!)
            }
        }
    } 


    /**
     * create frame buffer for shadow depth
     */
    fun createFrameBufferForShadowDepth(
        gl: WebGLRenderingContext,
        texture: WebGLTexture,
        depthBuffer: WebGLRenderbuffer): WebGLFramebuffer? {
        val result = gl.createFramebuffer()

        val savedFramebuffer = gl.getParameter(
            WebGLRenderingContext.FRAMEBUFFER_BINDING) as WebGLFramebuffer?

        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER, result)
        
        gl.framebufferTexture2D(WebGLRenderingContext.FRAMEBUFFER,
            WebGLRenderingContext.COLOR_ATTACHMENT0,
            WebGLRenderingContext.TEXTURE_2D,
            texture, 0)

        gl.framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
            WebGLRenderingContext.DEPTH_ATTACHMENT,
            WebGLRenderingContext.RENDERBUFFER, depthBuffer)
         

        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER, savedFramebuffer)

        return result
    }


    /**
     * create render buffer for shadow depth
     */
    fun createRenderDepthBuffer(
        gl: WebGLRenderingContext,
        width: Int,
        height: Int): WebGLRenderbuffer? {
        val result = gl.createRenderbuffer()
        val savedRenderbuffer = gl.getParameter(
            WebGLRenderingContext.RENDERBUFFER_BINDING) as WebGLRenderbuffer?
        gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER, result)

        gl.renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
            WebGLRenderingContext.DEPTH_COMPONENT16,
            width, height)  
       
        gl.bindRenderbuffer(
            WebGLRenderingContext.RENDERBUFFER,
            savedRenderbuffer) 
        return result
    }


    /**
     * create shadow depth texture
     */
    fun createShadowDepthTexture(
        gl: WebGLRenderingContext,
        width: Int,
        height: Int): WebGLTexture? {
        
        val savedTexture = gl.getParameter(
            WebGLRenderingContext.TEXTURE_BINDING_2D) as WebGLTexture?
        
        val result = gl.createTexture()
        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, result)
        gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0,
            WebGLRenderingContext.RGBA, width, height,
            0, WebGLRenderingContext.RGBA,
            WebGLRenderingContext.UNSIGNED_BYTE, null)

        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, savedTexture)

        return result
    }
    /**
     * calc orth graphic parameter
     */
    fun calcOrtho(grid: Grid): Orthographic {
        return calcOrtho(grid.glrs!!,
            grid.pointLight!!,
            grid.camera!!,  
            grid.display!!) 
    }    

    /**
     * calc orth graphic parameter
     */
    fun calcOrtho(glrs: glrs.InitOutput,
        pointLight: net.ocsoft.mswp.PointLight,
        camera: Camera,
        display: Display): Orthographic {
        val bounds = calcBoundsForRendering(
            glrs, pointLight, camera, display)
        
        return Orthographic(
            bounds.first[0], bounds.first[2],
            bounds.first[1], bounds.first[3], 
            0.3f, bounds.second)
    }

    /**
     * calculate bounds for rendering
     */
    fun calcBoundsForRendering(
        glrs: glrs.InitOutput,
        pointLight: net.ocsoft.mswp.PointLight,
        camera: Camera,
        display: Display): Pair<FloatArray, Float> {
        val ptLight = pointLight.point
        val center = camera.center
        val normVec = Float64Array(Array<Double>(ptLight.size) { 
            ptLight[it].toDouble() - center[it].toDouble()
        })
        var normVecLength = 0.0
        
        for (i in 0 until normVec.length) {
            normVecLength += normVec[i].pow(2.0)
        }
        normVecLength = sqrt(normVecLength)
        
        val projPlaneRef = glrs.plane_create(normVec, 
            Float64Array(Array<Double>(center.size) { center[it].toDouble() }) )

        val vertices = getButtonsMovementVertices(display)
         
        var farFromCenter = Array<Float?>(1) { null }
        vertices.forEach {
            val vertex = it
            val projectedPt = glrs.plane_project(projPlaneRef, 
                Float64Array(Array<Double>(vertex.size) {
                    vertex[it].toDouble() }))!!
            var res = 0f
            for (idx0 in 0 until projectedPt.length) {
                res += (projectedPt[idx0].toFloat() - 
                    center[idx0]).pow(2f)
            } 
            res = sqrt(res)
            if (farFromCenter[0] == null) {
                farFromCenter[0] = res
            } else if (farFromCenter[0]!! > res) {
                farFromCenter[0] = res
            } 
        } 
        
        val result = Pair(floatArrayOf(
                -farFromCenter[0]!!,
                -farFromCenter[0]!!,
                farFromCenter[0]!!, 
                farFromCenter[0]!!),
                normVecLength.toFloat())                
            
        glrs.plane_release(projPlaneRef)

        return result
    }
   
     
    

    /**
     * calc near vertex distance from plane
     * planeRef is a plane on ceter
     */
    private fun calcFarFromEye(glrs: glrs.InitOutput,
        planeRef: Number,
        vertices: Array<FloatArray>):FloatArray?  {
        
        val floatIdxRef = glrs.plane_sort_points_0(planeRef, vertices) 
       
        val floatKeysRef = glrs.float_indices_get_float_keys(
            floatIdxRef) 
        var result : FloatArray? = null
        if (glrs.float_vec_size(floatKeysRef).toInt() > 0) {
            val floatRef = glrs.float_vec_get(floatKeysRef, 0)
            
            val indices = glrs.float_indices_get_indices(
                floatIdxRef, floatRef)
            if (indices != null && indices.length > 0) {
                result = vertices[indices[0]]
            }
            glrs.float_release(floatRef)
        }
        glrs.float_vec_release(floatKeysRef)
        glrs.float_indices_release(floatIdxRef)
        return result 
    }
    
          
     

    /**
     * get buttons movement vertices
     */
    fun getButtonsMovementVertices(
        display: Display): Array<FloatArray> {
        return display.calcButtonsMovingBounds() 
    }

    /**
     * othographic matrix
     */
    fun getOrthoGraphicMatrix(grid: Grid) :Float32Array? {
        return getOrthoGraphicMatrix(grid)
    }
 
    /**
     * othographic matrix
     */
    fun getOrthoGraphicMatrix(glrs: glrs.InitOutput) :Float32Array? {
        val ortho = this.orthoGraphic
        var result: Float32Array? = null
        if (ortho != null) {
            val matRef = glrs.matrix_new_ortho(
                ortho.left, ortho.right,
                ortho.bottom, ortho.top, ortho.zNear, ortho.zFar)
            result = glrs.matrix_get_components_col_order_32(matRef)
            glrs.matrix_release(matRef) 
        }
        return result
    }
 
}

// vi: se ts=4 sw=4 et: 
