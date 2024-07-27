package com.ravenl.htmlannotator.view.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.ListItemHandler
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.span.OrderedListItemSpan
import com.ravenl.htmlannotator.view.span.UnorderedListItemSpan
import com.ravenl.htmlannotator.view.styler.NewLineStyler
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class ListItemSpannedHandler : ListItemHandler() {

    override fun addUnorderedItem(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?,
        parent: Node
    ) {
        list.add(NewLineStyler)
        list.add(SpanStyler(UnorderedListItemSpan()))
    }

    override fun addOrderedItem(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?,
        parent: Node,
        index: Int
    ) {
        list.add(NewLineStyler)
        list.add(SpanStyler(OrderedListItemSpan(index)))
    }
}