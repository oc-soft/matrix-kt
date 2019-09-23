package net.ocsoft.mswp.ui

import kotlin.js.Json
import kotlin.browser.window
import kotlin.collections.ArrayList

/**
 * manage responsive interface
 */
class Responsive {

    /**
     * singleton object
     */
    companion object {
        /**
         * find row count from setting
         */
        fun findRowCount(
            setting: dynamic,
            minKeyword: String,
            maxKeyword: String,
            countKeyword: String = "count"): Int? {
            return findCount(setting,
                window.innerHeight,
                minKeyword, maxKeyword, countKeyword) 
        }
        /**
         * find column count from setting
         */
        fun findColumnCount(
            setting: dynamic,
            minKeyword: String,
            maxKeyword: String,
            countKeyword: String = "count"): Int? {
            return findCount(setting,
                window.innerWidth,
                minKeyword, maxKeyword, countKeyword) 
        }
     
        /**
         * find count which is match the device display
         */ 
        fun findCount(
            setting: dynamic,
            windowSize: Int,
            minKeyword: String,
            maxKeyword: String,
            countKeyword: String = "count"): Int? {
            var result : Int? = null
            result = findCountInRange(setting, windowSize, 
                minKeyword, maxKeyword, countKeyword)
            if (result == null) {
                result = findCountInMin(setting, windowSize,
                    minKeyword, maxKeyword, countKeyword)
            }
            if (result == null) {
                result = findCountInMax(setting, windowSize,
                    minKeyword, maxKeyword, countKeyword)
            }
            return result 
        }

        /**
         * find column count which is match the device display
         */ 
        fun findCountInRange(
            setting: dynamic,
            windowSize: Int,
            minKeyword: String,
            maxKeyword: String,
            countKeyword: String = "count"): Int? {
            var result : Int? = null
            val rangeSettings = ArrayList<dynamic>()
            for (i in 0..setting.length - 1) {
                val elem  = setting[i] 
                if (elem[minKeyword] != null
                    && elem[maxKeyword] != null) {
                    rangeSettings.add(elem) 
                }     
            }
            val item = rangeSettings.find({
                val minSize: Int = it[minKeyword] 
                val maxSize: Int = it[maxKeyword]
                minSize <= windowSize && windowSize <= maxSize
            }) 
            if (item != null) {
                result = item[countKeyword]
            }
            return result 
        }
         
        /**
         * find column count which is match the device display
         */ 
        fun findCountInMin(
            setting: dynamic,
            windowSize: Int,
            minKeyword: String,
            maxKeyword: String,
            countKeyword: String = "count"): Int? {
            var result : Int? = null
            val minSettings = ArrayList<dynamic>()
            for (i in 0..setting.length - 1) {
                val elem  = setting[i] 
                if (elem[minKeyword] != null
                    && elem[maxKeyword] == null) {
                    minSettings.add(elem) 
                }     
            }
            var diffMin = Int.MAX_VALUE
            minSettings.forEach({
                val diff = windowSize - it[minKeyword] as Int 
                if (diff > 0 && diff < diffMin) {
                    diffMin = diff
                    result = it[countKeyword]        
                } 
            }) 
            return result 
        }
        /**
         * find column count which is match the device display
         */ 
        fun findCountInMax(
            setting: dynamic,
            windowSize: Int,
            minKeyword: String,
            maxKeyword: String,
            countKeyword: String = "count"): Int? {
            var result : Int? = null
            val maxSettings = ArrayList<dynamic>()
            for (i in 0..setting.length - 1) {
                val elem  = setting[i] 
                if (elem[minKeyword] == null
                    && elem[maxKeyword] != null) {
                    maxSettings.add(elem) 
                }     
            }
            var diffMax = Int.MAX_VALUE 
            maxSettings.forEach({
                val diff = it[minKeyword] as Int - windowSize 
                if (diff > 0 && diff < diffMax) {
                    diffMax = diff
                    result = it[countKeyword]        
                } 
            }) 
            return result 
        }
    }
}
