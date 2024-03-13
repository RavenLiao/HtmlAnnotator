package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import org.jsoup.nodes.Node

abstract class AbsStyledTextHandler(private val addNewLineAtBefore: Boolean = true) : TagHandler() {

    override fun beforeChildren(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node
    ) {
        if (addNewLineAtBefore && builder.isNotEmpty()) {
            if (builder[builder.length - 1] != '\n') {
                builder.append('\n')
            }
        }
    }

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        rangeList.add(getTagStyler(node, start, end))
    }

    abstract fun getTagStyler(node: Node, start: Int, end: Int): TagStyler

}