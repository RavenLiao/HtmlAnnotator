package com.ravenl.htmlannotator.view.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.handler.AbsStyledTextHandler
import com.ravenl.htmlannotator.view.styler.SpannedStyler
import com.fleeksoft.ksoup.nodes.Node

class SingleSpanHandler(addNewLineAtBefore: Boolean = true, val newSpan: () -> Any) :
    AbsStyledTextHandler(addNewLineAtBefore) {

    private val span by lazy { newSpan() }

    override fun getTagStyler(node: Node, start: Int, end: Int): TextStyler =
        SpannedStyler(start, end, span)
}