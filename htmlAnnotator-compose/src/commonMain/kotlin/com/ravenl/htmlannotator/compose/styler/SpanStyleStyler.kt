package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.SpanStyle

class SpanStyleStyler(val getSpan: () -> SpanStyle) : ISpanStyleStyler {
    override fun getSpanStyler(): SpanStyle = getSpan()
}