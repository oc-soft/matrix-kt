package css

import org.w3c.dom.css.CSSRule
import org.w3c.dom.css.CSSRuleList

external abstract class CSSKeyframesRule: CSSRule {


    val name: String

    val cssRules: CSSRuleList
     
    fun appendRule(rule: String)

    fun deleteRule(select: String)

    fun findRule(select: String): CSSKeyframeRule?
}

// vi: se ts=4 sw=4 et:
