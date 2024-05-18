package com.ravenl.htmlannotator.view.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.ListItemHandler
import com.ravenl.htmlannotator.view.span.OrderedListItemSpan
import com.ravenl.htmlannotator.view.span.UnorderedListItemSpan
import com.ravenl.htmlannotator.view.styler.SpannedStyler
import org.jsoup.nodes.Node

open class ListItemSpannedHandler : ListItemHandler() {
    override fun beforeChildren(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node
    ) {
        if (builder.isNotEmpty()) {
            if (builder[builder.length - 1] != '\n') {
                builder.append('\n')
            }
        }
    }

    override fun addUnorderedItem(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int,
        parent: Node
    ) {
        rangeList.add(SpannedStyler(start, end, UnorderedListItemSpan()))
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
        rangeList.add(SpannedStyler(start, end, OrderedListItemSpan(index)))
    }

}