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
        if (isOpened(rowIndex, colIndex)) {
            result = getNumber(rowIndex, colIndex)
        }
        return  result
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
                        val disp = if (cell1 in hitLocations) 1 else 0
                        totalNumber += disp
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
        columnIndex : Int) : Set<CellIndex> {
        
        val result = HashSet<CellIndex>()
       
        var cellsToStart = HashSet<CellIndex>()  
        updateOpenableCellIndices(rowIndex, columnIndex, 0, cellsToStart)
        result.addAll(cellsToStart)
        val zeroColumns =  findZeroColumns(rowIndex, cellsToStart)

        intArrayOf(-1, 1).forEach({ step ->
            zeroColumns.forEach({
                updateOpenableCellIndices(rowIndex + step,
                    it, step, result)   
            }) 
        })
        return result
    }


    /**
     * update openable cell incies
     */
    fun updateOpenableCellIndices(
        rowIndex: Int,
        colIndex: Int,
        step: Int,
        indices: MutableSet<CellIndex>) {
        var cellNumber = getNumber(rowIndex, colIndex) 
        if (cellNumber != null) {
            indices.add(CellIndex(rowIndex, colIndex))
            if (cellNumber == 0) {
                val subIndices = HashSet<CellIndex>()
                intArrayOf(-1, 1).forEach({
                    updateOpenableCellLine(
                        rowIndex, colIndex, it, subIndices) 
                })
                val zeroColumns = findZeroColumns(rowIndex, subIndices)
                indices.addAll(subIndices)
                var nextRowIndex = rowIndex
                if (step > 0) {
                    nextRowIndex++
                } else if (step < 0) {
                    nextRowIndex--
                }
                if (nextRowIndex >= 0 && nextRowIndex < rowSize - 1
                    && nextRowIndex != rowIndex) {
                    zeroColumns.forEach({ 
                        updateOpenableCellIndices(
                            nextRowIndex, it, step, indices)
                    })
                }
            }
        }       
          
    }
    /**
     * find 0 number column indices
     */
    fun findZeroColumns(rowIndex: Int,
        indices: Set<CellIndex>) : Set<Int> {
        val result = HashSet<Int>()
        indices.forEach({
            if (it.row == rowIndex) {
                val cellNumber = getNumber(it.row, it.column)
                if (cellNumber != null && cellNumber > 0) {
                    result.add(it.column)
                }
            }
        })

        return result
    } 
    /**
     * update openable cell's line
     */
    fun updateOpenableCellLine(
        rowIndex: Int,
        colIndex: Int,
        step: Int,
        indices : MutableSet<CellIndex>) {
        var cellNumber = getNumber(rowIndex, colIndex) 
        if (cellNumber != null) {
             indices.add(CellIndex(rowIndex, colIndex))
             if (cellNumber == 0) {
                if (step > 0 && colIndex < columnSize - 1) {
                    updateOpenableCellLine(rowIndex, colIndex + 1,
                        step, indices)
                } else if (step < 0 && colIndex > 0) {
                    updateOpenableCellLine(rowIndex, colIndex - 1,
                        step, indices)
                }
            }
        }
    }
}
