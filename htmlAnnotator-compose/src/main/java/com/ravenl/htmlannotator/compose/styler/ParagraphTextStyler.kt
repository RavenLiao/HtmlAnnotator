package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle

class ParagraphTextStyler(start: Int, end: Int, private val paragraphStyle: ParagraphStyle) :
    AnnotatedTagStyler(start, end) {

    override fun addStyle(builder: AnnotatedString.Builder) {
        builder.addStyle(paragraphStyle, start, end)
    }
}