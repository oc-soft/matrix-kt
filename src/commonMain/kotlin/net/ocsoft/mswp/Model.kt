
package net.ocsoft.mswp


class Model(rowCount: Int = 20, 
    columnCount: Int = 20,
    val physicsEng: PhysicsEng = PhysicsEng(),
    val logic: Logic = Logic(rowCount, columnCount, 5)) {
    init {
        logic.status = Status()
    }	
}
