package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import com.ravenl.htmlannotator.compose.styler.SpanStyleStyler
import com.ravenl.htmlannotator.core.handler.AbsPreHandler
import com.ravenl.htmlannotator.core.model.TextStyler

class PreAnnotatedHandler : AbsPreHandler() {
    override fun getMonospaceStyler(): TextStyler = SpanStyleStyler {
        SpanStyle(fontFamily = FontFamily.Monospace)
    }
}