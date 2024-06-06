package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.util.appendNewLine
import com.fleeksoft.ksoup.nodes.Node

class AppendLinesHandler(private val isStripExtraWhiteSpace: Boolean, private val amount: Int) :
    TagHandler() {

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        repeat(amount) {
            if (!builder.appendNewLine(isStripExtraWhiteSpace)) return@repeat
        }
    }
}