package com.ravenl.htmlannotator.core

import com.ravenl.htmlannotator.core.css.model.CSSStyleBlock
import java.util.Stack

data class HtmlAnnotation(
    val htmlBody: String,
    val tagStylers: List<TextStyler>,
    val cssStack: Stack<CSSStyleBlock>
)