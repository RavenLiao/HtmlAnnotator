package com.ravenl.htmlannotator.core.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.TextStyler

abstract class AbsLinkHandler : TagHandler() {

    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        list.add(
            getUrlStyler(
                node.attr("src"),
                cssDeclarations
            )
        )
    }


    abstract fun getUrlStyler(url: String, cssDeclarations: List<CSSDeclaration>?): TextStyler
}