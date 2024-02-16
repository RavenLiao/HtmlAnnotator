package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.util.appendNewLine
import org.jsoup.nodes.Node

class NewLineHandler(private val isStripExtraWhiteSpace: Boolean, private val amount: Int) :
    TagHandler() {
    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        node: Node,
        start: Int,
        end: Int
    ) {
        repeat(amount) {
            if (!builder.appendNewLine(isStripExtraWhiteSpace)) return@repeat
        }
    }
}