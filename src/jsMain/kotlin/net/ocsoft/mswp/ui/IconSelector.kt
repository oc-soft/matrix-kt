package net.ocsoft.mswp.ui

import kotlin.math.min
import jQuery
import JQueryEventObject
import JQueryStatic
import JQuery
import fontawesome.Icons

import kotlin.collections.List

/**
 * icon selector
 */
class IconSelector(val option : Option) {
    /**
     * option 
     */
    data class Option(val modalQuery: String, 
        val itemListQuery: String,
        val itemsQuery: String,
        val itemTemplateQuery: String,
        val blankItemTemplateQuery: String,
        val pagingContainerQuery: String,
        val pagingCtrlFullTemplateQuery: String,
        val pagingCtrlMediumTemplateQuery: String,
        val pagingCtrlSimpleTemplateQuery: String,
        val pagingItemTemplateQuery: String)

    /**
     * icon page contents meta infomation
     */
    data class ContentsMeta(
        val colSize: Int,
        val rowSize: Int,
        val iconIdentifiers: List<Icons.Identifier>)
    

    /**
     * runtime configuration
     */
    var runtimeConfig: dynamic? = null

    /**
     * contents meta information
     */
    var contentsMeta: ContentsMeta? = null

    /**
     * count of icon items in page
     */
    val countOfIconItemsInPage: Int
        get() {
            val items = jQuery(option.itemsQuery)
            return items.length.toInt()
        }

    /**
     * bind into html
     */
    fun bind() {
    }

    /**
     * unbind from html
     */
    fun unbind() {
    }


    /**
     * visible modal dialog
     */
    fun show() {
        setupContents()
        jQuery(option.modalQuery).asDynamic().modal() 
        // destroyContents()
    }
    /**
     * set up user interface contents
     */
    fun setupContentsI() {
 
    }

    /**
     * set up user interface contents
     */
    fun setupContents() {
        var ids = Icons.getAllIdentifiersCodeOrder() 
        var tableSize = findTableSize()
        if (tableSize != null) {
            var (colSize, rowSize) = tableSize
            if (colSize != null && rowSize != null) {
                contentsMeta = ContentsMeta(colSize, rowSize, ids)
                updatePage(0, contentsMeta!!)
                setupPagination()
            }
        }
    }
    

    /**
     * setup pagination ui
     */
    fun setupPagination() {
        var ctMeta = contentsMeta
        if (ctMeta != null) {
            val pageCount = calcCountOfPages(ctMeta)       
            if (pageCount > 1) {
                val pagingSize = findPagingSize()
                if (pagingSize != null) {
                    var templateQuery = option.pagingCtrlSimpleTemplateQuery

                    if (pageCount > pagingSize) {
                        val pagingCount = pageCount / pagingSize
                        if (pagingCount > 2) {
                            templateQuery = option.pagingCtrlFullTemplateQuery 
                        } else {
                            templateQuery = 
                                option.pagingCtrlMediumTemplateQuery 
                        } 
                    } 
                    setupPagingCtrl(templateQuery, Pair(0, pagingSize))
                } 
            }
        } 
    }

    /**
     * update pagination ui
     */
    fun updatePagination(
        pageIndex: Int) {
    }

    /**
     * create paging control
     */
    fun setupPagingCtrl(templateQuery: String,
        page: Pair<Int, Int>) {
        val (startIndex, size) = page
        
        val pagingCtrlStr = jQuery(templateQuery).html()
        val pagingCtrl = jQuery(pagingCtrlStr)
        val insertEndIdx = pagingCtrl.children().length.toInt() / 2 
        for (i in startIndex..startIndex + size - 1) {
            val pagingItemStr = jQuery(option.pagingItemTemplateQuery).html()
            val pagingItem = jQuery(pagingItemStr)
            pagingItem.html("${i + 1}")
            val insertIdx = pagingCtrl.children().length.toInt() - insertEndIdx
            pagingCtrl.children().eq(insertIdx).before(pagingItem) 
        }
        val pagingContainer = jQuery(option.pagingContainerQuery)
        if (pagingContainer.children().length.toInt() > 0) {
            pagingContainer.children().eq(0).replaceWith(pagingCtrl)
        } else {
            pagingContainer.append(pagingCtrl)
        }
    }

    
    /**
     * update icon  page
     */
    fun updatePage(pageIndex : Int,
        contentsMeta: ContentsMeta,
        selectionInPage: Int? = null) {
        val ids = contentsMeta.iconIdentifiers
        val iconCount = contentsMeta.colSize * contentsMeta.rowSize
        val pageCount = (ids.size + iconCount - 1) / iconCount
       
        while (countOfIconItemsInPage > iconCount) {
            removeItem(countOfIconItemsInPage - 1)
        } 

        if (0 <= pageIndex && pageIndex < pageCount) {
            val iconStart = iconCount * pageIndex
            val iconEnd = (iconCount * (pageIndex + 1)) - 1
            for (i in  iconStart..iconEnd) {  
                var iconElem: Icons.Identifier? = null
                var idxInPage = i - iconStart
                if (i < ids.size) {
                    iconElem = ids[i]
                }
                replaceItem(iconElem, idxInPage)
            }
        }
    } 
    /**
     * calculate count of pages
     */
    fun calcCountOfPages(contentsMeta: ContentsMeta): Int {
        val ids = contentsMeta.iconIdentifiers
        val iconCount = contentsMeta.colSize * contentsMeta.rowSize
        val result = (ids.size + iconCount - 1) / iconCount
        return result
    }
    /**
     * tear down user interface contents
     */
    fun destroyContents() {
        val items = jQuery(option.itemListQuery)
        items.empty()
        val pagingContainer = jQuery(option.pagingContainerQuery)
        pagingContainer.empty() 
    } 

    /**
     * find table size from runtime configuration
     */
    fun findTableSize(): Pair<Int?, Int?>? {
        var result : Pair<Int?, Int?>? = null
        val rc = runtimeConfig["icon_list"]
        if (rc != null) {
            var rowCount : Int? = null 
            if (rc.rowSize != null) {
                rowCount = Responsive.findRowCount(
                    rc.rowSize,
                    "minHeight", "maxHeight")
            }
            if (rowCount == null) {
                rowCount = rc.rowSizeDefault
            }

            var colCount : Int? = null
            if (rc.colSize != null) {
                colCount = Responsive.findRowCount(
                    rc.ColSize,
                    "minWidth", "maxWidth") 
            }
            if (colCount == null) {
                colCount = rc.colSizeDefault
            }
            result = Pair(colCount, rowCount)
        } 

        return result 
    }

    /**
     * find paging size from runtime configuration
     */
    fun findPagingSize(): Int? {
        var result : Int? = null
        val rc = runtimeConfig["icon_list"]
        if (rc != null) {
            if (rc.pagingSize != null) {
                result = Responsive.findXCount(
                    rc.pagingSize,
                    "minWidth", "maxWidth")
            }
            if (result == null) {
                result = rc.pagingSizeDefault
            }
        }
        return result
    }

 
    /**
     * append item
     */
    fun appendItem(item : Icons.Identifier?) {
        replaceItem(item, countOfIconItemsInPage)
    }
    /**
     * replace item
     */
    fun replaceItem(item: Icons.Identifier?,
        index: Int) {
        val items = jQuery(option.itemsQuery)
        if (0 <= index && index <= items.length.toInt()) {
            var newNode : JQuery? = null
            if (item != null) {
                val newLine = jQuery(option.itemTemplateQuery).html()
                newNode = jQuery(newLine) 
                val divNode = jQuery("div", newNode as JQueryStatic)
                val iNode = jQuery("i", divNode as JQueryStatic)
                iNode.addClass("${item.prefix} fa-${item.name}")
            } else {
                val newLine = jQuery(option.blankItemTemplateQuery).html()
                newNode = jQuery(newLine) 
            }
            if (index < items.length.toInt()) {
                items.eq(index).replaceWith(newNode) 
            } else {
                jQuery(option.itemListQuery).append(newNode)
            }
        }
    }
    /**
     * remove item from icon list
     */
    fun removeItem(index: Int) {
        val items = jQuery(option.itemsQuery)
        items.eq(index).remove()    
    }
}

