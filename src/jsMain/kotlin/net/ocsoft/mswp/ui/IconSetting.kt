package net.ocsoft.mswp.ui


import kotlin.collections.MutableList

/**
 * icon setting
 */
class IconSetting(
    mineIcon: Persistence.Icon = Persistence.Icon("fas", "skull")) {

    /**
     * class instance
     */
    companion object {
        val MINE_ICON = "mineIcon"
    }

    var mineIcon: Persistence.Icon = mineIcon
        set(value)
            {
                if (field != value) {
                    field = value
                    notifyChange(MINE_ICON)
                }
            }
   
    /**
     * event listeners
     */
    private val listeners: MutableList<(Any?, String)->Unit>
        = ArrayList<(Any?, String)->Unit>()


    /**
     * add listener
     */ 
    fun addListener(listener: ((Any?, String)->Unit)) {
        listeners.add(listener) 
    }
    /**
     * remove listener
     */
    fun removeListener(listener: ((Any?, String)->Unit)) {
        val idx = listeners.indexOfLast({ it == listener })
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
