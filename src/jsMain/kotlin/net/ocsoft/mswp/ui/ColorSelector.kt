package net.ocsoft.mswp.ui

import net.ocsoft.mswp.ColorScheme
import jQuery
import JQueryEventObject
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLCanvasElement

/**
 * user interface to select color
 */
class ColorSelector(
    /**
     * option
     */
    val option : Option) {

    /**
     * setting
     */
    data class Option(val modalQuery: String,
        val canvasQuery: String,
        val okQuery: String)

    /**
     * color picker user interface
     */
    var colorPicker: net.ocsoft.color.picker.UI? = null

    /**
     * called this method when modal is hidden
     */
    var modalHiddenHdlr: ((JQueryEventObject, Any?)->Any)? = null

    /**
     * ok button handler
     */
    var okHdlr: ((JQueryEventObject, Any?)->Any)? = null

    /**
     * color item click handler
     */
    var colorItemClickHdlr: ((JQueryEventObject, Any?)->Any)? = null

 
    /**
     * color scheme
     */
    var colorScheme: ColorScheme? = null

    /**
     * visible modal color selector
     */
    fun show() {
        bindModal()
        val modalObj = jQuery(option.modalQuery)
        modalObj.asDynamic().modal("show") 
    } 

    /**
     * hide modal color selector
     */
    fun hide() {
        unbindModal()
    }

    /**
     * bind color picker user interface into html element
     */
    fun bindColorPicker() {
        val colorPicker = net.ocsoft.color.picker.UI() 
        val modal = jQuery(option.modalQuery) 
        syncCanvasWithClientSize(
            jQuery(option.canvasQuery)[0] as HTMLCanvasElement)

        colorPicker.bind(modal[0]!!) 
        
        this.colorPicker = colorPicker
    }

    /**
     * unbind color picker user interface
     */
    fun unbindColorPicker() {
        val colorPicker = this.colorPicker
        colorPicker?.unbind()
        this.colorPicker = null
    }

    /**
     * connect functions into modal user interface.
     */
    fun bindModal() {
        modalHiddenHdlr = { e, arg -> onModalHidden(e, arg) }
        okHdlr = { e, arg -> onOk(e, arg) }
        colorItemClickHdlr = { e, args -> onColorItemClick(e, args) }
       
        val modalQuery = jQuery(option.modalQuery)
        jQuery(option.okQuery).on("click", okHdlr!!)
        modalQuery.on("hidden.bs.modal", modalHiddenHdlr!!)

        setupContents()
    }

    /**
     * detach functions from modal user interface.
     */
    fun unbindModal() {
        teardownContents()

        if (modalHiddenHdlr != null) {
            jQuery(option.modalQuery).off("hidden.bs.modal", modalHiddenHdlr!!)
            modalHiddenHdlr = null
        }

        if (okHdlr != null) {
            jQuery(option.okQuery).off("click", okHdlr!!)
            okHdlr = null
        }
        if (colorItemClickHdlr != null) {
            colorItemClickHdlr = null
        }
    }

    /**
     * setup modal dialog contents
     */
    fun setupContents() {
        bindColorPicker()
    }

    /**
     * tear down modal dialog contents
     */
    fun teardownContents() {
        unbindColorPicker()
    }

    /**
     * handle the event to hide modal
     */
    fun onModalHidden(e: JQueryEventObject, a: Any?) {
        unbindModal()
    }

    /**
     * handle the click event on color item
     */
    fun onColorItemClick(e: JQueryEventObject, a: Any?) {
        
    }

    /**
     * handle ok
     */
    @Suppress("UNUSED_PARAMETER")
    fun onOk(e: JQueryEventObject, args: Any?): Any {
        postSave()
        return Unit
    }

    /**
     * do save setting
     */
    private fun postSave() {
    }
 

    /**
     * synchronize canvas size with client size.
     */
    private fun syncCanvasWithClientSize(
        canvas: HTMLCanvasElement) {

        var doResize: Boolean
        doResize = canvas.width != canvas.clientWidth
        if (!doResize) {
            doResize = canvas.height != canvas.clientHeight
        }
        if (doResize) {
            canvas.width = canvas.clientWidth
            canvas.height = canvas.clientHeight
        }  
    }



}

// vi: se ts=4 sw=4 et:
