package net.ocsoft.mswp


/**
 * manage field of view, frame aspect, near and far planes
 */
class Camera(fieldOfView: Float = 45 * kotlin.math.PI.toFloat() / 180,
    aspect: Float = 2f,
    zNear: Float = 0.1f,
    zFar: Float = 100f) {

    companion object {
        val EVENT_NAMES = arrayOf(
            "fieldOfView",
            "aspect",
            "zNear",
            "zFar")
    }
    /**
     * field of view
     */ 
    var fieldOfView = fieldOfView
        set(value) {
            if (field != value) {
               field = value
               this.notify("fieldOfView")
            }
        }
    /**
     * frame aspect
     */
    var aspect = aspect
        set(value) {
            if (field != value) {
               field = value
               this.notify("aspect")
            }
        }
    /**
     * near plane bound
     */ 
    var zNear = zNear 
        set(value) {
            if (field != value) {
               field = value
               this.notify("zNear")
            }
        }
    /**
     * far plane bound
     */ 
    var zFar = zFar
        set(value) {
            if (field != value) {
               field = value
               this.notify("zFar")
            }
        }
    /**
     * event listeners
     */
    private val listeners : MutableMap<String, MutableList<(String, Camera) -> Unit>?>
        = HashMap<String, MutableList<(String, Camera) -> Unit>?>()
    /**
     * listeners to recieve all event
     */
    private val allEventListeners: MutableList<(String, Camera) -> Unit> 
        = ArrayList<(String, Camera) -> Unit>()
    
   
    /**
     * register event listener
     */
    fun on(eventName: String?, listener: (String, Camera)->Unit) {
        if (eventName != null) {
            var listeners = this.listeners.get(eventName)
            if (listeners == null) {
                listeners = ArrayList<(String, Camera) -> Unit>()
                listeners.add(listener)
                this.listeners[eventName] = listeners
            }
        } else {
            allEventListeners.add(listener)
        }
    }
    /**
     * unregister event lister
     */
    fun off(eventName: String?, listener: (String, Camera)->Unit) {
        if (eventName != null) {
            var listeners = this.listeners.get(eventName)
            if (listeners != null) {
                listeners.remove(listener)
                if (listeners.size == 0) {
                    this.listeners.remove(eventName)
                }
            }
        } else {
            allEventListeners.remove(listener)
        }
    } 
    /**
     * notify event
     */
    fun notify(eventName: String) {
        var listeners = this.listeners.get(eventName)
        if (listeners != null) {
            listeners.forEach({ listener -> 
                listener(eventName, this)
            })
        }
        allEventListeners.forEach({ listener ->
            listener(eventName, this)
        })
    }
}
