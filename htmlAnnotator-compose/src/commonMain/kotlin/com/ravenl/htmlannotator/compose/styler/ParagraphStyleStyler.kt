package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.ParagraphStyle

class ParagraphStyleStyler(val getSpan: () -> ParagraphStyle) : IParagraphStyleStyler {
    override fun getParagraphStyle(): ParagraphStyle = getSpan()

}

