
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
    var sourceClickHdlr: ((event: JQueryEventObject, args: Any)->Any)? = null

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
            if (value != null) {
                dropdownTarget?.css("display",
                    if (value!!) { "block" } else { "none" })
            } 
        }
    

    /**
     * bind html node 
     */
    fun bind(eventSource: String, dropdownTarget: String) {
        this.sourceClickHdlr = { evt, args -> handleSourceNodeClick(evt) }
        jQuery(eventSource)?.on("click", sourceClickHdlr!!)
        sourceQuery = eventSource
        dropdownQuery = dropdownTarget
    }

    /**
     * detach this object from html node
     */
    fun unbind() {
        if (sourceClickHdlr != null) {
            jQuery(sourceQuery!!)?.off("click", sourceClickHdlr!!)
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
            this.visibledDropdown = !visibledDropdown!!
        }
    }

}
// vi: se ts=4 sw=4 et:
