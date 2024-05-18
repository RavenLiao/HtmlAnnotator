package com.ravenl.htmlannotator.compose.ext.styler

import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.compose.styler.AnnotatedStyler

class UnorderedListStyler(start: Int, end: Int) :
    AnnotatedStyler(start, end) {
    override fun addStyle(builder: AnnotatedString.Builder) {
        builder.addStringAnnotation(TAG_NAME, "•", start, end)
    }

    companion object {
        const val TAG_NAME = "li-ul"
    }
}

class OrderedListStyler(private val index: Int, start: Int, end: Int) :
    AnnotatedStyler(start, end) {
    override fun addStyle(builder: AnnotatedString.Builder) {
        builder.addStringAnnotation(TAG_NAME, "$index.", start, end)
    }

    companion object {
        const val TAG_NAME = "li-ol"
    }
}