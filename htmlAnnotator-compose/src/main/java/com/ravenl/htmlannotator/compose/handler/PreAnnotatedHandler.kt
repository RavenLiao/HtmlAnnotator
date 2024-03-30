package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import com.ravenl.htmlannotator.compose.styler.SpanTextStyler
import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.handler.AbsPreHandler
import org.jsoup.nodes.Node

class PreAnnotatedHandler(isStripExtraWhiteSpace: Boolean) : AbsPreHandler(isStripExtraWhiteSpace) {

    override fun getMonospaceStyler(node: Node, start: Int, end: Int): TextStyler =
        SpanTextStyler(
            start,
            end,
            SpanStyle(fontFamily = FontFamily.Monospace)
        )
}