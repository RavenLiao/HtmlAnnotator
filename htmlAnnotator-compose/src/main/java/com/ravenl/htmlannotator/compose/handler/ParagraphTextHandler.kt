package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.ParagraphStyle
import com.ravenl.htmlannotator.compose.styler.ParagraphTextStyler
import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.handler.AbsStyledTextHandler
import org.jsoup.nodes.Node

class ParagraphTextHandler(val newStyle: () -> ParagraphStyle) : AbsStyledTextHandler() {

    private val paragraphStyle by lazy { newStyle() }

    override fun getTagStyler(node: Node, start: Int, end: Int): TagStyler =
        ParagraphTextStyler(start, end, paragraphStyle)
}