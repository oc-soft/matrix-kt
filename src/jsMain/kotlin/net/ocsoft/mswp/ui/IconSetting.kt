package net.ocsoft.mswp.ui


import kotlin.collections.MutableList

/**
 * icon setting
 */
class IconSetting(
    ngIcon: Persistence.Icon = Persistence.Icon("fas", "skull"),
    okIcon: Persistence.Icon = Persistence.Icon("fas", "star")) {

    /**
     * class instance
     */
    companion object {
        /**
         * NG icon property name
         */
        val NG_ICON = "ng"
        /**
         * OK icon property name
         */ 
        val OK_ICON = "ok"
    }

    /**
     * NG icon
     */
    var ngIcon: Persistence.Icon = ngIcon
        set(value)
            {
                if (field != value) {
                    field = value
                    notifyChange(NG_ICON)
                }
            }
     
    /**
     * OK icon
     */
    var okIcon: Persistence.Icon = okIcon
        set(value)
            {
                if (field != value) {
                    field = value
                    notifyChange(OK_ICON)
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
