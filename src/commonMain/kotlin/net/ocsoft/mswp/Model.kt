package net.ocsoft.mswp

/**
 * gaming model
 */
class Model(rowCount: Int = 20, 
    columnCount: Int = 20,
    mineCount: Int = 5,
    lockableCount: Int = 6,
    /**
     * physics engine
     */
    val physicsEng: PhysicsEng = PhysicsEng(),
    /**
     * game logic
     */
    val logic: Logic =
        Logic(rowCount, columnCount, mineCount, lockableCount)) {

    /**
     * initializer
     */
    init {
        logic.status = Status()
    }	
}
// vi: se ts=4 sw=4 et:
