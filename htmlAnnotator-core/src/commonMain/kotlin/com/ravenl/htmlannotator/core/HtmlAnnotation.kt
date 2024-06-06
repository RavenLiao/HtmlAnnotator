package com.ravenl.htmlannotator.core

import com.ravenl.htmlannotator.core.css.model.CSSStyleBlock

data class HtmlAnnotation(
    val htmlBody: String,
    val tagStylers: List<TextStyler>,
    val cssStack: ArrayDeque<CSSStyleBlock>
)