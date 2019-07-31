
package net.ocsoft.mswp


class Model(val rowCount: Int = 20, 
    val columnCount: Int = 20,
    val physicsEng: PhysicsEng = PhysicsEng()) {
    override fun toString(): String
        = "rowCount: ${this.rowCount}\ncolumnCount: ${this.columnCount}"
	
}
