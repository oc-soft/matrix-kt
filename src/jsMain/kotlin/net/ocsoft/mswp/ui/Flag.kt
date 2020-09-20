package net.ocsoft.mswp.ui

import net.ocsoft.mswp.Logic
import kotlinx.browser.window
import org.w3c.dom.HTMLElement
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import jQuery

/**
 * manage flag
 */
class Flag(
    val option: Option) {

    /**
     * singleton instance
     */
    companion object {
        /**
         * flagging item name
         */
        val FLAGGING = "flagging"
    }

    /**
     * option
     */
    data class Option(
        /**
         * query for flag item
         */
        val flagItemQuery: String,
        /**
         * query for flag icon
         */
        val flagIconQuery: String,
        /**
         * query for number query
         */
        val flagNumberQuery: String,
        /**
         * animation class name
         */
        val animationClassName: String) 

    /**
     * root element
     */
    var rootElement: HTMLElement? = null

    

    /**
     * event handler
     */
    var clickFlagHdlr: ((Event)->Unit)? = null

    /**
     * event listeners
     */
    private val listeners: MutableList<(Any?, String)->Unit>
        = ArrayList<(Any?, String)->Unit>()


    /**
     * game logic
     */
    var logic: Logic? = null
        set(value) {
            val doSet = value != field
            if (doSet) {
                val oldLogic = this.logic
                if (oldLogic != null) {
                    detach(oldLogic)
                }
                field = value 
                if (value != null) {
                    attach(value)
                } 
            }
        }

    

    /**
     * flag item
     */
    val flagItemUI: HTMLElement?
        get() {
            var result: HTMLElement? = null
            val rootElem = this.rootElement
            if (rootElem != null) {
                val jqFlagItem = jQuery(option.flagItemQuery)
                if (jqFlagItem.length.toInt() > 0) {
                    result = jqFlagItem[0]
                }
            }
            return result
        }

    /**
     * flag icon
     */
    val flagIconUI: HTMLElement?
        get() {
            var result: HTMLElement? = null
            val rootElem = this.rootElement
            if (rootElem != null) {
                val jqFlagIcon = jQuery(selector = option.flagIconQuery,
                    context = rootElem)
                if (jqFlagIcon.length.toInt() > 0) {
                    result = jqFlagIcon[0]
                }
            }
            return result
        }
    /**
     * flag number 
     */
    val flagNumberUI: HTMLElement?
        get() {
            var result: HTMLElement? = null
            val rootElem = this.rootElement
            if (rootElem != null) {
                val jqFlagNumber = jQuery(
                    selector = option.flagNumberQuery,
                    context = rootElem)
                if (jqFlagNumber.length.toInt() > 0) {
                    result = jqFlagNumber[0]
                }
            }
            return result
        }
    /**
     * locking available count
     */
    var lockingAvailableCountUI: Int?
        get() {
            val flagNumberUI = this.flagNumberUI 
            var result: Int? = null
            if (flagNumberUI != null) {
                val num = flagNumberUI.innerText.toIntOrNull()
                if (num != null) {
                    result = num
                }
            } 
            return result
        }
        set(value) {
            val flagNumberUI = this.flagNumberUI 
            var strVal = ""
            if (value != null) {
                strVal = value.toString()
            } 
            if (flagNumberUI != null) {
                flagNumberUI.innerText = strVal
            } 
         }

    /**
     * you get true if it is flaging mode
     */
    var isFlagging: Boolean?
        get() {
            return isFlagIconAnimating
        }
        set(value) {
            val oldValue = isFlagging
            isFlagIconAnimating = value
            if (isFlagging != oldValue) {
                notifyChange(FLAGGING)
            }
        }

    /**
     * whether is flag icon animating?
     */
    var isFlagIconAnimating: Boolean? = null
        get() {
            var result: Boolean? = null
            val iconUi = this.flagIconUI
            if (iconUi != null) {
               result = iconUi.classList.contains(option.animationClassName) 
            }
            return result
        }
        set(value) {
            if (value != null) {
                if (value != field) {
                    val iconUi = this.flagIconUI
                    if (iconUi != null) {
                        if (value) {
                            iconUi.classList.add(option.animationClassName)
                        } else {
                            iconUi.classList.remove(option.animationClassName)
                        } 
                    }
                }
            }
        }

    /**
     * logic change handler
     */
    var logicChangeHdlr: ((Any?, String)->Unit)? = null

    /**
     * connect this object to  html node
     */
    fun bind(rootElement: HTMLElement) {
        this.rootElement = rootElement
 
        
        val clickHdlr: (Event) -> Unit  = { this.onClickFlag(it) }
        
        val flagItem = this.flagItemUI
        if (flagItem != null) {
            flagItem.addEventListener("click", clickHdlr)
        }

        this.clickFlagHdlr = clickHdlr
        this.postSyncUIWithLogic()
    }

    /**
     * disconnect this object from html node
     */
    fun unbind() {
        if (this.clickFlagHdlr != null) {
            val flagItem = this.flagItemUI
            flagItem?.removeEventListener("click", this.clickFlagHdlr)
            
            this.clickFlagHdlr = null
        }

        this.rootElement = null
    }

    /**
     * handle click on flag element
     */
    @Suppress("UNUSED_PARAMETER")
    fun onClickFlag(event: Event) {
       postTogleFlagging() 
    }

    /**
     * do flip-flop flagging lately
     */
    fun postTogleFlagging() {
        window.setTimeout({ toggleFlagging() })
    }

    /**
     * do flip-flop flagging
     */
    fun toggleFlagging() {
        val flagging = isFlagging
        if (flagging != null) {
            isFlagging = !flagging
        }
    }

    /**
     * synchronize user interface with logic lately
     */
    fun postSyncUIWithLogic() {
        window.setTimeout({ syncUIWithLogic() })
    }

    /**
     * synchronize user interface with logic
     */
    fun syncUIWithLogic() {
        val logic = this.logic
        var lockableCount: Int? = null
        if (logic != null) {
            lockableCount = logic.lockableAvailability
        } 
        lockingAvailableCountUI = lockableCount  
    }

    /**
     * handle the event from logic
     */
    @Suppress("UNUSED_PARAMETER")
    fun onLogicChanged(anObj: Any?, kind: String) {
        if (kind == Logic.LOCKED_COUNT) {
            postSyncUIWithLogic()
        }
    }

    /**
     * connect this object to logic object
     */
    fun attach(logic: Logic) {
        this.logicChangeHdlr = { arg0, arg1 -> onLogicChanged(arg0, arg1)  }
        logic.addListener(this.logicChangeHdlr!!)
    }

    /**
     * disconnect this object from logic object
     */
    fun detach(logic: Logic) {
        if (this.logicChangeHdlr != null) {
            logic.removeListener(this.logicChangeHdlr!!)
            this.logicChangeHdlr = null
        }
    }


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
