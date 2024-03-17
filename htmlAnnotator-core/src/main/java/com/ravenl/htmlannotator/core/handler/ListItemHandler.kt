package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import org.jsoup.nodes.Node

open class ListItemHandler : TagHandler() {

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
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node
    ) {
        if (builder.isNotEmpty()) {
            if (builder[builder.length - 1] != '\n') {
                builder.append('\n')
            }
        }
        when (node.parent()?.nodeName()) {
            "ul" -> {
                builder.append("\u2022 ")
            }
            "ol" -> {
                val index = getMyIndex(node) ?: return
                builder.append(index)
                builder.append(". ")
            }
        }
    }
}