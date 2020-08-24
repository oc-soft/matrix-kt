package net.ocsoft.mswp.ui

import kotlin.text.Regex
import kotlin.text.toInt
import kotlin.math.min
import kotlin.math.max
import kotlin.math.roundToInt
import net.ocsoft.mswp.ColorScheme
import net.ocsoft.mswp.ColorSchemeContainer
import jQuery
import JQuery
import JQueryEventObject
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.window
/**
 * user interface to select color
 */
class ColorSelector(
    /**
     * option
     */
    val option : Option) {

    /**
     * class instance
     */
    companion object {
        /**
         * rgb to float array
         */
        fun rgbStrToFloatArray(rgb: String): FloatArray? {
            val reg = Regex(
                "^rgb\\s*\\(\\s*"
                + "(\\d+)\\s*,\\s*"
                + "(\\d+)\\s*,\\s*"
                + "(\\d+)\\s*\\)")
            val matchRes = reg.find(rgb)
            var result: FloatArray? = null
            if (matchRes != null) {
                val values = matchRes.groupValues 
                result = FloatArray(3) {
                    values[it + 1].toInt().toFloat() / 255f
                }
            }
            return result
        }
        /**
         * float array to rgb string
         */
        fun floatArrayToRgbStr(value: FloatArray): String? {
            var result: String? = null

            if (value.size > 2) {
                result = "rgb("
                result += "${floatToRgbInt(value[0])},"
                result += "${floatToRgbInt(value[1])},"
                result += "${floatToRgbInt(value[2])}"
                result += ")"
            }
            return result
        }
        /**
         * float value to rgb integer
         */
        fun floatToRgbInt(value: Float): Int {
            return (min(max(value, 0f), 1f) * 255f).roundToInt()
        } 
    }

    /**
     * setting
     */
    data class Option(val modalQuery: String,
        val canvasQuery: String,
        val okQuery: String,
        val itemsContainerQuery: String,
        val item0Query: String,
        val item1Query: String,
        val item3Query: String,
        val item4Query: String,
        val itemEnv0Query: String,
        val itemEnv1Query: String,
        val itemClassName: String)

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
     * called this method when modal is showing.
     */
    var modalShownHdlr: 
        ((event: JQueryEventObject, args: Any?)-> Any)? = null

    /**
     * color item click handler
     */
    var colorItemClickHdlr: ((JQueryEventObject, Any?)->Any)? = null


    /**
     * handle event form color picker user interface
     */
    var colorPickerHdlr: ((String, Any) -> Unit)? = null
    
    
 
    /**
     * color scheme
     */
    var colorScheme: ColorScheme?
        get() {
            var result : ColorScheme? = null
            val container = this.colorSchemeContainer
            if (container != null) {
                result = container.colorScheme
            }
            return result
        }
        set(value) {
            val container = this.colorSchemeContainer
            if (container != null) {
                if (value != null) {
                    container.colorScheme = value
                }
            }
        }

    /**
     * color scheme container
     */
    var colorSchemeContainer: ColorSchemeContainer? = null
    

    /**
     * color scheme which is kept in user interface
     */
    var colorSchemeUi: ColorScheme?
        get() {
            var result: ColorScheme? = null

            val colorScheme = this.colorScheme
            if (colorScheme != null) { 
                val color0 = color0Ui
                val color1 = color1Ui
                val color3 = color3Ui
                val color4 = color4Ui
                val colorEnv0 = colorEnv0Ui
                val colorEnv1 = colorEnv1Ui
                if (color0 != null
                    && color1 != null
                    && color3 != null
                    && color4 != null
                    && colorEnv0 != null
                    && colorEnv1 != null) {
                    result = ColorScheme(colorScheme)
                    result[0] = color0                   
                    result[1] = color1
                    result[3] = color3
                    result[4] = color4
                    result.setEnvironment(0, colorEnv0)
                    result.setEnvironment(1, colorEnv1)
                }
            }
            return result
        }
        set(value) {
            if (value != null) {
                color0Ui = value[0]
                color1Ui = value[1]
                color3Ui = value[3]
                color4Ui = value[4]
                colorEnv0Ui = value.getEnvironment(0)
                colorEnv1Ui = value.getEnvironment(1)
            }
        }
      
    /**
     * item 0 color value
     */
    var color0Ui: FloatArray?
        get() {
            val elem = colorItem0Ui 
            var result: FloatArray? = null
            if (elem != null) {
                val bgStr = jQuery(elem as Element).css("backgroundColor")
                result = rgbStrToFloatArray(bgStr)            
            }
            return result
        }
        set(value) {
            if (value != null) {
                val bgStr = floatArrayToRgbStr(value)
                if (bgStr != null) {
                    val elem = colorItem0Ui 
                    if (elem != null) {  
                        jQuery(elem as Element).css("backgroundColor", bgStr)
                    }
                }
            }
        } 
    /**
     * item 1 color value
     */
    var color1Ui: FloatArray?
        get() {
            val elem = colorItem1Ui 
            var result: FloatArray? = null
            if (elem != null) {
                val bgStr = jQuery(elem as Element).css("backgroundColor")
                result = rgbStrToFloatArray(bgStr)            
            }
            return result
        }
        set(value) {
            if (value != null) {
                val bgStr = floatArrayToRgbStr(value)
                if (bgStr != null) {
                    val elem = colorItem1Ui 
                    if (elem != null) {  
                        jQuery(elem as Element).css("backgroundColor", bgStr)
                    }
                }
            }
        } 

    /**
     * item 3 color value
     */
    var color3Ui: FloatArray?
        get() {
            val elem = colorItem3Ui 
            var result: FloatArray? = null
            if (elem != null) {
                val bgStr = jQuery(elem as Element).css("backgroundColor")
                result = rgbStrToFloatArray(bgStr)            
            }
            return result
        }
        set(value) {
            if (value != null) {
                val bgStr = floatArrayToRgbStr(value)
                if (bgStr != null) {
                    val elem = colorItem3Ui 
                    if (elem != null) {  
                        jQuery(elem as Element).css("backgroundColor", bgStr)
                    }
                }
            }
        } 

    /**
     * item 4 color value
     */
    var color4Ui: FloatArray?
        get() {
            val elem = colorItem4Ui 
            var result: FloatArray? = null
            if (elem != null) {
                val bgStr = jQuery(elem as Element).css("backgroundColor")
                result = rgbStrToFloatArray(bgStr)            
            }
            return result
        }
        set(value) {
            if (value != null) {
                val bgStr = floatArrayToRgbStr(value)
                if (bgStr != null) {
                    val elem = colorItem4Ui 
                    if (elem != null) {  
                        jQuery(elem as Element).css("backgroundColor", bgStr)
                    }
                }
            }
        } 

    /**
     * item 0 environment color value
     */
    var colorEnv0Ui: FloatArray?
        get() {
            val elem = colorItemEnv0Ui 
            var result: FloatArray? = null
            if (elem != null) {
                val bgStr = jQuery(elem as Element).css("backgroundColor")
                result = rgbStrToFloatArray(bgStr)            
            }
            return result
        }
        set(value) {
            if (value != null) {
                val bgStr = floatArrayToRgbStr(value)
                if (bgStr != null) {
                    val elem = colorItemEnv0Ui 
                    if (elem != null) {  
                        jQuery(elem as Element).css("backgroundColor", bgStr)
                    }
                }
            }
        } 

    /**
     * item 1 environment color value
     */
    var colorEnv1Ui: FloatArray?
        get() {
            val elem = colorItemEnv1Ui 
            var result: FloatArray? = null
            if (elem != null) {
                val bgStr = jQuery(elem as Element).css("backgroundColor")
                result = rgbStrToFloatArray(bgStr)            
            }
            return result
        }
        set(value) {
            if (value != null) {
                val bgStr = floatArrayToRgbStr(value)
                if (bgStr != null) {
                    val elem = colorItemEnv1Ui 
                    if (elem != null) {  
                        jQuery(elem as Element).css("backgroundColor", bgStr)
                    }
                }
            }
        } 

    /**
     * selected color scheme
     */
    var selectedSchemeColor: FloatArray?
        get() {
            val elem = selectedColorSchemeItem 
            var result: FloatArray? = null
            if (elem != null) {
                val bgStr = jQuery(elem as Element).css("backgroundColor")
                result = rgbStrToFloatArray(bgStr)            
            }
            return result
        }
        set(value) {
            if (value != null) {
                val bgStr = floatArrayToRgbStr(value)
                if (bgStr != null) {
                    val elem = selectedColorSchemeItem
                    if (elem != null) {  
                        jQuery(elem as Element).css("backgroundColor", bgStr)
                    }
                }
            }
        }

    /**
     * color item 0 user interface
     */
    val colorItem0Ui : HTMLElement?
        get() {
            return getColorItem(option.item0Query)
        }
    /**
     * color item 1 user interface
     */
    val colorItem1Ui : HTMLElement?
        get() {
            return getColorItem(option.item1Query)
        }
    /**
     * color item 3 user interface
     */
    val colorItem3Ui : HTMLElement?
        get() {
            return getColorItem(option.item3Query)
        }
    /**
     * color item 4 user interface
     */
    val colorItem4Ui : HTMLElement?
        get() {
            return getColorItem(option.item4Query)
        }
    /**
     * color item environment 0 user interface
     */
    val colorItemEnv0Ui : HTMLElement?
        get() {
            return getColorItem(option.itemEnv0Query)
        }
    /**
     * color item environment 1 user interface
     */
    val colorItemEnv1Ui : HTMLElement?
        get() {
            return getColorItem(option.itemEnv1Query)
        }

    /**
     * selected color item
     */
    val selectedColorSchemeItem: HTMLElement?
        get() {
            var result: HTMLElement? = null
            val schemeContainer = jQuery(
                selector = option.itemsContainerQuery, 
                context = jQuery(option.modalQuery))
            if (schemeContainer.length.toInt() > 0) {
                val scheme = jQuery(
                    selector = ".selected", 
                    context = schemeContainer)     
                if (scheme.length.toInt() > 0) {
                    result = scheme[0] as HTMLElement
                } 
            } 
            return result
        }




    /**
     * get color item
     */
    fun getColorItem(className: String): HTMLElement? {
        var result: HTMLElement? = null
        val modal = jQuery(option.modalQuery)
        if (modal.length.toInt() > 0) {
            val elem = jQuery(selector = className, context = modal.eq(0))
            if (elem.length.toInt() > 0) {
                result = elem[0] as HTMLElement
            }
        }

        return result 
    }


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
        colorPicker.template = null
        this.colorPicker = colorPicker
         
         
    }


    /**
     * this is called when modal dailog is showing
     */
    @Suppress("UNUSED_PARAMETER")
    fun onShownModal(event: JQueryEventObject, args: Any?): Any {
        
        syncCanvasWithClientSize(
            jQuery(option.canvasQuery)[0] as HTMLCanvasElement)

        this.colorPicker?.bind(jQuery(option.modalQuery)[0] as HTMLElement) 
        this.colorPicker?.addEventListener("pickerLocation", 
            colorPickerHdlr!!)
 
        this.colorSchemeUi = this.colorScheme 


        val colorItem = colorItem0Ui
        if (colorItem != null) {
            selectColorItem(colorItem)
        }
        return Unit
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
        modalShownHdlr = { e, arg -> onShownModal(e, arg) }
        okHdlr = { e, arg -> onOk(e, arg) }
        colorItemClickHdlr = { e, args -> onColorItemClick(e, args) }
        colorPickerHdlr = { t, _ -> onMarkColorChanged(t) }

        val jqModalQuery = jQuery(option.modalQuery)
        jQuery(option.okQuery).on("click", okHdlr!!)
        jqModalQuery.on("hidden.bs.modal", modalHiddenHdlr!!)
        jqModalQuery.on("shown.bs.modal", this.modalShownHdlr!!)
        jQuery(selector = option.itemsContainerQuery,
            context = jqModalQuery).on(
            "click", colorItemClickHdlr!!)
 
        setupContents()

    }

    /**
     * detach functions from modal user interface.
     */
    fun unbindModal() {
        teardownContents()

        val jqModalQuery = jQuery(option.modalQuery)
        if (modalHiddenHdlr != null) {
            jqModalQuery.off("hidden.bs.modal", modalHiddenHdlr!!)
            modalHiddenHdlr = null
        }
        if (modalShownHdlr != null) {
            jqModalQuery.off("shown.bs.modal", modalShownHdlr!!)
            modalShownHdlr = null
        }

        colorSchemeContainer = null

        if (okHdlr != null) {
            jQuery(option.okQuery).off("click", okHdlr!!)
            okHdlr = null
        }
        if (colorItemClickHdlr != null) {
            jQuery(selector = option.itemsContainerQuery, 
                context = jqModalQuery).off(
                "click", colorItemClickHdlr!!)
            colorItemClickHdlr = null
        }
        if (colorPickerHdlr != null) {
            val colorPicker = this.colorPicker  
            colorPicker?.removeEventListener("pickerLocation", 
                colorPickerHdlr!!)
            colorPickerHdlr = null
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
    @Suppress("UNUSED_PARAMETER")
    fun onModalHidden(e: JQueryEventObject, a: Any?) {
        unbindModal()
    }

    /**
     * handle the click event on color item
     */
    @Suppress("UNUSED_PARAMETER")
    fun onColorItemClick(e: JQueryEventObject, a: Any?) {
        val jqTarget = jQuery(e.target)
        if (jqTarget.hasClass(option.itemClassName)) {
            window.setTimeout({ 
                selectColorItem(e.target as HTMLElement) 
            })           
        } 
    }


    /**
     * handle picker marker changed event
     */
    fun onMarkColorChanged(kind: String) {
        if (kind == "pickerLocation") {
            window.setTimeout({ syncSeletectedSchemeColorWithColorPicker() })  
        }
    }

    /**
     * select color item
     */
    fun selectColorItem(colorItem: HTMLElement) {
        val jqColorItem = jQuery(colorItem)
        if (!jqColorItem.hasClass("selected")) {
            jqColorItem.siblings().removeClass("selected")
            jqColorItem.addClass("selected")
            window.setTimeout({ syncColorPickerWithSelectedSchemeColor() })  
        }
    }

    /**
     * synchronize color picker with selected scheme color
     */
    fun syncColorPickerWithSelectedSchemeColor() {
        val color = selectedSchemeColor
        val picker = colorPicker
        if (picker != null && color != null) {
            val color255 = Array<Number>(color.size) {
                color[it] * 255
            }
            val savedPickerHdlr = colorPickerHdlr
            picker.removeEventListener("pickerLocation", colorPickerHdlr!!)
            picker.markColor = color255
            picker.addEventListener("pickerLocation", savedPickerHdlr!!)
        }
    }

    /**
     * synchronize selected scheme color with color picker
     */
    fun syncSeletectedSchemeColorWithColorPicker() {
        val picker = colorPicker
        if (picker != null) {
            val color255 = picker.markColor
            if (color255 != null) {
                val color = FloatArray(color255.size) {
                    color255[it].toFloat() / 255f 
                }
                 
                selectedSchemeColor = color
            }
        }
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
