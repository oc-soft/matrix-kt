
package net.ocsoft.ui

import jQuery
import JQuery
import JQueryEventObject

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
            return dropdownTarget?.css("display") != "none"
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
                    css = "block"  
                    attachHandlerToHideDropdown()
                    dropdownTarget?.css("display", css)
                }
            }
        }
    

    /**
     * bind html node 
     */
    fun bind(eventSource: String, dropdownTarget: String) {
        this.sourceClickHdlr = { evt, _ -> handleSourceNodeClick(evt) }
        this.targetClickHdlr = { evt, _ -> handleTargetNodeClick(evt) }
        jQuery(eventSource).on("click", sourceClickHdlr!!)
        jQuery(dropdownTarget).on("click", targetClickHdlr!!)
        sourceQuery = eventSource
        dropdownQuery = dropdownTarget
        val visibleDropdown = this.visibledDropdown
        if (visibledDropdown!= null && visibleDropdown!!) {
            attachHandlerToHideDropdown()
        }
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
