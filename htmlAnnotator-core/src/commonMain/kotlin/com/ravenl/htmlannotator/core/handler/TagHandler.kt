package com.ravenl.htmlannotator.core.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.HtmlNode
import com.ravenl.htmlannotator.core.model.TextStyler

open class TagHandler {

    open fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
    }


    open val handleChildrenNode: ((node: Node, cssDeclarations: List<CSSDeclaration>?) -> List<HtmlNode>)? =
        null

}