package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.core.TagStyler

abstract class AnnotatedTagStyler(start: Int, end: Int) : TagStyler(start, end) {

    abstract fun addStyle(builder: AnnotatedString.Builder)
}