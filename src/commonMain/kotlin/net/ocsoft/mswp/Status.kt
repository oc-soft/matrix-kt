package net.ocsoft.mswp

import kotlin.collections.MutableSet
import kotlin.collections.HashSet


/**
 * game status
 */
class Status {

    /**
     * the game is in animating status
     */
    var inAnimating : Boolean = false
    /**
     * opened buttons set
     */
    val openedButtons : MutableSet<CellIndex>  = HashSet<CellIndex>()

    /**
     * whether the game is started or not
     */
    val isStarted : Boolean
        get() {
            return openedButtons.size != 0
        }
    /**
     * register opened button
     */
    fun registerOpened(row: Int, column: Int) {
        openedButtons.add(CellIndex(row, column))
    }
    /**
     * you will have true if the button is opened.
     */
    fun isOpened(row: Int, column: Int): Boolean {
        return openedButtons.contains(CellIndex(row, column))
    }
    /**
     * get opened indecies
     */
    fun getOpenedIndices() : Array<IntArray> {
        val result = Array<IntArray>(openedButtons.size) { IntArray(2) }

        openedButtons.forEachIndexed({
            idx, elem ->
            result[idx][0] = elem.row
            result[idx][1] = elem.column
        }) 
        return result
    }
    /**
     * clear opened buttons
     */
    fun clearOpenedButtons() {
        openedButtons.clear()
    }
}
