package com.ravenl.htmlannotator.core

import com.ravenl.htmlannotator.core.css.CSSStyleBlock
import java.util.Stack

data class HtmlAnnotation(
    val htmlBody: String,
    val rangeList: List<TagStyler>,
    val cssStack: Stack<CSSStyleBlock>
)