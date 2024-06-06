package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.fleeksoft.ksoup.nodes.Node

abstract class AbsStyledTextHandler(addNewLineAtBefore: Boolean = true) :
    NewLineHandler(addNewLineAtBefore) {

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        rangeList.add(getTagStyler(node, start, end))
    }

    abstract fun getTagStyler(node: Node, start: Int, end: Int): TextStyler

}