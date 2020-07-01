package net.ocsoft.mswp.ui
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLBuffer
import org.khronos.webgl.WebGLTexture
import org.khronos.webgl.WebGLRenderbuffer
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.Float64Array
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import net.ocsoft.mswp.ui.grid.Display
import net.ocsoft.mswp.ui.Grid


/**
 * provide user interface for editing point light.
 */
class PointLight(
    val pointOffset: Float = 0.1f) {
    

    /**
     * the point on projection plane for editing point
     */
    var pointOnPlane: FloatArray? = null

    /**
     * the normal projection plane vector for edditing point
     */
    var normalVector: FloatArray? = null

    /**
     * point light
     */
    var pointLight: net.ocsoft.mswp.PointLight? = null
   
    /**
     * light editing table
     */
    var lightEditingTable: Array<FloatArray>? = null


    /**
     * light editing table aray cache
     */
    var lightEditingTableArrayCache: Float32Array? = null

    /**
     * optimized for gl rendering
     */
    val lightEditingTableArray: Float32Array? 
        get() {
            var result: Float32Array? = null
            result = lightEditingTableArrayCache  
            if (result == null) {
                result = createLightEditingTableArray()
                lightEditingTableArrayCache = result
            } 
            return result
        } 

    


    /**
     * projected light edit table coordinate
     */
    var lightEditViewTable: Array<Float32Array>? = null

    
    /**
     * model matrix
     */
    val modelMatrix: Float32Array = 
        Float32Array(Array<Float>(16){ 
            if (it / 4 == it % 4) { 1.0f } else { 0.0f }
        })

    /**
     * projection matrix
     */
    var projectionMatrix: Float32Array? = null

    /**
     * viewport
     */
    var viewport: IntArray? = null


    /**
     * create light editing table array
     */
    fun createLightEditingTableArray(): Float32Array? {
        val vertexArray = lightEditingTable
        var result: Float32Array? = null
       
        if (vertexArray != null && vertexArray.size == 4) {
            val triangleCount = vertexArray.size - 2
            result = Float32Array(
                triangleCount * 3  * 3)
            val indices = arrayOf(
                intArrayOf(0, 1, 2),
                intArrayOf(2, 3, 0))
            for (idx in 0..result.length - 1) {
                val coordIdx = idx % 3
                val groupIdx = idx / (3 * 3) 
                val groupOffset = (idx % (3 * 3)) / 3
                val ptIdx = indices[groupIdx][groupOffset]
                result[idx] = vertexArray[ptIdx][coordIdx]
            }
        } 
        return result
    }
    

    /**
     * setup projection plane for editing light point
     */
    fun setupLightUiPlane(
        grid: Grid): Unit {
        val glrs = grid.glrs 
        val renderingCtx = grid.renderingCtx
        pointOnPlane = null
        normalVector = null
        lightEditingTable = null
        projectionMatrix = null
        lightEditingTableArrayCache = null
        lightEditViewTable = null
        if (glrs != null) {
            val board = grid.board
            val buttonsCoord = grid.display.calcButtonsCoordinate() 
             
            val normal = board.normalVector  
            val bounds  = board.bounds
            val planeRef = glrs.plane_create(
                Float64Array(
                    Array<Double>(normal.size) 
                        { normal[it].toDouble() }), 
                Float64Array(
                    Array<Double>(bounds[0].size)
                        { bounds[0][it].toDouble() }))
            
            val floatIdxRef = glrs.plane_sort_points_0(
                planeRef, buttonsCoord) 

            val floatKeysRef = glrs.float_indices_get_float_keys(
                floatIdxRef) 
            var farPtFromBoard: FloatArray? = null
            if (glrs.float_vec_size(floatKeysRef).toInt() > 0) {
                val floatRef = glrs.float_vec_get(
                    floatKeysRef,
                    glrs.float_vec_size(floatKeysRef).toInt() - 1)
                
                val indices = glrs.float_indices_get_indices(
                    floatIdxRef, floatRef)
                if (indices != null && indices.length > 0) {
                    farPtFromBoard = buttonsCoord[indices[0]]
                }

                glrs.float_release(floatRef)
            }
            if (farPtFromBoard != null) {
                pointOnPlane = calcPointOffset(farPtFromBoard, normal) 
                normalVector = normal  
                lightEditingTable = Array<FloatArray>(bounds.size) {
                    projectOnPlane(glrs,
                        normalVector!!, pointOnPlane!!,
                        bounds[it])!!
                }
            }

            glrs.float_vec_release(floatKeysRef)
            glrs.float_indices_release(floatIdxRef)
            glrs.plane_release(planeRef)
        
        }
        if (lightEditingTable != null) {
            projectionMatrix = createProjectionMatrix(grid)    
            viewport = grid.viewport!!
            lightEditViewTable = calcLightEditViewTable(
                glrs!!,
                lightEditingTable!!,
                projectionMatrix!!,
                viewport!!)
        }
    } 


    /**
     * calculate point moved offset from point 
     */
    fun calcPointOffset(point: FloatArray,
        normalDirection: FloatArray): FloatArray {
         
        val result = floatArrayOf(
            point[0] + normalDirection[0] * pointOffset,
            point[1] + normalDirection[1] * pointOffset,
            point[2] + normalDirection[2] * pointOffset)

        return result
    }


    /**
     * project point on plane
     */
    fun projectOnPlane(
        glrs: glrs.InitOutput,
        normal: FloatArray,
        pointOnPlane: FloatArray,
        point: FloatArray): FloatArray? {
        val plane = glrs.plane_create(
            Float64Array(
                Array<Double>(normal.size){ normal[it].toDouble() }),
            Float64Array(
                Array<Double>(pointOnPlane.size){pointOnPlane[it].toDouble()})) 

        val result = projectOnPlane(glrs, plane, point)

        glrs.plane_release(plane)
        return result
    }



    /**
     * project point on plane
     */
    fun projectOnPlane(
        glrs: glrs.InitOutput,
        planeRef: Number,
        point: FloatArray): FloatArray? {
        
        val projPoint = glrs.plane_project(
            planeRef,
            Float64Array(Array<Double>(point.size) { point[it].toDouble() }))
        var result: FloatArray? = null
        if (projPoint != null) {
            result = FloatArray(projPoint.length) { projPoint[it].toFloat() }
        }
        return result
    }

    /**
     * create projection matrix
     */
    fun createProjectionMatrix(
        grid: Grid): Float32Array? {
        return grid.createCameraMatrix()
    }

    /**
     * calculate editing view table
     */
    fun calcLightEditViewTable(
        glrs: glrs.InitOutput,
        lightEditingTable: Array<FloatArray>,
        projectionMatrix: Float32Array, 
        viewport: IntArray): Array<Float32Array> {
         
        
        val coordinates = gl.Matrix.project(glrs, 
            lightEditingTableArray!!,
            modelMatrix,
            projectionMatrix,
            viewport)!!
         
        val result = Array<Float32Array>(coordinates.length / 3) {
            coordinates.subarray(it * 3, it * 3 + 3)
        }
        
        return result 
    }

    /**
     * handle user input
     */
    fun handleUserInput(
        grid: Grid, 
        wgl: WebGLRenderingContext,
        xw: Int, yw: Int) {

        val lightPointW = calcLightPointWindowCoordinte(grid, wgl, xw, yw)

        if (lightPointW != null) {
            val lightPoint = calcPointLightFromEditorMarker(
                grid.glrs!!,
                lightPointW[0], lightPointW[1], lightPointW[2]) 
            setPointLightCoordinate(grid, wgl, lightPoint)
        }
    }
    
    /**
     * calculate light point window coordinate
     */
    fun calcLightPointWindowCoordinte(
        grid: Grid, 
        wgl: WebGLRenderingContext,
        xw: Int, yw: Int): FloatArray? {
        var result: FloatArray? = null
        val pos = calcNewLightPointLocation(
            grid.glrs!!, xw.toFloat(), yw.toFloat())

        grid.beginForLightEditDepthFrame(wgl)
        val buffer = Uint8Array(4)
        wgl.readPixels(
            pos[0].roundToInt(), 
            pos[1].roundToInt(), 1, 1,
            WebGLRenderingContext.RGBA, 
            WebGLRenderingContext.UNSIGNED_BYTE, 
            buffer)
        val zw = gl.ColorCodec.decodeFloat(buffer) 
        grid.endForLightEditDepthFrame(wgl)
        if (zw != null) {
            result = floatArrayOf(xw.toFloat(), yw.toFloat(), zw)
        }

        return result
    }
    

    /**
     * set point light coord
     */
    fun setPointLightCoordinate(
        grid: Grid, 
        wgl: WebGLRenderingContext,
        lightPoint: FloatArray) {
        var doUpdate = false 
        for (idx in 0..pointLight!!.point.size) {
            if (pointLight!!.point[idx] != lightPoint[idx]) {
                pointLight!!.point[idx] = lightPoint[idx] 
                doUpdate = true     
            } 
        }
        if (doUpdate) {
            grid.postDrawScene(wgl)
        } 
    }
    
    
    /**
     * find out bound lines
     */
    fun calcEachDistancesFromBounds(
        glrs: glrs.InitOutput,
        xw: Float, yw: Float): Array<Double?>? {

        val lightEditViewTable = this.lightEditViewTable
 
        var result = Array<Double?>(lightEditViewTable!!.size) { 
            if (lightEditViewTable != null) {
                val pt = Float64Array(arrayOf(xw.toDouble(), yw.toDouble())) 
                val p1 = lightEditViewTable[it] 
                val p2 = lightEditViewTable[
                    (it + 1) % lightEditViewTable.size]
               
                val planeRef = glrs.plane_create_with_2d(
                    Float64Array(Array<Double>(p1.length) 
                        { p1[it].toDouble() }), 
                    Float64Array(Array<Double>(p2.length)
                        { p2[it].toDouble() }))
                var res: Double? = null
                if (planeRef != null) {
                    res = glrs.plane_distance(
                        planeRef, pt)!!.toDouble()
                }
                if (planeRef != null) {
                    glrs.plane_release(planeRef)
                }
                res
            } else {
                null
            } 
        }
        return result
    }


    /**
     * calculate light position from a point which is not in light editer table
     * bounds.
     */
    fun calcNewLightPointLocation(
        glrs: glrs.InitOutput,
        xw: Float, yw: Float): FloatArray {

        val distances = calcEachDistancesFromBounds(glrs, xw, yw)
        var indices = ArrayList<Pair<Int, Double>>()
        distances!!.forEachIndexed({ idx, dis -> 
            if (dis != null && dis < 0) {
                indices.add(Pair(idx, dis)) 
            }
        })
        var result: FloatArray? = null
        if (indices.size == 2) {
            var pt: Float32Array? = null
            if ((indices[0].first + 1) % lightEditViewTable!!.size
                    == indices[1].first) {
                pt = lightEditViewTable!![indices[1].first]
                
            } else {
                pt = lightEditViewTable!![indices[0].first]
            }
            result = floatArrayOf(pt[0], pt[1])
        } else if (indices.size == 1) {
            val seg1 = glrs.segment_create_11(
                Float64Array(
                    Array<Double>(2) {
                        lightEditViewTable!![indices[0].first][it].toDouble()
                    }),
                 Float64Array(
                    Array<Double>(2) {
                        lightEditViewTable!![(indices[0].first + 1)
                        % lightEditViewTable!!.size][it].toDouble()
                    }))
            val curLight = calcEditorMarkerWindowCoordinate(glrs)!! 
            
            
            val seg2 = glrs.segment_create_11(
                Float64Array(arrayOf(xw.toDouble(),
                    yw.toDouble())),
                Float64Array(arrayOf(curLight[0].toDouble(),
                    curLight[1].toDouble())))

            val params = glrs.segment_cross_point_parameter_2d_exact_11(
                seg1, seg2)
            if (params != null) {
                val pt = glrs.segment_point_on_t(seg1, params[0])!!
                result = floatArrayOf(pt[0].toFloat(), pt[1].toFloat())
            } else {
                result = curLight
            }

            glrs.segment_release(seg2)
            glrs.segment_release(seg1)
        } else {
            result = floatArrayOf(xw, yw) 
        }
        return result!!
    }          

    
    /**
     * calculate editor maker window coordinate
     */
    fun calcEditorMarkerWindowCoordinate(
        glrs: glrs.InitOutput): FloatArray? {
        val lightOnPlane = calcLightPointOnPlane(glrs)
        
        val lightOnPlaneWinCoord = gl.Matrix.project(
            glrs, 
            lightOnPlane[0], lightOnPlane[1], lightOnPlane[2],
            modelMatrix,
            projectionMatrix!!,
            viewport!!)
        var result: FloatArray? = null
        if (lightOnPlaneWinCoord != null) {
            result = floatArrayOf(
                lightOnPlaneWinCoord[0],
                lightOnPlaneWinCoord[1],
                lightOnPlaneWinCoord[2])
        }
        return result;
    }
    /**
     * calculate point light coordinate from window coordinate
     */
    fun calcPointLightFromEditorMarker(
        glrs: glrs.InitOutput,
        xw: Float, yw: Float, zw: Float): FloatArray {
        return calcPointLightFromOnPlane(
            glrs,
            calcPointLightOnPlaneFromEditorMarker(
                glrs, xw, yw, zw)!!)
    }
    /**
     * calculate point light coordinate on plane from window coordinate
     */
    fun calcPointLightOnPlaneFromEditorMarker(
        glrs: glrs.InitOutput,
        xw: Float, yw: Float, zw: Float): FloatArray? {
        val lightOnPlaneWinCoord = gl.Matrix.unproject(
            glrs, 
            xw, yw, zw,
            modelMatrix,
            projectionMatrix!!,
            viewport!!)
        var result: FloatArray? = null
        if (lightOnPlaneWinCoord != null) {
            result = floatArrayOf(
                lightOnPlaneWinCoord[0],
                lightOnPlaneWinCoord[1],
                lightOnPlaneWinCoord[2])
        }
        return result;
    }

    /**
     * calculate point light coordinate on plane from window coordinate
     */
    fun calcPointLightFromOnPlane(
        glrs: glrs.InitOutput,
        pointOnPlane: FloatArray): FloatArray {

        val distance = calcLightPointDistanceFromEditorPlane(glrs)
         
        return FloatArray(pointOnPlane.size) {
            normalVector!![it] * distance + pointOnPlane[it]
        }
    }

    /**
     * calculate distance between point light and editor plane
     */
    fun calcLightPointDistanceFromEditorPlane(
        glrs: glrs.InitOutput): Float {
    
        val plane = glrs.plane_create(
            Float64Array(Array<Double>(normalVector!!.size) {
                normalVector!![it].toDouble()
            }),
            Float64Array(Array<Double>(pointOnPlane!!.size) {
                pointOnPlane!![it].toDouble()
            }))

        val distance = glrs.plane_distance(plane,  
            Float64Array(Array<Double>(pointLight!!.point.size) {
                pointLight!!.point[it].toDouble()
            }))
        
        glrs.plane_release(plane)
        return distance!!.toFloat()
    }


    /**
     * calculate projected light point on editor plane
     */
    fun calcLightPointOnPlane(
        glrs: glrs.InitOutput): FloatArray {
        return projectOnPlane(glrs,
            normalVector!!, 
            pointOnPlane!!,
            pointLight!!.point)!!
    }
    /**
     * set up frame buffer for depth 
     */
    fun setupFrameBufferForDepth(
        gl: WebGLRenderingContext,
        renderingCtx : RenderingCtx) {

        renderingCtx.pointLightEditDepthFramebuffer = gl.createFramebuffer()
        
        val savedFramebuffer = gl.getParameter(
            WebGLRenderingContext.FRAMEBUFFER_BINDING) as
                WebGLFramebuffer?
  
        
        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
            renderingCtx.pointLightEditDepthFramebuffer) 
        renderingCtx.pointLightEditDepthBufferRead =
            createDepthBufferRead(gl)
        renderingCtx.pointLightEditDepthBuffer =
            createDepthBufferForDepthRendering(gl, renderingCtx)
        gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER,
            savedFramebuffer) 
 
    }

    /**
     * light editing vertex buffer
     */
    fun createLightEditTableBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,
                result)
            val dataSource = lightEditingTableArray!!
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                dataSource, 
                WebGLRenderingContext.STATIC_DRAW) 
        }
        return result
    }
 
    /**
     * create depth buffer as rendering buffer
     */
    fun createDepthBufferRead(
        gl: WebGLRenderingContext): WebGLRenderbuffer? {

        val result = gl.createRenderbuffer()
        if (result != null) {
            val savedBuffer = gl.getParameter(
                WebGLRenderingContext.RENDERBUFFER_BINDING) 
                    as WebGLRenderbuffer?
            gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
                result)
            gl.renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
                WebGLRenderingContext.RGBA4,
                gl.canvas.width, gl.canvas.height)

            gl.framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
                WebGLRenderingContext.COLOR_ATTACHMENT0, 
                WebGLRenderingContext.RENDERBUFFER, result)
 
            gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
                savedBuffer) 
        }  
        return result
    }

    /**
     * crate depth buffer for depth rendering
     */
    fun createDepthBufferForDepthRendering(
        gl: WebGLRenderingContext,
        renderingCtx: RenderingCtx): WebGLRenderbuffer? {
        val result = gl.createRenderbuffer()
        if (result != null) {
            gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER,
                result) 
            gl.renderbufferStorage(WebGLRenderingContext.RENDERBUFFER,
                WebGLRenderingContext.DEPTH_COMPONENT16,
                gl.canvas.width, gl.canvas.height);

            gl.framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER,
                WebGLRenderingContext.DEPTH_ATTACHMENT, 
                WebGLRenderingContext.RENDERBUFFER, result)
        }
        return result
    }


    /**
     * open gl buffer
     */
    fun setupBuffer(
        gl: WebGLRenderingContext,
        renderingCtx : RenderingCtx) {
        renderingCtx.pointLightMarkerBuffer = createBuffer(gl)
        renderingCtx.lightingTableBuffer = createLightEditTableBuffer(gl)
        setupFrameBufferForDepth(gl, renderingCtx)
    }

    
    /**
     * create buffer for open gl
     */
    fun createBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,
                result) 
            gl.bufferData(
                WebGLRenderingContext.ARRAY_BUFFER,
                Float32Array(3), 
                WebGLRenderingContext.DYNAMIC_DRAW)
        }
        return result
    }


    /**
     * attach model matrix
     */
    fun attachModelMatrix(
        gl: WebGLRenderingContext,
        renderingCtx : RenderingCtx) {
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {
            val modelMatrixLoc = gl.getUniformLocation(shaderProg,
                "uModelViewMatrix")
            gl.uniformMatrix4fv(modelMatrixLoc, false,
                modelMatrix)
        }
     }

    /**
     * draw point light edit table to store depth value
     */
    fun drawForDepthBuffer(
        gl: WebGLRenderingContext,
        renderingCtx : RenderingCtx) {
        val shaderProg = renderingCtx.pointLightDepthShaderProgram
        if (shaderProg != null) {
            val savedProgram = gl.getParameter(
                WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?

            gl.useProgram(shaderProg)
            val verLoc = gl.getAttribLocation(shaderProg, 
                "aVertexPosition")
 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, 
                renderingCtx.lightingTableBuffer)
            gl.vertexAttribPointer(
                verLoc,
                3,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            gl.enableVertexAttribArray(verLoc)
            gl.drawArrays(
                WebGLRenderingContext.TRIANGLES, 
                0, 
                lightEditingTableArray!!.length / 3) 
                
            gl.useProgram(savedProgram)
        }
    }




    /**
     * draw light point
     */
    fun drawScene(
        gl: WebGLRenderingContext,
        renderingCtx : RenderingCtx,
        glrs: glrs.InitOutput,
        glyph: Glyph,
        textures: Textures) {

        val lightOnPlane = calcLightPointOnPlane(glrs)
 
        val shaderProg = gl.getParameter(
            WebGLRenderingContext.CURRENT_PROGRAM) as WebGLProgram?
        if (shaderProg != null) {

            val savedTex = gl.getParameter(
                WebGLRenderingContext.TEXTURE_BINDING_2D) as WebGLTexture?

            val savedTexNum = gl.getParameter(
                WebGLRenderingContext.ACTIVE_TEXTURE) as Int
            val savedBuffer = gl.getParameter(
                WebGLRenderingContext.ARRAY_BUFFER_BINDING) as WebGLBuffer?

            val verLoc = gl.getAttribLocation(shaderProg,
                "aVertexPosition")
            val ptSizeLoc = gl.getUniformLocation(shaderProg,
                "uPointSize")
            val texSampler = gl.getUniformLocation(shaderProg,
                "uSampler")

            gl.uniform1f(ptSizeLoc, glyph.lightMarkerPointSize.toFloat())

            var txtNumber = Textures.LightMarkerTextureIndex
            gl.activeTexture(txtNumber)
            txtNumber -= WebGLRenderingContext.TEXTURE0

            gl.uniform1i(texSampler, txtNumber)
            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                textures.pointLightMarkerTexture)

            gl.enableVertexAttribArray(verLoc)
            gl.bindBuffer(
                WebGLRenderingContext.ARRAY_BUFFER,
                renderingCtx.pointLightMarkerBuffer) 
            gl.bufferSubData(
                WebGLRenderingContext.ARRAY_BUFFER,
                0,
                Float32Array(
                    Array<Float>(lightOnPlane.size) {
                        lightOnPlane[it]
                    }))
            gl.vertexAttribPointer(verLoc, 3,
                WebGLRenderingContext.FLOAT, false, 0, 0)

            
            gl.drawArrays(WebGLRenderingContext.POINTS, 0, 1)
            
            gl.bindBuffer(
                WebGLRenderingContext.ARRAY_BUFFER,
                savedBuffer)
 
            gl.bindTexture(WebGLRenderingContext.TEXTURE_2D,
                savedTex)
            gl.activeTexture(savedTexNum)
        } 
    }

}
// vi: se ts=4 sw=4 et:
