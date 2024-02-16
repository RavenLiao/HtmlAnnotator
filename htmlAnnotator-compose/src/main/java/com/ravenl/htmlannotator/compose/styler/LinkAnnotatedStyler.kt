package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.UrlAnnotation

class LinkAnnotatedStyler(private val url: String, start: Int, end: Int) :
    AnnotatedTagStyler(start, end) {

    override fun addStyle(builder: AnnotatedString.Builder) {
        @OptIn(ExperimentalTextApi::class)
        builder.addUrlAnnotation(UrlAnnotation(url), start, end)
    }
}