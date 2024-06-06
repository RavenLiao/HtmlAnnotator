package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.core.TextStyler

abstract class AnnotatedStyler(start: Int, end: Int) : TextStyler(start, end) {

    abstract fun addStyle(builder: AnnotatedString.Builder)
}