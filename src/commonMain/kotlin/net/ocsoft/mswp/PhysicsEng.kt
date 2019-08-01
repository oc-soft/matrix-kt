package net.ocsoft.mswp

import kotlin.collections.*
import kotlin.math.*


/**
 * physics engine
 */
class PhysicsEng(var gravity: Float = 9.8f,
    val fps : Float = 30f){

    companion object { 
        
        /**
         * create matrices vertical motion.
         * I use z axis as Vertical axis.
         * v is inital speed.
         * g is gravity to the plane
         * object is located at 0 which is very hard plane. object can not go
         * down the plane.
         */
        fun createVerticalMotion(v: Float, 
            g: Float, 
            fps: Float = 30f): Array<FloatArray>? {
            var result : Array<FloatArray>? = null
            if (v > 0 && g > 0 && fps > 0) {
                // time displacement
                val td = 1f / fps    
                var zLocs : MutableList<Float> = ArrayList<Float>()
                val sim = { t: Float -> v * t - 0.5f * g * t.pow(2) }
                var curTime = td
                var curLoc = sim(curTime)
                while (curLoc > 0) {
                    zLocs.add(curLoc)
                    curTime += td
                    curLoc = sim(curTime)
                } 
                result = Array<FloatArray>(zLocs.size) { i ->
                    floatArrayOf(
                        1f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f,
                        0f, 0f, 1f, 0f,
                        0f, 0f, zLocs[i], 1f
                    )
                }
            }
            return result
        } 
        /**
         * create matrices vertical motion.
         * I use z axis as Vertical axis.
         * v is inital speed.
         * g is gravity to the plane
         * object is located at 0 which is very hard plane. object can not go
         * down the plane.
         */
        fun createVerticalMotion(v: Float, 
            g: Float, 
            divisionCount: Int): Array<FloatArray>? {
            var result : Array<FloatArray>? = null
            if (v > 0 && g > 0 && divisionCount > 0) {
                // time displacement
                val duration = 2 * v / g
                val td = duration / divisionCount   
                val sim = { t: Float -> v * t - 0.5f * g * t.pow(2) }

                val zLocs = FloatArray(divisionCount) {
                    i ->
                    var zLoc = 0f
                    if (i < divisionCount - 1) {
                        val t1 = (i + 1) * td
                        val t2 = duration - (divisionCount - i - 1) * td
                        zLoc = sim((t1 + t2) / 2)
                    }
                    zLoc 
                }
                result = Array<FloatArray>(zLocs.size) { i ->
                    floatArrayOf(
                        1f, 0f, 0f, 0f,
                        0f, 1f, 0f, 0f,
                        0f, 0f, 1f, 0f,
                        0f, 0f, zLocs[i], 1f
                    )
                }
            }
            return result
        } 
        /**
         * calculate vertical motion duration
         */
        fun calcVerticalMotionDuration(v: Float, 
            g: Float) : Float {
            return (2 * v) / g
        }
        /**
         * calculate one rotaion spin around z axis
         */
        fun calcSpin(
            axis: FloatArray,
            rps: Float,
            rotationCount : Float = 1f,
            fps: Float = 30f) : Array<FloatArray>? {
            val axisN = Math.normalize3(axis)
            var result : Array<FloatArray>? = null
            if (rotationCount > 0 && axisN != null) {
                var rotations : MutableList<FloatArray>
                    = ArrayList<FloatArray>()
                // time displacement
                val totalRadians = PI * 2 * rotationCount
                val td = 1f / fps    
                val dispRadian = rps * td
                var curRadian = dispRadian 
                while (abs(curRadian) < totalRadians) {
                    rotations.add(Math.calcRotationMatrix3(axisN,
                        curRadian)!!) 
                    curRadian += dispRadian
                }
                result = Array<FloatArray>(rotations.size) {
                    i -> rotations[i] 
                }
            } 
            return result
        }
        /**
         * calculate one rotaion spin around z axis
         */
        fun calcSpin(
            axis: FloatArray,
            divisionCount: Int,
            rotationCount : Float = 1f) : Array<FloatArray>? {
            val axisN = Math.normalize3(axis)
            var result : Array<FloatArray>? = null
            if (axisN != null && divisionCount > 0) {
                var rotations : MutableList<FloatArray>
                    = ArrayList<FloatArray>()
                // time displacement
                val totalRadians = (PI * 2 * rotationCount).toFloat()
                // displacement of radian
                val dispRadian = totalRadians / divisionCount 
                result = Array<FloatArray>(divisionCount) {
                    i ->
                   var matrix : FloatArray?
                    if (i != divisionCount - 1) {
                        val rad1 = (i + 1) * dispRadian
                        var rad2 = totalRadians
                        rad2 -= (divisionCount - i - 1) * dispRadian
                        matrix = Math.calcRotationMatrix3(
                            axisN, (rad1 + rad2) / 2)!!
                    } else {
                        matrix = Math.calcRotationMatrix3(
                            axisN, totalRadians)
                    }
                    matrix!!
                }
            } 
            return result
        }
         
        /**
         * calculate matricies to move up-down and spin
         */ 
        fun calcSpinAndVerticalMotion(t: Float,
            g: Float,
            axis: FloatArray,
            rotationCount: Float,
            fps: Float = 30f): Array<FloatArray>? {
            val v = 0.5f * g * t
            val totalRadians = (PI * 2 * abs(rotationCount)).toFloat()
            val rps = totalRadians / t * sign(rotationCount)
            val vMotionMat = createVerticalMotion(v, g, fps)
            val sMotionMat = calcSpin(axis, rps, abs(rotationCount), fps)
            var result: Array<FloatArray>? = null
            if (vMotionMat != null && sMotionMat != null) {
                result = Array<FloatArray>(vMotionMat.size) {
                    i ->
                    Matrix.multiply(vMotionMat[i], sMotionMat[i])!!
                }        
            } 
            return result 
        } 
            
        /**
         * calculate matricies to move up-down and spin
         */ 
        fun calcSpinAndVerticalMotion(t: Float,
            g: Float,
            axis: FloatArray,
            rotationCount: Float,
            divisionCount: Int): Array<FloatArray>? {
            val v = 0.5f * g * t
            val totalRadians = (PI * 2 * abs(rotationCount)).toFloat()
            val vMotionMat = createVerticalMotion(v, g, divisionCount)
            val sMotionMat = calcSpin(axis, divisionCount, rotationCount)
            var result: Array<FloatArray>? = null
            if (vMotionMat != null && sMotionMat != null) {
                result = Array<FloatArray>(vMotionMat.size) {
                    i ->
                    Matrix.multiply(sMotionMat[i], vMotionMat[i])!!
                }        
            } 
            return result 
        } 
        /**
         * calculate matricies to move up-down and spin
         */ 
        fun calcSpinAndVerticalMotionTest(t: Float,
            g: Float,
            axis: FloatArray,
            rotationCount: Float,
            divisionCount: Int): Array<FloatArray>? {
            val v = 0.5f * g * t
            val totalRadians = (PI * 2 * abs(rotationCount)).toFloat()
            val vMotionMat = createVerticalMotion(v, g, divisionCount)
            val sMotionMat = calcSpin(axis, 
                divisionCount, rotationCount)
            var result: Array<FloatArray>? = null
            if (vMotionMat != null && sMotionMat != null) {
                result = Array<FloatArray>(vMotionMat.size) {
                    i -> sMotionMat[i]!!
                }        
            } 
            return result 
        } 
     }
    /**
     * create matrices vertical motion.
     * I use z axis as Vertical axis.
     * v is inital speed.
     * object is located at 0 which is very hard plane. object can not go
     */
     fun createVerticalMotion(v: Float) : Array<FloatArray>? {
        return PhysicsEng.createVerticalMotion(v, gravity, fps) 
     } 

     /**
      * calculate one rotaion spin around z axis
      */
     fun calcSpin(
        axis: FloatArray,
        rps: Float,
        rotationCount : Float = 1f) : Array<FloatArray>? {
        return PhysicsEng.calcSpin(axis, rps, rotationCount, fps)
    }
    /**
     * calculate matricies to move up-down and spin
     */ 
    fun calcSpinAndVerticalMotion1(t: Float,
        axis: FloatArray,
        rotationCount: Float): Array<FloatArray>? {
        val frameDuration = 1 / fps  
        var countOfFrames = t / frameDuration
        countOfFrames = ceil(countOfFrames)
         
        return PhysicsEng.calcSpinAndVerticalMotion(
            frameDuration * countOfFrames, gravity,
            axis, rotationCount, countOfFrames.toInt())
    } 
}
