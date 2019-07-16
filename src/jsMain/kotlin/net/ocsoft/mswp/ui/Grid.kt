package net.ocsoft.mswp.ui

import org.w3c.dom.*
import org.khronos.webgl.*
import jQuery
import net.ocsoft.mswp.*
import kotlin.browser.*

typealias GLRctx = WebGLRenderingContext

/**
 * game play ground grid
 */
class Grid {
    var model : Model? = null

    var camera : Camera? = null
        set(value) {
            if (field != value) {
                if (field != null) {
                    detachCameraListener()
                }
                field = value
                if (field != null) {
                    attachCameraListener()
                }
            }
        }
    /**
     * shader program for squarr
     */
    var shaderProgram: WebGLProgram? = null
    /**
     * vertex buffer for square
     */
    var squareBuffer : WebGLBuffer? = null
    /**
     * color buffer for square
     */
    var squareColorBuffer : WebGLBuffer? = null

    /**
     * vertex indices for cube
     */
    var boxIndexBuffer : WebGLBuffer? = null

    var matrixRotation : Float = 0f
    
    /**
     * initialize gl context
     */
    fun setupEnv(gl :WebGLRenderingContext) {
        gl.clearColor(0.0f, 0.0f, 0.0f, 1.0f)
        gl.clear(WebGLRenderingContext.COLOR_BUFFER_BIT)
        gl.enable(GLRctx.DEPTH_TEST)
        gl.depthFunc(GLRctx.LEQUAL)
        gl.clear(GLRctx.COLOR_BUFFER_BIT or GLRctx.DEPTH_BUFFER_BIT)
    }
    /**
     * draw scene
     */
    fun drawScene(gl: WebGLRenderingContext) {
       setupEnv(gl)
       updateView(gl)
    }
    fun setup(gl: WebGLRenderingContext) {
        setupShaderProgram(gl)
        setupBuffer(gl)
        setupCamera(gl)
    }
    fun teardown(gl: WebGLRenderingContext) {
        teardownBuffer(gl)
        teardownShaderProgram(gl) 
    }
    /**
     * set up camera
     */
    fun setupCamera(gl: WebGLRenderingContext) {
        val cam = this.camera
        if (cam != null) {
            detachCameraListener()
            var aspect = gl.canvas.clientWidth.toFloat()
            aspect /= gl.canvas.clientHeight
            cam.aspect = aspect
            attachCameraListener() 
        } 
    }
    fun setupShaderProgram(gl: WebGLRenderingContext) {
        val vertexShader : WebGLShader? = createVertexShader(gl)
        var fragmentShader : WebGLShader? = createFragmentShader(gl)
        if (this.shaderProgram != null) {
            gl.deleteProgram(this.shaderProgram)
            this.shaderProgram = null
        }
        if (vertexShader != null 
            && fragmentShader != null) { 
            val shaderProg = gl.createProgram()  
            if (shaderProg != null) {
                gl.attachShader(shaderProg, vertexShader)
                gl.attachShader(shaderProg, fragmentShader)
                gl.linkProgram(shaderProg)
                this.shaderProgram = assertLinkError(gl, shaderProg)
            }
        }
    }
    fun teardownShaderProgram(gl: WebGLRenderingContext) {
        val shaderProgram = this.shaderProgram
        if (shaderProgram != null) {
            val shaders = gl.getAttachedShaders(shaderProgram)
            if (shaders != null) {
                shaders.forEach({
                    shader->
                    gl.detachShader(shaderProgram, shader)
                    gl.deleteShader(shader)
                })
            }
            gl.deleteProgram(shaderProgram)
            this.shaderProgram = null
        } 

    }
    /**
     * connect a canvas and create grid
     */
    fun bind(nodeQuery: String, 
        model: Model,
        camera: Camera) {
        this.model = model
        this.camera = camera
        jQuery({
            val canvasNode = jQuery(nodeQuery)
            val canvas = canvasNode[0] as HTMLCanvasElement
            var gl = canvas.getContext("webgl") as WebGLRenderingContext
            setup(gl)
            drawScene(gl)
            startAnimation(gl)
        }) 
    }
    private fun startAnimation(gl: WebGLRenderingContext) {
        var then = .0
        fun render(now : Double): Unit {
            val now1 = now * 0.001
            var deltaTime = now1 - then
            matrixRotation += deltaTime.toFloat()
            then = now1
            drawScene(gl)
            window.requestAnimationFrame { render(it) } 
        } 
        window.requestAnimationFrame { render(it) }
    }
        
    private fun setupBuffer(gl: WebGLRenderingContext) {
       squareBuffer = createSimpleRectBuffer(gl)
       squareColorBuffer = createSimpleColorBuffer(gl)
       boxIndexBuffer = createSimpleIndicesBuffer(gl)
    }
    private fun teardownBuffer(gl: WebGLRenderingContext) {
        arrayOf(squareBuffer, 
            squareColorBuffer, 
            boxIndexBuffer).forEach({ buffer ->
            if (buffer != null) {
                gl.deleteBuffer(buffer)
            }
        })
     
    }
    private fun createSimpleRectBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,
                result)
            val pos : Array<Float> = arrayOf(
                -1.0f, -1.0f, 1f,
                1.0f, -1.0f, 1f,
                1.0f, 1.0f, 1f,
                -1.0f, 1.0f, 1f,

                -1.0f, -1.0f, -1f,
                -1.0f, 1.0f, -1f,
                1.0f, 1.0f, -1f,
                1.0f, -1.0f, -1f,

        -1f, 1f, -1f,
        -1f, 1f, 1f,
        1f, 1f, 1f,
        1f, 1f, -1f,

        // Bottom face
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        1.0f, -1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,
  
        // Right face
        1.0f, -1.0f, -1.0f,
        1.0f,  1.0f, -1.0f,
        1.0f,  1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,
  
        // Left face
        -1.0f, -1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
        -1.0f,  1.0f, -1.0f)
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                Float32Array(pos), WebGLRenderingContext.STATIC_DRAW) 
        }
        return result
    }
    private fun createSimpleIndicesBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val indices: Array<Short> = arrayOf<Short>(    
            0,  1,  2,      0,  2,  3,    // front
            4,  5,  6,      4,  6,  7,    // back
            8,  9,  10,     8,  10, 11,   // top
            12, 13, 14,     12, 14, 15,   // bottom
            16, 17, 18,     16, 18, 19,   // right
            20, 21, 22,     20, 22, 23   // left
        )
        val result = gl.createBuffer()
        gl.bindBuffer(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER, result)

        gl.bufferData(WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
            Uint16Array(indices),
            WebGLRenderingContext.STATIC_DRAW)
        return result
    }
    private fun createSimpleColorBuffer(
        gl: WebGLRenderingContext): WebGLBuffer? {
        val result = gl.createBuffer()
        if (result != null) {
            val colorElem = arrayOf<Array<Float>>(
                arrayOf(1.0f, 1.0f, 1.0f, 1.0f),
                arrayOf(1.0f, 0.0f, 0.0f, 1.0f),
                arrayOf(0.0f, 1.0f, 0.0f, 1.0f), 
                arrayOf(0.0f, 0.0f, 1.0f, 1.0f),
                arrayOf(1.0f, 1.0f, 0.0f, 1.0f),
                arrayOf(1.0f, 0.0f, 1.0f, 1.0f))
            val colors = Array(colorElem.size 
                * colorElem[0].size * 4) {
                i ->
                colorElem[i / (colorElem[0].size * 4)][i % colorElem[0].size]
            }
                 
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, result)
            gl.bufferData(WebGLRenderingContext.ARRAY_BUFFER,
                Float32Array(colors),
                WebGLRenderingContext.STATIC_DRAW)
        }
        return result
    }
    private fun updateView(gl: WebGLRenderingContext) {
        val shaderProg = this.shaderProgram

        if (shaderProg != null) {
            val verLoc = gl.getAttribLocation(shaderProg, 
                "aVertexPosition")
            val verColor = gl.getAttribLocation(shaderProg,
                "aVertexColor")
           
            val recBuff = squareBuffer
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER, recBuff)

            gl.vertexAttribPointer(
                verLoc,
                3,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            
            gl.enableVertexAttribArray(verLoc)
            gl.bindBuffer(WebGLRenderingContext.ARRAY_BUFFER,  
                squareColorBuffer)
            gl.vertexAttribPointer( 
                verColor,
                4,
                WebGLRenderingContext.FLOAT,
                false,
                0, 0)
            gl.enableVertexAttribArray(verColor)


            gl.bindBuffer(
                WebGLRenderingContext.ELEMENT_ARRAY_BUFFER,
                boxIndexBuffer)

            gl.useProgram(shaderProg)

            updateViewMatrix(gl)
            gl.drawElements(WebGLRenderingContext.TRIANGLES, 36, 
                WebGLRenderingContext.UNSIGNED_SHORT, 0) 
        }         
    }
    private fun updateViewMatrix(gl: WebGLRenderingContext) {
        val cam = this.camera
        val shaderProg = this.shaderProgram

        if (cam != null && shaderProg != null) {
            val projMat = mat4.create()
            val uProjMat = gl.getUniformLocation(shaderProg, 
                "uProjectionMatrix")    
            val uModelMat = gl.getUniformLocation(shaderProg,
                "uModelViewMatrix")
            mat4.perspective(projMat, cam.fieldOfView,
                cam.aspect, cam.zNear, cam.zFar)
            val modelMat = mat4.create()

            mat4.translate(modelMat, modelMat,
                vec3.fromValues(-0.0, 0.0, -6.0))
 
            mat4.rotate(modelMat, modelMat, matrixRotation,
                vec3.fromValues(0, 0, 1)) 

            mat4.rotate(modelMat, modelMat, matrixRotation * .7f,
                vec3.fromValues(0, 1, 0))  

            gl.uniformMatrix4fv(uProjMat, false,
                projMat as Float32Array)
                    
            gl.uniformMatrix4fv(uModelMat, false, 
                modelMat as Float32Array)
 
        }
    }
    private fun createVertexShader(gl: WebGLRenderingContext): WebGLShader? {
        val prog = """
    attribute vec4 aVertexPosition;
    attribute vec4 aVertexColor;
    uniform mat4 uModelViewMatrix;
    uniform mat4 uProjectionMatrix;

    varying lowp vec4 vColor;
    void main() {
        gl_Position = uProjectionMatrix * uModelViewMatrix * aVertexPosition;
        vColor = aVertexColor;
    }
"""
        var result : WebGLShader? = null
        val shader = gl.createShader(WebGLRenderingContext.VERTEX_SHADER)
        if (shader != null) {
            gl.shaderSource(shader, prog)
            gl.compileShader(shader)
            result = assertCompileError(gl, shader)
        }
        return result
    } 
    private fun createFragmentShader(gl: WebGLRenderingContext): WebGLShader? {
        val prog = """
    varying lowp vec4 vColor;
    void main() {
        gl_FragColor = vColor;
    }
"""
        var result : WebGLShader? = null
        val shader = gl.createShader(WebGLRenderingContext.FRAGMENT_SHADER)
        if (shader != null) {
            gl.shaderSource(shader, prog)
            gl.compileShader(shader)
            result = assertCompileError(gl, shader)
        }
        return result
    } 
    private fun assertCompileError(
        gl: WebGLRenderingContext,
        shader: WebGLShader): WebGLShader? {
        var result : WebGLShader? = null 
        if (gl.getShaderParameter(shader,
            WebGLRenderingContext.COMPILE_STATUS)!! as Boolean) {
            result = shader 
        }
        return result
    }
    private fun assertLinkError(
        gl: WebGLRenderingContext,
        prog: WebGLProgram): WebGLProgram? {
        var result: WebGLProgram? = null
        if (gl.getProgramParameter(prog,
            WebGLRenderingContext.LINK_STATUS)!! as Boolean) {
            result = prog
        }
        return result 
    }  

    private fun attachCameraListener() {
        camera?.on(null, this::onCameraChanged)
    }
    private fun detachCameraListener() {
        camera?.off(null, this::onCameraChanged)
    }
    private fun onCameraChanged(eventName : String, camera : Camera) {
    }
}
