package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.UrlAnnotation

class LinkAnnotatedStyler(private val url: String) : IUrlAnnotationStyler {
    @OptIn(ExperimentalTextApi::class)
    override fun getUrlAnnotation(): UrlAnnotation = UrlAnnotation(url)
}