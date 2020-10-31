package net.ocsoft.mswp

import kotlin.collections.MutableSet
import kotlin.collections.HashSet
import kotlin.random.Random
import kotlin.math.*

/**
 * the game logic
 */
class Logic(rowSize: Int,
    columnSize: Int,
    hitCount: Int,
    lockableCount: Int) {
    /**
     * class instance
     */
    companion object {
        /**
         * locked count
         */
        val LOCKED_COUNT = "lockedcount"
    }


    /**
     * game status
     */ 
    var status: Status? = null
        set(value) {
            val doSet = value != field

            if (doSet) {
                val oldLockedCount = lockedCount
                val oldStatus = this.status
                if (oldStatus != null) {
                    detach(oldStatus)
                }
                field = value
                if (value != null) {
                    attach(value)
                }
                if (oldLockedCount != lockedCount) {
                    notifyEvent(LOCKED_COUNT)
                }
            }
        }

    /**
     * row size
     */
    var rowSize : Int = rowSize

    /**
     * column size
     */
    var columnSize : Int = columnSize 
    
    /**
     * you get the number of buttons
     */
    val cellsCount : Int
        get() {
            return rowSize * columnSize
        }
    /**
     * number of hit locations
     */
    var hitCount : Int = hitCount 

    /**
     * number of lock to prevent from opening button
     */
    var lockableCount: Int = lockableCount

    /**
     * hit location
     */ 
    val hitLocations : MutableSet<CellIndex> = HashSet<CellIndex>()


    /**
     * reasonalb number of lock to revent from opening button
     */
    val reasonableLockCount: Int
        get() {
            return min(cellsCount, lockableCount)
        }
    /**
     * count of locking button
     */
    val lockedCount: Int
        get() {
            var result = 0
            val status = this.status
            if (status != null) {
                result = status.lockingCount
            }
            return result
        }
    /**
     * count of availability to lock button
     */
    val lockableAvailability: Int
        get() {
           return reasonableLockCount - lockedCount 
        }

    /**
     * all mine locations
     */
    val mineLocations : Set<CellIndex>
        get() {
            return hitLocations
        }

    /**
     * unlocking mine locations
     */
    val unlockingMineLocations: Set<CellIndex>
        get() {
            val result = HashSet<CellIndex>()
            result.addAll(mineLocations) 
            result.removeAll(lockingLocations)
            return result
        }

    /**
     * locking locations
     */
    val lockingLocations: Set<CellIndex>
        get() {
            var result: Set<CellIndex>
            val status = this.status
            if (status != null) {
                result = status.lockingButtons
            } else {
                result = HashSet<CellIndex>()
            }
            return result
        }

    /**
     * get opened cells
     */
    val openedCells : Set<CellIndex>
        get() {
            var openedCells : Set<CellIndex>?
            val status = this.status
            if (status != null) {
                openedCells = status.openedButtons
            } else {
                openedCells = HashSet<CellIndex>()
            } 
            return openedCells
        }

    /**
     * is game over
     */
    val isOver: Boolean
        get() {
            val gameOverStatus = HashSet<GamingStatus>(
                arrayOf(GamingStatus.LOST,
                    GamingStatus.WON).toList())
            var result = gamingStatus in gameOverStatus
             
            return result 
        }
    /**
     * gaming status
     */
    val gamingStatus : GamingStatus
        get() {
            var result = GamingStatus.NOT_PLAYING
            val mineLocations = this.mineLocations
            val status = this.status
            if (status != null && mineLocations.size > 0) {
                val openingCells = status.openingButtons

                if (openingCells != null) {
                    if ((openingCells intersect mineLocations).size > 0) {
                        result = GamingStatus.LOST
                    }
                }
                if (result != GamingStatus.LOST) {
                    val openedCells = status.openedButtons
                    if ((openedCells intersect mineLocations).size > 0) {
                        result = GamingStatus.LOST
                    }
                    if (result != GamingStatus.LOST) {
                        var totalOpenedCellsCount =  openedCells.size 
                        if (openingCells != null) {
                            totalOpenedCellsCount += openingCells.size
                        }
                        val restCount = cellsCount - totalOpenedCellsCount
                        if (restCount == mineLocations.size) {
                            result = GamingStatus.WON
                        } 
                    }
                }
            }
            return result 
        }
    /**
     * event listeners
     */
    private val listeners: MutableList<(Any?, String)->Unit>
        = ArrayList<(Any?, String)->Unit>()

    /**
     * handle the event from status object
     */
    private var statusEventHdlr: ((Any?, String)->Unit)? = null
 
    /**
     * start game if it did not start
     */
    fun startIfNot(rowIndex: Int, colIndex: Int) {
        val status = this.status
        if (status != null) {
            if (!status.isStarted) {
                start(rowIndex, colIndex)
            }
        } 
    }
    /**
     * start game
     */
    fun start(rowIndex: Int, colIndex: Int) {
        hitLocations.clear()
        val columnSize = this.columnSize
        val rowSize = this.rowSize 
        if (rowSize > 0 && columnSize > 0) { 
            val locationSize = rowSize * columnSize
            if (hitCount > locationSize) {
                hitCount = round(locationSize * 0.9).toInt()
            }     
            
            while (hitLocations.size < hitCount) {
                val rIdx = Random.nextInt(0, rowSize)
                val cIdx = Random.nextInt(0, columnSize)
                if (rowIndex != rIdx || colIndex != cIdx) {
                    hitLocations.add(CellIndex(rIdx, cIdx));
                } 
            }
        } 
    } 

    /**
     * you get true if the cell is opened
     */
    fun isOpened(rowIndex: Int, colIndex: Int) : Boolean {
        var result = false
        val status = this.status
        if (status != null) {
            result = status.isOpened(rowIndex, colIndex)
        }
        return result
    }

    /**
     * you get true if the cell is opened
     */
    fun isOpened(cell: CellIndex) : Boolean {
        return isOpened(cell.row, cell.column)
    }

    /**
     * you get true if the cell is opening
     */
    fun isOpening(rowIndex: Int, colIndex: Int) : Boolean {
        val status = this.status
        var result = false
        if (status != null) {
            val cells = status.openingButtons
            if (cells != null) {
                result = CellIndex(rowIndex, colIndex) in cells        
            }
        } 
        return result
    }
    /**
     * register opened cell
     */
    fun registerOpened(rowIndex: Int, colIndex: Int) {
        val status = this.status
        if (status != null) {
            status.registerOpened(rowIndex, colIndex)
        }  
    }

    /**
     * toggle locking
     */
    fun toggleLock(rowIndex: Int, colIndex: Int): Boolean {
        var result = false
        val status = isLocking(rowIndex, colIndex)
        if (status != null) {
            if (status) {
                unlock(rowIndex, colIndex)
                result = true
            } else {
                result = lock(rowIndex, colIndex)
            } 
        }
        return result
    }

    /**
     * lock 
     */
    fun lock(rowIndex: Int, colIndex: Int): Boolean {
        val status = this.status
        var result = false 
        if (status != null) {
            if (gamingStatus == GamingStatus.NOT_PLAYING) {
                if (lockableAvailability > 0) {
                    result = status.lockCell(rowIndex, colIndex)
                }
            }
        }
        return result
    }
    /**
     * unlock
     */
    fun unlock(rowIndex: Int, colIndex: Int) {
        val status = this.status
        if (status != null) {
            status.unlockCell(rowIndex, colIndex)
        }
    }
    /**
     * get true if the location is locking
     */
    fun isLocking(rowIndex: Int, colIndex: Int): Boolean? {
        val status = this.status
        var result: Boolean? = null
        if (status != null) {
            result = status.isLocking(rowIndex, colIndex) 
        } 
        return result
    }

    /**
     * you get true if the cell is locked 
     */
    fun isLocking(cell: CellIndex) : Boolean? {
        return isLocking(cell.row, cell.column)
    }


    /**
     * get the number to display on button if it was opened
     */
    fun getNumberIfOpened(rowIndex: Int, colIndex: Int) : Int? {
        var result : Int? = null
        if (isOpened(rowIndex, colIndex) || isOpening(rowIndex, colIndex)) {
            result = getNumber(rowIndex, colIndex)
        }
        return  result
    }
    /**
     * get the number to display on button
     */
    fun getNumber(cell: CellIndex) : Int? {
        return getNumber(cell.row, cell.column)
    } 
    /**
     * get the number to display on button
     */ 
    fun getNumber(rowIndex : Int, colIndex: Int) : Int? {
        var result : Int? = null 
        val cell = CellIndex(rowIndex, colIndex)
        if (cell !in hitLocations) {
            val xOffset = intArrayOf(-1, 0, 1)
            val yOffset = intArrayOf(-1, 0, 1)  
            var totalNumber = 0
            xOffset.forEach({
                xIdx ->
                yOffset.forEach({
                    yIdx ->
                    val cell1 = CellIndex(cell.row + xIdx, 
                        cell.column + yIdx)
                    if (cell != cell1) {
                        if (0 <= cell1.row 
                            && cell1.row < rowSize) {
                            if (0 <= cell1.column 
                                && cell1.column < columnSize) {
                                    var disp: Int 
                                    disp = if (cell1 in hitLocations) 1 else 0
                                    totalNumber += disp
                            }
                        }
                    }
                })
            }) 
            result = totalNumber
        }  
        return result
    } 
    /**
     * get openable cell indices
     */ 
    fun getOpenableCells(
        rowIndex : Int, 
        columnIndex : Int) : Pair<MutableSet<CellIndex>, Boolean> {
        
        val cellsRes = HashSet<CellIndex>()
        val locking = isLocking(rowIndex, columnIndex)
        var result : Pair<Set<CellIndex>, Boolean>
        if (locking != null && !locking) {
            val startCell = CellIndex(rowIndex, columnIndex)
            val cellNum = getNumber(startCell)
            if (cellNum != null) {
                if (cellNum == 0) {
                    val cellsProcessed = HashSet<CellIndex>()  
                    val cells = ArrayList<CellIndex>()
                    val openableCells = HashSet<CellIndex>()
                    cellsProcessed.addAll(mineLocations)
                    cellsProcessed.addAll(openedCells)
                    cellsProcessed.addAll(lockingLocations)
                    cells.add(startCell) 
                    updateOpenableCells(cells, cellsProcessed, openableCells)
                    cellsRes.addAll(createUiOpenableCells(openableCells)) 
                } else {
                    cellsRes.add(startCell) 
                } 
            }  
            result = Pair(cellsRes, false)
        } else {
            result = Pair(cellsRes, true)
        }
        
        return result
    }
    /**
     * create openable cells to display user interface
     */
    fun createUiOpenableCells(openableCells:Set<CellIndex>): Set<CellIndex> {
        val result = HashSet<CellIndex>()
        val cellNext = arrayOf(
            CellIndex(-1, -1), CellIndex(-1, 0), CellIndex(-1, 1),
            CellIndex(0, -1), CellIndex(0, 0), CellIndex(0, 1),
            CellIndex(1, -1), CellIndex(1, 0), CellIndex(1, 1))
        openableCells.forEach({
            currentCell->
            cellNext.forEach({
                cellDisp->
                val neighborCell = CellIndex(
                    currentCell.row + cellDisp.row,
                    currentCell.column + cellDisp.column)
                if (isValidCell(neighborCell)) {
                    if (neighborCell !in mineLocations &&
                        !isOpened(neighborCell) &&
                        !isLocking(neighborCell)!!) {
                        result.add(neighborCell)
                    }
                }
            })
        }) 
        return result
    }

     
    /**
     * update openable cell
     */
    fun updateOpenableCells(cells: MutableList<CellIndex>,
        cellsProcessed: MutableSet<CellIndex>,
        openableCells: MutableSet<CellIndex>) {
        if (cells.size > 0) {
            val cellNext = arrayOf(
                CellIndex(-1, -1), CellIndex(-1, 0), CellIndex(-1, 1),
                CellIndex(0, -1), CellIndex(0, 1),
                CellIndex(1, -1), CellIndex(1, 0), CellIndex(1, 1))
            val currentCell = cells.removeAt(0) 
            openableCells.add(currentCell)
            cellsProcessed.add(currentCell) 
            cellNext.forEach({
                cellDisp->
                val neighborCell = CellIndex(currentCell.row + cellDisp.row,
                    currentCell.column + cellDisp.column)
                if (isValidCell(neighborCell)) {
                    val neighborNumber = getNumber(neighborCell)
                    if (neighborNumber != null && neighborNumber == 0) {
                        if (neighborCell !in cellsProcessed) {
                            cells.add(neighborCell)
                        }
                    }
                }
            })
            updateOpenableCells(cells, cellsProcessed, openableCells)
        }
    } 

    /**
     * you get true if the cell is in 0..rowSize - 1 and 0..columnSize - 1
     */
    fun isValidCell(cell: CellIndex) : Boolean {
        var result = cell.row in 0 until rowSize
        if (result) {
            result = cell.column in 0 until columnSize
        } 
        return result
    }

    /**
     * connect this and status 
     */
    fun attach(status: Status) {
        statusEventHdlr = { arg0, arg1 -> onStatusChanged(arg0, arg1) }
        status.addListener(statusEventHdlr!!)
    }

    /**
     * disconnect this and status
     */
    fun detach(status: Status) {
        if (statusEventHdlr != null) {
            status.removeListener(statusEventHdlr!!)
            statusEventHdlr = null
        }
    }

    /**
     * handle evet for status
     */
    @Suppress("UNUSED_PARAMETER")
    fun onStatusChanged(arg: Any?, kind: String) {
        if (kind == Status.LOCKING_COUNT) {
            notifyEvent(LOCKED_COUNT)
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
    private fun notifyEvent(name: String) {
        val listeners = ArrayList(this.listeners)
        listeners.forEach { it(this, name) }
    }
}
// vi: se ts=4 sw=4 et:
