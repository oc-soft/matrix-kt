
package net.ocsoft.mswp


class Model(val rowCount: Int = 20, 
    val columnCount: Int = 20) {
    override fun toString(): String
        = "rowCount: ${this.rowCount}\ncolumnCount: ${this.columnCount}"
	
}
