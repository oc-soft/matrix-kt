
package net.ocsoft.ui

import org.w3c.dom.HTMLElement
import jQuery
import JQuery
import JQueryEventObject
import popper.Popper

/**
 * manage drop down user interface
 */
class Dropdown {

    /**
     * handle source clicked
     */
    var sourceClickHdlr: ((e: JQueryEventObject, args: Any)->Any)? = null

    /**
     * handle target clicked
     */
    var targetClickHdlr: ((e: JQueryEventObject, args: Any)->Any)? = null


    /**
     * handle drowdown background
     */
    var backgroundClickHdlr: ((e: JQueryEventObject, args: Any)->Any)? = null

    /**
     * dropdown query string
     */
    var dropdownQuery: String? = null

    /**
     * event source node
     */
    var sourceQuery: String? = null
    /**
     * dropdown target
     */
    var dropdownTarget: JQuery? = null
        get() {
            var result : JQuery? = null
            val ddq = dropdownQuery
            if (ddq != null) {
                result = jQuery(ddq)
            }
            return result
        }

    /**
     * visibility about dropdown
     */
    var visibledDropdown: Boolean? 
        get() {
            return dropdownTarget?.css("visibility") != "hidden"
        }
        set(value) {
            var doSet = false 
            val oldState = visibledDropdown
            if (oldState != null) {
                if (value != null) {
                    doSet = oldState != value
                } else {
                    doSet = true
                }
            } else {
                if (value != null) {
                    doSet = true
                }
            }
            if (doSet) {
                if (value != null) {
                    var css: String
                    if (value) {
                        dropdownTarget?.css("visibility", "visible")
                        dropdownTarget?.css("pointer-event", "auto")
                    } else { 
                        dropdownTarget?.css("visibility", "hidden")
                        dropdownTarget?.css("pointer-event", "none")
                    } 
                }
            }
        }
    
    /**
     * popper instance
     */
    var popperInstance : popper.Instance? = null


    /**
     * bind html node 
     */
    fun bind(eventSource: String, dropdownTarget: String) {
        this.sourceClickHdlr = { evt, _ -> handleSourceNodeClick(evt) }
        this.targetClickHdlr = { evt, _ -> handleTargetNodeClick(evt) }
        val jqReference = jQuery(eventSource)
        val jqTarget = jQuery(dropdownTarget)

        jqReference.on("click", sourceClickHdlr!!)
        jqTarget.on("click", targetClickHdlr!!)
        sourceQuery = eventSource
        dropdownQuery = dropdownTarget
        val visibleDropdown = this.visibledDropdown

        val reference = jqReference[0] as HTMLElement
        val target = jqTarget[0] as HTMLElement
        val popperOption = popper.Css.readOption(target)

        popperInstance = Popper.createPopper(reference, target, 
            popperOption as Any)
        attachHandlerToHideDropdown()
    }

    /**
     * detach this object from html node
     */
    fun unbind() {
        detachHandlerToHideDropdown()
        if (targetClickHdlr != null) {
            jQuery(dropdownQuery!!).off("click", targetClickHdlr!!)
            targetClickHdlr = null
        }
        if (sourceClickHdlr != null) {
            jQuery(sourceQuery!!).off("click", sourceClickHdlr!!)
            sourceClickHdlr = null
        }
        if (popperInstance != null) {
            popperInstance!!.destroy()
            popperInstance = null
        }
        sourceQuery = null 
        dropdownQuery = null
     }

    /**
     * handle source node event
     */ 
    fun handleSourceNodeClick(eventObj: JQueryEventObject) {
        val visibledDropdown = this.visibledDropdown
        if (visibledDropdown != null) {
            this.visibledDropdown = !visibledDropdown
        }
        eventObj.stopPropagation()
    }
    
    /**
     * handle target node event
     */
    fun handleTargetNodeClick(eventObj: JQueryEventObject) {
        eventObj.stopPropagation()
    }

    /**
     * this procedure hide dropdown if user click some location.
     */
    fun attachHandlerToHideDropdown() {
        if (this.backgroundClickHdlr == null) {
            this.backgroundClickHdlr = { _: JQueryEventObject, _: Any ->
                this.visibledDropdown = false 
                Unit 
            }
            jQuery(kotlinx.browser.document).on("click",
                this.backgroundClickHdlr!!)
        }
    } 


    /**
     * this procedure hide dropdown if user click some location.
     */
    fun detachHandlerToHideDropdown() {
        if (this.backgroundClickHdlr != null) {
            jQuery(kotlinx.browser.document).off(
                "click", this.backgroundClickHdlr!!)
            this.backgroundClickHdlr = null 
        }
    } 


}
// vi: se ts=4 sw=4 et:
