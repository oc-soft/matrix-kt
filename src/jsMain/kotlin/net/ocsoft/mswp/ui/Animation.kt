package net.ocsoft.mswp.ui

import kotlin.math.min
import net.ocsoft.mswp.ui.grid.Buttons
import net.ocsoft.mswp.Matrix
import net.ocsoft.mswp.Status
/**
 * managing animation
 */
class Animation {

    companion object {
        /**
         * create matrices for each frame
         */
        fun setupButtons(buttons : Buttons,
            openedButtonIndices: Array<IntArray>,
            buttonIndices : Array<IntArray>,
            renderingCtx : RenderingCtx) {
            val buttonMatrices = renderingCtx.cloneButtonMatrices()    
            val spinAndVMotionMtx = renderingCtx.spinAndVMotionMatrices
            val spinMotionMtx = renderingCtx.spinMotionMatrices
            val buttonNormalVecMatrices = renderingCtx.buttonNormalVecMatrices
            if (buttonMatrices != null
                && buttonNormalVecMatrices != null
                && spinAndVMotionMtx != null
                && spinMotionMtx != null) {
                val countOfFrames = min(spinAndVMotionMtx.size,
                    spinMotionMtx.size)

                var animationMatrices = Array<Array<FloatArray>>(
                    countOfFrames) {
                    i ->
                    val aniMtx = spinAndVMotionMtx[i]
                    val lastAniMtx = spinMotionMtx[spinMotionMtx.size - 1]
                    val frameMat = Array<FloatArray>(
                        buttons.rowCount * buttons.columnCount) {
                        j -> buttonMatrices[j]          
                    }
                    openedButtonIndices.forEach({
                        rowCol ->
                        var locMatIdx = rowCol[0] * buttons.columnCount
                        locMatIdx += rowCol[1]
                        val locMat = frameMat[locMatIdx]
                        val newLocMat = Matrix.multiply(lastAniMtx, locMat)
                        frameMat[locMatIdx] = newLocMat!!
                    }) 
                    buttonIndices.forEach({
                        rowCol ->
                        var locMatIdx = rowCol[0] * buttons.columnCount
                        locMatIdx += rowCol[1]
                        val locMat = frameMat[locMatIdx]
                        val newLocMat = Matrix.multiply(aniMtx, locMat)
                        frameMat[locMatIdx] = newLocMat!!
                    })
                    frameMat 
                     
                }
                val normalVecAnimMatrices = Array<Array<FloatArray>>(
                    countOfFrames) {
                    midx ->
                    val normalVecAniMtx = spinMotionMtx[midx]
                    val frameMat = Array<FloatArray>(
                        buttons.rowCount * buttons.columnCount) {
                        j ->           
                        buttonNormalVecMatrices[j]
                    }
                    buttonIndices.forEach({
                        rowCol ->
                        var locMatIdx = rowCol[0] * buttons.columnCount
                        locMatIdx += rowCol[1]
                        val aMat = frameMat[locMatIdx]
                        val newMat = Matrix.multiply(normalVecAniMtx, aMat)
                        frameMat[locMatIdx] = newMat!!
                    })
                    frameMat 
                }
                renderingCtx.animationMatrices = 
                    animationMatrices.toMutableList()
                renderingCtx.animationNormalMatrices =
                    normalVecAnimMatrices.toMutableList()
             }
        }
        /**
         * start animation if rendering context has animation matrices
         */
        fun doAnimate(renderingCtx : RenderingCtx) {
            if (Animation.hasNextFrame(renderingCtx)) {
                val animationMatrices = renderingCtx.animationMatrices
                val normalVecAniMtx = renderingCtx.animationNormalMatrices 
                if (animationMatrices != null
                    && normalVecAniMtx != null) {
                    renderingCtx.buttonMatricesForDrawing = 
                        animationMatrices[0]
                    renderingCtx.buttonNormalVecMatricesForDrawing =
                        normalVecAniMtx[0] 
                    animationMatrices.removeAt(0)
                    normalVecAniMtx.removeAt(0)
                    if (animationMatrices.size == 0) {
                        renderingCtx.animationMatrices = null
                    }
                    if (normalVecAniMtx.size == 0) {
                        renderingCtx.animationNormalMatrices = null
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

