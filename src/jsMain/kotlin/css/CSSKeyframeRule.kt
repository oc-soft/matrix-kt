package css

import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.css.CSSRule

/**
 * key frame rule
 */
external abstract class CSSKeyframeRule: CSSRule {
 
    val keyText: String

    val style: CSSStyleDeclaration
    
}

// vi: se ts=4 sw=4 et:
