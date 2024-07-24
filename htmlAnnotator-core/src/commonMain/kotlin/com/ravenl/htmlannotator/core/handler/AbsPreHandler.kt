package com.ravenl.htmlannotator.core.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.HtmlNode
import com.ravenl.htmlannotator.core.model.StringNode
import com.ravenl.htmlannotator.core.model.StyleNode
import com.ravenl.htmlannotator.core.model.TextStyler

abstract class AbsPreHandler : TagHandler() {

    override val handleChildrenNode: ((node: Node, cssDeclarations: List<CSSDeclaration>?) -> List<HtmlNode>)? =
        { node, _ ->
            val nodeLength = node.nodeName().length
            val contentHtml = buildString {
                node.outerHtml().let { html ->
                    html.substring(nodeLength + 2, html.length - nodeLength - 3)
                }.also(::append)
                repeat(2) {
                    append('\n')
                }
            }

            listOf(
                StyleNode(
                    mutableListOf(getMonospaceStyler()),
                    mutableListOf(StringNode(contentHtml))
                )
            )
        }


    abstract fun getMonospaceStyler(): TextStyler
}