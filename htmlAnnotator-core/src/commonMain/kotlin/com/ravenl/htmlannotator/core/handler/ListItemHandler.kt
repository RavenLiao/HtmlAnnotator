package com.ravenl.htmlannotator.core.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.TextStyler

abstract class ListItemHandler : TagHandler() {

    private fun getMyIndex(node: Node): Int? {
        val parent = node.parent() ?: return null
        var i = 1
        for (child in parent.childNodes()) {
            if (child === node) {
                return i
            }
            if ("li" == child.nodeName()) {
                i++
            }
        }
        return null
    }

    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        val parent = node.parent() ?: return
        when (parent.nodeName()) {
            "ul" -> {
                addUnorderedItem(list, node, cssDeclarations, parent)
            }
            "ol" -> {
                val index = getMyIndex(node) ?: return
                addOrderedItem(list, node, cssDeclarations, parent, index)
            }
        }
    }

    abstract fun addUnorderedItem(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?,
        parent: Node
    )

    abstract fun addOrderedItem(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?,
        parent: Node,
        index: Int
    )
}