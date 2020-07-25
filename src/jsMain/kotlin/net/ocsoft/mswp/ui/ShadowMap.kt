package net.ocsoft.mswp.ui

import kotlin.collections.sort
import kotlin.collections.last
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.absoluteValue

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
     * frame buffer size
     */
    var frameBufferSize: IntArray? = null 
       
    /**
     * set up 
     */
    fun setup(
        grid: Grid,
        gl: WebGLRenderingContext) {
        
        val viewport = gl.getParameter(
            WebGLRenderingContext.VIEWPORT) as Int32Array?
        val canvas = gl.canvas as HTMLCanvasElement 

        frameBufferSize = intArrayOf(
            Display.calcPower2Value(canvas.width),
            Display.calcPower2Value(canvas.height))


        val texture = createShadowDepthTexture(gl,
            frameBufferSize!![0], frameBufferSize!![1])
        val depthBuffer = createRenderDepthBuffer(gl,
            frameBufferSize!![0], frameBufferSize!![1])

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
        val savedViewport = gl.getParameter(
            WebGLRenderingContext.VIEWPORT) as Int32Array

        gl.bindFramebuffer(
            WebGLRenderingContext.FRAMEBUFFER,
            grid.renderingCtx.shadowDepthFramebuffer)
        gl.viewport(0, 0, frameBufferSize!![0], frameBufferSize!![1])
        beginEnv(grid, gl)
        drawSceneI(grid, gl)
        endEnv(grid, gl)
        gl.flush()
        
        gl.viewport(savedViewport[0], savedViewport[1],
            savedViewport[2], savedViewport[3])
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
        drawBoard(grid, gl)
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

        grid.display.bindButtonVerticesBuffer(gl)
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
     * draw board 
     */
    fun drawBoard(
        grid: Grid,
        gl: WebGLRenderingContext) {
        val savedArrayBuffer = gl.getParameter(
                WebGLRenderingContext.ARRAY_BUFFER_BINDING) as WebGLBuffer?

        grid.display.drawBoardI(gl)
        gl.bindBuffer(
            WebGLRenderingContext.ARRAY_BUFFER,
            savedArrayBuffer)
    }


    /**
     * debug button vertices
     */
    fun debugButtonVertics(
        grid: Grid,
        rowIndex: Int, colIndex: Int) {
        val glrs = grid.glrs!!
        val projMat = calcProjectionMatrix(grid)
        val lookFromMat = getLookFromLightPoint(grid)!!
        val orthoMat = getOrthoGraphicMatrix(grid)!!
        val modelMat = grid.display.getButtonMatrixForDrawing(
            rowIndex, colIndex)
        val lookFromMatRef = glrs.matrix_create_with_components_col_order(
            Float64Array(Array<Double>(lookFromMat.length) {
                lookFromMat[it].toDouble() }))
 
        val orthoMatRef = glrs.matrix_create_with_components_col_order(
            Float64Array(Array<Double>(orthoMat.length) {
                orthoMat[it].toDouble() }))

        val matRef = grid.glrs!!.matrix_create_with_components_col_order(
            Float64Array(Array<Double>(projMat!!.length) {
                projMat[it].toDouble() }))
        val modelMatRef = grid.glrs!!.matrix_create_with_components_col_order(
            Float64Array(Array<Double>(modelMat.size) {
                modelMat[it].toDouble() }))
        grid.glrs!!.matrix_multiply_mut(matRef, modelMatRef)
        val vertices = grid.buttons.mineButton.vertices
        println("buton[${rowIndex}][${colIndex}]")
        for (idx in 0 until vertices.size / 3) {
            val srcPt = Float32Array(
                Array<Float>(4) {
                    idx1 -> 
                    if (idx1 < 3) {
                        vertices[3 * idx + idx1]
                    } else {
                        1f
                    } 
                })

            val pt = glrs.matrix_apply_r_32(matRef, srcPt)
            println("${pt!![0]}, ${pt!![1]}, ${pt!![2]}, ${pt!![3]}")
        }
        
        glrs.matrix_release(lookFromMatRef)
        glrs.matrix_release(orthoMatRef) 
        glrs.matrix_release(modelMatRef)
        glrs.matrix_release(matRef) 

    }

    /**
     * setup env
     */
    fun beginEnv(grid: Grid,
        gl: WebGLRenderingContext) {
        val savedBackColor = grid.backColor.copyOf()
        for (i in 0 until grid.backColor.size) {
            grid.backColor[i] = 0.0f
        } 
        grid.backColor[0] = 1f
        grid.setupEnv(gl)

        for (i in 0 until grid.backColor.size) {
            grid.backColor[i] = savedBackColor[i]
        } 

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
                val projMat = calcProjectionMatrix(grid)
                gl.uniformMatrix4fv(uProjMat, false, projMat!!)
            }
        }
    } 
    /**
     * setup shadow setting for drawing
     */
    fun setupShadowSettingForDrawing(
        grid: Grid,
        gl: WebGLRenderingContext) {
        attachShadowProjectionMatrix(gl, grid)
        attachShadowTexture(gl, grid.renderingCtx)
    }
    /**
     * attach shadow projection matrix into current program
     */
    fun attachShadowProjectionMatrix(
        gl: WebGLRenderingContext,
        grid: Grid) {
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val projMat = getShadowProjectionMatrixForTexture(grid) 
            if (projMat != null) {
                val uProjMat = gl.getUniformLocation(shaderProg, 
                    "uShadowMapProjectionMatrix")    
                if (uProjMat != null) { 
                    gl.uniformMatrix4fv(uProjMat, false, projMat)
                }
            }
        } 
    }
    /**
     * attach shadow mapping texture
     */
    fun attachShadowTexture(
        gl: WebGLRenderingContext,
        renderingCtx: RenderingCtx) {
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val uTexLoc = gl.getUniformLocation(shaderProg, 
                    "uShadowDepthSampler") 
            if (uTexLoc != null) {
                var txtNumber = Textures.ShadowmappingTextureIndex
                txtNumber -= WebGLRenderingContext.TEXTURE0
                gl.uniform1i(uTexLoc, txtNumber)
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
        val savedActiveTexture = gl.getParameter(
            WebGLRenderingContext.ACTIVE_TEXTURE) as Int  
        val result = gl.createTexture()
        var txtNumber = Textures.ShadowmappingTextureIndex
        gl.activeTexture(txtNumber)
        gl.bindTexture(WebGLRenderingContext.TEXTURE_2D, result)
        gl.texImage2D(WebGLRenderingContext.TEXTURE_2D, 0,
            WebGLRenderingContext.RGBA, width, height,
            0, WebGLRenderingContext.RGBA,
            WebGLRenderingContext.UNSIGNED_BYTE, null)
        gl.texParameteri(WebGLRenderingContext.TEXTURE_2D,
            WebGLRenderingContext.TEXTURE_WRAP_S,
            WebGLRenderingContext.CLAMP_TO_EDGE);
        gl.texParameteri(WebGLRenderingContext.TEXTURE_2D,
            WebGLRenderingContext.TEXTURE_WRAP_T,
            WebGLRenderingContext.CLAMP_TO_EDGE);
        gl.texParameteri(WebGLRenderingContext.TEXTURE_2D,
            WebGLRenderingContext.TEXTURE_MIN_FILTER,
            WebGLRenderingContext.LINEAR);

        gl.activeTexture(savedActiveTexture)
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
        val nearFarIndices = intArrayOf(0, 1)  
        if (bounds[2][0].absoluteValue >  bounds[2][1].absoluteValue) {
            nearFarIndices[0] = 1
            nearFarIndices[1] = 0
        } 
        val adjustments = floatArrayOf(1f, 1f) 
        
        return Orthographic(
            bounds[0][0] * adjustments[0], bounds[0][1] * adjustments[1],
            bounds[1][0] * adjustments[0], bounds[1][1] * adjustments[1],
            -bounds[2][nearFarIndices[0]], -bounds[2][nearFarIndices[1]])
    }

    /**
     * calculate bounds for rendering
     */
    fun calcBoundsForRendering(
        glrs: glrs.InitOutput,
        pointLight: net.ocsoft.mswp.PointLight,
        camera: Camera,
        display: Display): Array<FloatArray> {
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
        

        val vertices = getGameBounds(display)

        val matRef = getLookFromLightPointI(glrs, camera, pointLight)

         
        var bounds = Array<Array<Float?>>(3) { Array<Float?>(2) { null } }
        val valueSelector: Array<(Int, Int, Float)->Boolean> = arrayOf(
            {
                idx0, idx1, value -> value < bounds[idx0][idx1]!! 
            },
            {
                idx0, idx1, value -> value > bounds[idx0][idx1]!! 
            }
        )
        vertices.forEach {
            vertexElem ->  
            val vertexSrc = Float32Array(Array<Float>(4) {
                if (it < vertexElem.size) {
                    vertexElem[it]
                } else {
                    1f
                }
            })
            val vertex = glrs.matrix_apply_r_32(matRef, vertexSrc)!!
            for (idx0 in 0 until bounds.size) {
                for (idx1 in 0 until bounds[idx0].size) { 
                    if (bounds[idx0][idx1] == null) {
                        bounds[idx0][idx1] = vertex[idx0] 
                    } else if (valueSelector[idx1](
                        idx0, idx1, vertex[idx0])) {
                        bounds[idx0][idx1] = vertex[idx0]
                    } 
                }
            }
        } 
        
        val result = Array(bounds.size) {
            idx0 ->
            FloatArray(bounds[idx0].size) {
                bounds[idx0][it]!!
            }
        }
            
        glrs.matrix_release(matRef)

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
     * calculate all of coordinates for game
     */
    fun getGameBounds(
        display: Display): Array<FloatArray> {
        return display.calcGameBounds()
    }

    /**
     * calculate projection matrix from light point view
     */
    fun calcProjectionMatrix(grid: Grid): Float32Array? {
        val glrs = grid.glrs!!
        val matRef = calcProjectionMatrixI(grid)
        val result = glrs.matrix_get_components_col_order_32(matRef)
        glrs.matrix_release(matRef)
        return result
    }
    /**
     * calculate projection matrix from light point view
     */
    fun calcProjectionMatrixI(grid: Grid): Number {
        val glrs = grid.glrs!!
        val ortho = orthoGraphic!!
        val result = glrs.matrix_new_ortho(
            ortho.left, ortho.right,
            ortho.bottom, ortho.top, ortho.zNear, ortho.zFar)
        val lookatMatRef = getLookFromLightPointI(grid)
        glrs.matrix_multiply_mut(result, lookatMatRef)
        glrs.matrix_release(lookatMatRef)
        return result
    }


    /**
     * get the matrix looking at from light point.
     */
    fun getLookFromLightPoint(grid: Grid): Float32Array? {

        val glrs = grid.glrs!!
        val matRef = getLookFromLightPointI(grid)
        val result = glrs.matrix_get_components_col_order_32(matRef) 
        glrs.matrix_release(matRef)
        return result
    }


    /**
     * get matrix looking at from light poing
     */
    private fun getLookFromLightPointI(grid: Grid): Number {
        val result = getLookFromLightPointI(
            grid.glrs!!, grid.camera!!, grid.pointLight!!)
        return result
     }
    
    /**
     * get matrix looking at from light poing
     */
    private fun getLookFromLightPointI(
        glrs: glrs.InitOutput,
        camera: Camera,
        pointLight: net.ocsoft.mswp.PointLight): Number {
        val ptLight = pointLight.point
        val camCenter = camera.center
        val camUp = camera.up

        val result = glrs.matrix_new_look_at(
            ptLight[0], ptLight[1], ptLight[2],
            camCenter[0], camCenter[1], camCenter[2],
            camUp[0], camUp[1], camUp[2])
        return result
     }
 
    /**
     * othographic matrix
     */
    private fun getOrthoGraphicMatrix(grid: Grid) :Float32Array? {
        return getOrthoGraphicMatrix(grid.glrs!!)
    }
 
    /**
     * othographic matrix
     */
    private fun getOrthoGraphicMatrix(glrs: glrs.InitOutput) :Float32Array? {
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

    /**
     * get shadow mapping projection matrix for texture coordinate
     */
    fun getShadowProjectionMatrixForTexture(grid: Grid): 
        Float32Array? {
        val ortho = this.orthoGraphic
        var result: Float32Array? = null
        if (ortho != null) {
            val glrs = grid.glrs!!
            val projRef = calcProjectionMatrixI(grid)
            val matRef = glrs.matrix_create_with_components_col_order(
               Textures.matrixFromProjectToTexture)
            glrs.matrix_multiply_mut(matRef, projRef)
            result = glrs.matrix_get_components_col_order_32(matRef)
            glrs.matrix_release(matRef) 
            glrs.matrix_release(projRef)
        }
        return result
 
    }
 
}

// vi: se ts=4 sw=4 et: 
