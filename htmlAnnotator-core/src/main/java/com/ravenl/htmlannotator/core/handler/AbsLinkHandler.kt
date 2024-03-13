package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import org.jsoup.nodes.Node

abstract class AbsLinkHandler : TagHandler() {

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        rangeList.add(getUrlStyler(node.attr("href"), start, end))
    }

    abstract fun getUrlStyler(url: String, start: Int, end: Int): TagStyler
}