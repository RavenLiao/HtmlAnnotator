package com.ravenl.htmlannotator.compose.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.ListItemHandler
import org.jsoup.nodes.Node

open class ListItemAnnotatedHandler : ListItemHandler() {
    override fun addUnorderedItem(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int,
        parent: Node
    ) {
        val text = "\u2022 "
        if (builder[start] == '\n') {
            builder.insert(start + 1, text)
        } else {
            builder.insert(start, text)
        }
    }

    override fun addOrderedItem(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int,
        parent: Node,
        index: Int
    ) {
        val text = "$index. "
        if (builder[start] == '\n') {
            builder.insert(start + 1, text)
        } else {
            builder.insert(start, text)
        }
    }

}