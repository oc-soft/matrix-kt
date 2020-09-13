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
     * the progress of opening buttons
     */
    var openingButtons: Set<CellIndex>? = null

    /**
     * locking buttons 
     */
    val lockingButtons: MutableSet<CellIndex> = HashSet<CellIndex>()

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
        return CellIndex(row, column) in openedButtons
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
     * get openend cells
     */
    fun getOpenedCellsRef(): Set<CellIndex>  {
        return openedButtons
    }
    /**
     * clear opened buttons
     */
    fun clearOpenedButtons() {
        openedButtons.clear()
    }

    /**
     * lock cell
     */
    fun lockCell(row: Int, column: Int) {
        lockingButtons.add(CellIndex(row, column))
    }

    /**
     * unlock cell
     */
    fun unlockCell(row: Int, column: Int) {
        lockingButtons.remove(CellIndex(row, column))
    }

    /**
     * isLocking
     */
    fun isLocking(row: Int, column: Int): Boolean {
        return CellIndex(row, column) in lockingButtons
    }
}
// vi: se ts=4 sw=4 et:
