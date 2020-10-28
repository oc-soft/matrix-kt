package less

import kotlinx.browser.window
import kotlin.collections.Map
import org.w3c.dom.get

/**
 * less.js wrapper
 */
class Less() {

    /**
     * class instance
     */
    companion object {

        /**
         * global instance
         */
        val instance: Less
            get() = Less(window["less"])
           
    }

    /**
     * js less obecect 
     */
    private var lessObject: dynamic = null

    /**
     * constructor
     */
    constructor(lessObject: dynamic):
        this() {
        this.lessObject = lessObject
    }

     

    /**
     * modify less variable
     */
    fun modifyVars(record: Map<String, String>) {
        val lessObj = this.lessObject

         
        if (lessObj != null) {
            val recordObj = js("{}")
            record.forEach {
                recordObj[it.key] = it.value
            } 
            lessObj.modifyVars(recordObj)
        }
    }

}

// vi: se ts=4 sw=4 et:
