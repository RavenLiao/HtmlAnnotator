package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.AnnotatedString

abstract class CSSAnnotatedHandler {
    abstract fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String)
}