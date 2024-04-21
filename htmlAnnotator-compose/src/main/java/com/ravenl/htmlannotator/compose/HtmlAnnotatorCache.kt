package com.ravenl.htmlannotator.compose

import androidx.compose.ui.text.AnnotatedString

interface HtmlAnnotatorCache {
    fun put(src: String, result: AnnotatedString)
    fun get(src: String): AnnotatedString?
}