package net.ocsoft.mswp

import org.w3c.dom.css.CSSStyleSheet
import org.w3c.dom.css.CSSRule
import kotlinx.browser.document
import kotlin.text.Regex
import jQuery
import net.ocsoft.Color
import kotlin.collections.HashMap
import kotlin.collections.ArrayList

import css.CSSKeyframesRule
import css.CSSKeyframeRule

/**
 * manipulate user inteface for  menu, background
 */
class Environment(val option: Option,
    colorScheme: ColorScheme) {

    /**
     * option
     */
    data class Option(
        val mainPlaygroundQuery: String,
        val backgroundQuery: String,
        val menuTextQuery: String,
        val flagIconQuery: String,
        val flagsQuery: Array<String>,
        val flaggingKeyFrameName: String,
        val backToMainIconQuery: String)


    /**
     * color scheme
     */
    var colorScheme: ColorScheme = colorScheme
        set(value) {
            if (field != value) {
                field = value
                syncWithColorScheme()
            }
        }


    /**
     * synchronize environment with color scheme
     */
    fun syncWithColorScheme() {
        // syncMainPlaygroundWithColorScheme()
        // syncBackgroundWithColorScheme()
        syncMenuTextWithColorScheme()
        syncFlagsWithColorScheme()
        syncFlaggingAnimationWithColorScheme()
        syncBacktoMainWithColorScheme()
    }

    /**
     * synchronize main play-ground with color scheme
     */
    fun syncMainPlaygroundWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Background)
        
        jQuery(option.mainPlaygroundQuery).css(
            "background-color", ColorScheme.toHtmlRgb(color!!))

    }

    /**
     * synchronize background with color scheme
     */
    fun syncBackgroundWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Background)
        
        jQuery(option.backgroundQuery).css(
            "background-color", ColorScheme.toHtmlRgb(color!!))

    }

    /**
     * synchronize menu text with color scheme
     */
    fun syncMenuTextWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Foreground)
        
        jQuery(option.menuTextQuery).css(
            "color", ColorScheme.toHtmlRgb(color!!))

    }

    /**
     * synchronize flags with color scheme
     */
    fun syncFlagsWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Foreground)
        val rgbColor = ColorScheme.toHtmlRgb(color!!)
        option.flagsQuery.forEach {
            jQuery(it).css("color", rgbColor)
        }
     }

    /**
     * synchronize flagging animation with color scheme
     */  
    fun syncFlaggingAnimationWithColorScheme() {
        val keyframesRule = findFlaggingAnimationKeyframe()
        val colorPattern = Regex("color:(.+);") 
        val foreColor = colorScheme.getEnvironment(ColorScheme.Foreground)
 
        if (keyframesRule != null && foreColor != null) {
            val rules = keyframesRule.cssRules
            val keyCssTextMap = HashMap<String, String>()
            val keys = ArrayList<String>()
            for (i in 0 until rules.length) {
                val keyFrameRule = rules.item(i) as CSSKeyframeRule
                val cssRule = keyFrameRule as CSSRule  
                var cssText = keyFrameRule.cssText
                val matchList = colorPattern.findAll(cssText).toList()
                if (matchList.size > 0) {
                    for (i in 0 until matchList.size) {
                        val matchItem = matchList[matchList.size - i - 1]
                        val color = Color.parse(matchItem.groupValues[1]) 
                        if (color != null) {
                            val newColor = Color(
                                foreColor[0],
                                foreColor[1],
                                foreColor[2],
                                if (color.alpha != null) {
                                    color.alpha.floatValue
                                } else {
                                    null
                                })
                            cssText = cssText.replaceRange(
                                matchItem.range,
                                "color: ${newColor};") 
                        }
                    }
                }
                keyCssTextMap[keyFrameRule.keyText] = cssText
                keys.add(keyFrameRule.keyText)
            }
            keys.forEach {
                keyframesRule.deleteRule(it)
                keyframesRule.appendRule(keyCssTextMap[it]!!) 
            }
        }
    }

    /**
     * find main css sytle sheet
     */
    fun findMainCssStyleSheet(): CSSStyleSheet? {
        val styleSheets = document.styleSheets 
        val pattern = Regex("main-[0-9a-z]+.css")
        var result: CSSStyleSheet? = null 
        for (i in 0 until styleSheets.length) {
            val styleSheet = styleSheets.item(i)
            val href = styleSheet?.href
            if (href != null) {
                val matchRes = pattern.find(href)
                if (matchRes != null) {
                    if (styleSheet is CSSStyleSheet) {
                        result = styleSheet
                        break
                    }
                }
            }
        }
        return result
    }
    /**
     * find flagging animation keyframe
     */  
    fun findFlaggingAnimationKeyframe(): CSSKeyframesRule? {
        val styleSheet = findMainCssStyleSheet()
        var result: CSSKeyframesRule? = null
        val KEYFRAMES_RULE = 7.toShort()
        if (styleSheet != null) {
            val pattern = Regex(
                "@keyframes\\s+${option.flaggingKeyFrameName}")
            val rules = styleSheet.cssRules
            for (i in 0 until rules.length) {
                val rule = rules.item(i)
                if (rule?.type == KEYFRAMES_RULE) {
                    val ruleContent = rule.cssText
                    val matchRes = pattern.find(ruleContent)
                    if (matchRes != null) {
                        result = rule as CSSKeyframesRule   
                        break
                    }
                }
            }
        }
        return result
    }

    
    /**
     * synchronize icon to back to main with color scheme
     */
    fun syncBacktoMainWithColorScheme() {
        val colorScheme = this.colorScheme

        val color = colorScheme.getEnvironment(ColorScheme.Foreground)
        
        jQuery(option.backToMainIconQuery).css(
            "color", ColorScheme.toHtmlRgb(color!!))

    }
    
}

// vi: se ts=4 sw=4 et:
