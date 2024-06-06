package com.ravenl.htmlannotator.compose.ext.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.compose.ext.styler.OrderedListStyler
import com.ravenl.htmlannotator.compose.ext.styler.UnorderedListStyler
import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.ListItemHandler

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
        rangeList.add(UnorderedListStyler(start, end))
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
        rangeList.add(OrderedListStyler(index, start, end))
    }

}