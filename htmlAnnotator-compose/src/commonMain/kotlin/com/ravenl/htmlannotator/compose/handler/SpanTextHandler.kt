package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.SpanStyle
import com.ravenl.htmlannotator.compose.styler.SpanTextStyler
import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.handler.AbsStyledTextHandler
import com.fleeksoft.ksoup.nodes.Node

class SpanTextHandler(addNewLineAtBefore: Boolean = true, val newSpanStyle: () -> SpanStyle) :
    AbsStyledTextHandler(addNewLineAtBefore) {

    private val spanStyle by lazy { newSpanStyle() }

    override fun getTagStyler(node: Node, start: Int, end: Int): TextStyler =
        SpanTextStyler(start, end, spanStyle)
}