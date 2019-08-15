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
    hitCount: Int) {
    /**
     * game status
     */ 
    var status: Status? = null
    /**
     * row size
     */
    var rowSize : Int = rowSize

    /**
     * column size
     */
    var columnSize : Int = columnSize 
    
    /**
     * number of hit locations
     */
    var hitCount : Int = hitCount 

    /**
     * hit location
     */ 
    val hitLocations : MutableSet<CellIndex> = HashSet<CellIndex>()

    /**
     * all mine locations
     */
    val mineLocations : Set<CellIndex>
        get() {
            return hitLocations
        }
    /**
     * is game over
     */
    val isOver: Boolean
        get() {
            var result = false
            val mineLocations = this.mineLocations
            val status = this.status
            if (status != null && mineLocations != null) {
                val openingCells = status.openingButtons

                if (openingCells != null) {
                    result = (openingCells intersect mineLocations).size > 0
                }
                if (!result) {
                    val openedCells = status.openedButtons
                    result = (openedCells intersect mineLocations).size > 0
                }
            }
            return result 
        }
 
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
        var status = this.status
        if (status != null) {
            status.registerOpened(rowIndex, colIndex)
        }  
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
        columnIndex : Int) : MutableSet<CellIndex> {
        
        val result = HashSet<CellIndex>()
        val startCell = CellIndex(rowIndex, columnIndex)
        val cellNum = getNumber(startCell)
        if (cellNum != null) {
            if (cellNum == 0) {
                val cellsProcessed = HashSet<CellIndex>()  
                val cells = ArrayList<CellIndex>()
                val openableCells = HashSet<CellIndex>()
                cells.add(startCell) 
                updateOpenableCells(cells, cellsProcessed, openableCells)
                result.addAll(createUiOpenableCells(openableCells)) 
            } else {
                result.add(startCell) 
            } 
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
                    result.add(neighborCell)
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
     * you get true if the celli is in 0..rowSize - 1 and 0..columnSize - 1
     */
    fun isValidCell(cell: CellIndex) : Boolean {
        var result = false
        result = cell.row in 0..rowSize - 1
        if (result) {
            result = cell.column in 0..columnSize - 1
        } 
        return result
    }
}
