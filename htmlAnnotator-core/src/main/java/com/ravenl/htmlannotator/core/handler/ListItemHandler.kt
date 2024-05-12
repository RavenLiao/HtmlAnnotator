package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import org.jsoup.nodes.Node

abstract class ListItemHandler : TagHandler() {

    private fun getMyIndex(node: Node): Int? {
        val parent = node.parent() ?: return null
        var i = 1
        for (child in parent.childNodes()) {
            if (child === node) {
                return i
            }
            if (child is Node) {
                if ("li" == child.nodeName()) {
                    i++
                }
            }
        }
        return null
    }

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

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        val parent = node.parent() ?: return
        when (parent.nodeName()) {
            "ul" -> {
                addUnorderedItem(builder, rangeList, cssDeclarations, node, start, end, parent)
            }
            "ol" -> {
                val index = getMyIndex(node) ?: return
                addOrderedItem(builder, rangeList, cssDeclarations, node, start, end, parent, index)
            }
        }
    }

    abstract fun addUnorderedItem(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int,
        parent: Node
    )

    abstract fun addOrderedItem(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int,
        parent: Node,
        index: Int
    )
}