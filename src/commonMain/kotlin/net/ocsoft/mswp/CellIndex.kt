package net.ocsoft.mswp

/**
 * manage row and col index.
 */
class CellIndex(val row: Int,
    val column: Int) : Comparable<CellIndex> {
    /**
     * convert int array
     */
    fun toIntArray(): IntArray {
        return intArrayOf(row, column)
    }
    /**
     * calculate hash code
     */
    override fun hashCode():Int {
        return row.inv() or column.inv() 
    }
    /**
     * compare the other object
     */
    override fun compareTo(other: CellIndex): Int {
        var result = row  - other.row
        if (result == 0) {
            result = column - other.column
        }
        return result
    }
    /**
     * you get true if the other object is equals
     */
    override fun equals(other: Any?): Boolean {
        var result = this === other 
        if (!result) {
            if (other is CellIndex) {
                val otherIndices = other
                result = compareTo(otherIndices) == 0
                 
            } 
        }
        return result
    }
} 




