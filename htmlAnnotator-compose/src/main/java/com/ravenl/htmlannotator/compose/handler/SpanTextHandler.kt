package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.SpanStyle
import com.ravenl.htmlannotator.compose.styler.SpanTextStyler
import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.handler.AbsStyledTextHandler
import org.jsoup.nodes.Node

class SpanTextHandler(addNewLineAtBefore: Boolean = true, val newSpanStyle: () -> SpanStyle) :
    AbsStyledTextHandler(addNewLineAtBefore) {

    private val spanStyle by lazy { newSpanStyle() }

    override fun getTagStyler(node: Node, start: Int, end: Int): TagStyler =
        SpanTextStyler(start, end, spanStyle)
}