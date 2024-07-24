package com.ravenl.htmlannotator.compose.ext.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.compose.ext.styler.OrderedListStyler
import com.ravenl.htmlannotator.compose.ext.styler.UnorderedListStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.ListItemHandler
import com.ravenl.htmlannotator.core.model.TextStyler

open class ListItemAnnotatedHandler : ListItemHandler() {
    override fun addUnorderedItem(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?,
        parent: Node
    ) {
        list.add(UnorderedListStyler())
    }

    override fun addOrderedItem(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?,
        parent: Node,
        index: Int
    ) {
        list.add(OrderedListStyler(index))
    }
}