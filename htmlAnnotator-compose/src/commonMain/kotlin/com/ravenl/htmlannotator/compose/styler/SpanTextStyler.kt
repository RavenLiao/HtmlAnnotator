package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle

class SpanTextStyler(start: Int, end: Int, private val spanStyle: SpanStyle) :
    AnnotatedStyler(start, end) {

    override fun addStyle(builder: AnnotatedString.Builder) {
        builder.addStyle(spanStyle, start, end)
    }
}