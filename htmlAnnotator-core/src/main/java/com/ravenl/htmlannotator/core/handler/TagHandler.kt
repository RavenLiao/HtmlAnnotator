package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import org.jsoup.nodes.Node

abstract class TagHandler {

    open fun beforeChildren(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node
    ) {
    }

    open fun handlerRendersContent(): Boolean = false

    abstract fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    )

}