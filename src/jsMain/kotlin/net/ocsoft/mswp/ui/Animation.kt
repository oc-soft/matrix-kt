package net.ocsoft.mswp.ui

import net.ocsoft.mswp.ui.grid.Buttons
import net.ocsoft.mswp.Matrix

/**
 * managing animation
 */
class Animation {

    companion object {
        /**
         * create matrices for each frame
         */
        fun setupButtons(buttons : Buttons,
            buttonIndices : Array<IntArray>,
            renderingCtx : RenderingCtx) {
            val buttonMatrices = renderingCtx.cloneButtonMatrices()    
            val spinAndVMotionMtx = renderingCtx.spinAndVMotionMatrices
            if (buttonMatrices != null && spinAndVMotionMtx != null) {
                var animationMatrices = Array<Array<FloatArray>>(
                    spinAndVMotionMtx.size) {
                    midx ->
                    val aniMtx = spinAndVMotionMtx[midx]
                    val frameMat = Array<FloatArray>(
                        buttons.rowCount * buttons.columnCount) {
                        j -> buttonMatrices[j]          
                    }
                    buttonIndices.forEach({
                        rowCol ->
                        var locMatIdx = rowCol[0] * buttons.columnCount
                        locMatIdx += rowCol[1]
                        val locMat = frameMat[locMatIdx]
                        val newLocMat = Matrix.multiply(locMat, aniMtx)
                        frameMat[locMatIdx] = newLocMat!!
                    })
                    frameMat 
                }
                renderingCtx.animationMatrices = 
                    animationMatrices.toMutableList()
            }
        }
        /**
         * start animation if rendering context has animation matrices
         */
        fun doAnimate(renderingCtx : RenderingCtx) {
            if (Animation.hasNextFrame(renderingCtx)) {
                val animationMatrices = renderingCtx.animationMatrices
                if (animationMatrices != null) {
                    renderingCtx.buttonMatricesForDrawing = 
                        animationMatrices[0]
                    animationMatrices.removeAt(0)    
                    if (animationMatrices.size == 0) {
                        renderingCtx.animationMatrices = null
                    }
                }
            }
        }
        /**
         * You get true if rendering context has next frame to animate.
         */
        fun hasNextFrame(renderingCtx: RenderingCtx): Boolean {
            var result = false
            val animationMatrices = renderingCtx.animationMatrices
            if (animationMatrices != null) {
                result = animationMatrices.size > 0
            }
            return result
        }
    }
}

