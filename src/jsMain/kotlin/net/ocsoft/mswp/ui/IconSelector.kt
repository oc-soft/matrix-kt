package net.ocsoft.mswp.ui

import jQuery
import JQueryEventObject
import fontawesome.Icons
/**
 * icon selector
 */
class IconSelector(val option : Option) {
    /**
     * option 
     */
    data class Option(val modalQuery: String, 
        val itemsQuery: String,
        val itemTemplateQuery: String)


    /**
     * bind into html
     */
    fun bind() {
        var ids = Icons.getAllIdentifiers() 
        ids.forEach({ appendItem(it) })
    }

    /**
     * unbind from html
     */
    fun unbind() {
        val items = jQuery(option.itemsQuery)
        items.empty()
    }


    /**
     * visible modal dialog
     */
    fun show() {
        jQuery(option.modalQuery).asDynamic().modal() 
    }


    fun appendItem(item : Icons.Identifier) {
        val newLine = jQuery(option.itemTemplateQuery).html()
        val newNode = jQuery(newLine) 
        newNode.text(item.prefix + item.name)
        val items = jQuery(option.itemsQuery)
        items.append(newNode)
        
    }
}

