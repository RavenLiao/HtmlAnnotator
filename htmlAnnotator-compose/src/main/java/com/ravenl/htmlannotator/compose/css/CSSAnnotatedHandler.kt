package com.ravenl.htmlannotator.compose.css

import com.ravenl.htmlannotator.compose.styler.AnnotatedStyler

abstract class CSSAnnotatedHandler {
    abstract fun addCssStyler(rangeList: MutableList<AnnotatedStyler>, start: Int, end: Int, value: String)
}