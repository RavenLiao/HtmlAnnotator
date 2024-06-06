package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.util.appendNewLine
import com.fleeksoft.ksoup.nodes.Node

abstract class AbsPreHandler(private val isStripExtraWhiteSpace: Boolean) : ParagraphHandler() {

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        val nodeLength = node.nodeName().length
        val contentHtml = node.outerHtml().run {
            //去除最外层标签
            substring(nodeLength + 2, length - nodeLength - 3)
        }
        builder.append(contentHtml)
        rangeList.add(getMonospaceStyler(node, end, end + contentHtml.length))

        repeat(2) {
            builder.appendNewLine(isStripExtraWhiteSpace)
        }
    }

    abstract fun getMonospaceStyler(node: Node, start: Int, end: Int): TextStyler

    override fun handlerRendersContent(): Boolean = true
}