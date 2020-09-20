package net.ocsoft.mswp

import kotlin.collections.MutableSet
import kotlin.collections.HashSet


/**
 * game status
 */
class Status {

    /**
     * class instance
     */
    companion object {
        /**
         * locked count
         */
        val LOCKING_COUNT = "lockingcount"
    }

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
     * count of locking to prevent from opening button
     */
    val lockingCount: Int
        get() {
           return lockingButtons.size
        }

    /**
     * whether the game is started or not
     */
    val isStarted : Boolean
        get() {
            return openedButtons.size != 0
        }
    /**
     * event listeners
     */
    private val listeners: MutableList<(Any?, String)->Unit>
        = ArrayList<(Any?, String)->Unit>()


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
     * clear all attirbute from buttons
     */
    fun clearAll() {
        clearOpenedButtons()
        clearLockingButtons()
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
    fun lockCell(row: Int, column: Int): Boolean {
    
        val result = lockingButtons.add(CellIndex(row, column))

        if (result) {
            notifyChange(LOCKING_COUNT)
        }
        return result
    }

    /**
     * unlock cell
     */
    fun unlockCell(row: Int, column: Int): Boolean {
        val result = lockingButtons.remove(CellIndex(row, column))
        if (result) {
            notifyChange(LOCKING_COUNT)
        }
        return result
    }

    /**
     * isLocking
     */
    fun isLocking(row: Int, column: Int): Boolean {
        return CellIndex(row, column) in lockingButtons
    }

    /**
     * clear all locking cell
     */
    fun clearLockingButtons() {
        val oldSize = lockingButtons.size
        lockingButtons.clear()
        if (oldSize > 0) {
            notifyChange(LOCKING_COUNT) 
        }
    }

    /**
     * add listener
     */ 
    fun addListener(listener: (Any?, String)->Unit) {
        listeners.add(listener) 
    }
    /**
     * remove listener
     */
    fun removeListener(listener: (Any?, String)->Unit) {
        val idx = listeners.indexOfLast { it == listener }
        if (idx >= 0) {
            listeners.removeAt(idx)
        }
    }


    /**
     * notify change
     */
    private fun notifyChange(name: String) {
        val listeners = ArrayList(this.listeners)
        listeners.forEach { it(this, name) }
    }
}
// vi: se ts=4 sw=4 et:
