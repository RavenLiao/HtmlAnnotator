package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import org.jsoup.nodes.Node

open class ParagraphHandler : TagHandler() {
    override fun beforeChildren(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node
    ) {
        if (builder.isNotEmpty()) {
            if (builder[builder.length - 1] != '\n') {
                builder.append('\n')
            }
        }
    }

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
    }
}