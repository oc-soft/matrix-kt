package net.ocsoft.mswp.ui


import kotlin.collections.MutableList
import kotlin.collections.HashMap

/**
 * icon setting
 */
class IconSetting(
    ngIcon: Persistence.Icon = Persistence.Icon("fas", "skull"),
    okIcon: Persistence.Icon = Persistence.Icon("fas", "star"),
    lightMarkerIcon: Persistence.Icon = Persistence.Icon("fas", "lightbulb")) {

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

        /**
         * light origin marker
         */
        val LIGHT_MARKER = "light-marker"

        /**
         * all icon property names
         */
        val allIcons : Array<String>
            get() {
                val result = arrayOf(
                    NG_ICON,
                    OK_ICON,
                    LIGHT_MARKER)
                return result
            }
    }

    /**
     * NG icon
     */
    var ngIcon: Persistence.Icon = ngIcon
        set(value) {
            if (field != value) {
                field = value
                notifyChange(NG_ICON)
            }
        }
     
    /**
     * OK icon
     */
    var okIcon: Persistence.Icon = okIcon
        set(value) {
            if (field != value) {
                field = value
                notifyChange(OK_ICON)
            }
        }
    /**
     * light origin marker icon
     */
    var lightMarkerIcon: Persistence.Icon = lightMarkerIcon
        set(value) {
            if (field != value) {
                field = value
                notifyChange(LIGHT_MARKER)
            }
        } 

    /**
     * snap short of icon mapping
     */
    val icons: Map<String, Persistence.Icon>
        get() {
            val result = HashMap<String, Persistence.Icon>()
            result[OK_ICON] = okIcon
            result[NG_ICON] = ngIcon
            result[LIGHT_MARKER] = lightMarkerIcon
            return result
        }
    
    /**
     * event listeners
     */
    private val listeners: MutableList<(Any?, String)->Unit>
        = ArrayList<(Any?, String)->Unit>()


    /**
     * add listener
     */ 
    fun addListener(listener: (Any?, String)->Unit) {
        listeners.add(listener) 
    }
    /**
     * remove listener
     */
    fun removeListener(listener: (Any?, String)->Unit) {
        val idx = listeners.indexOfLast { it == listener }
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
// vi: se ts=4 sw=4 et:
