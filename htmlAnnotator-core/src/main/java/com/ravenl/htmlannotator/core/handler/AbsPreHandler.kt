package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.util.appendNewLine
import org.jsoup.nodes.Node

abstract class AbsPreHandler(private val isStripExtraWhiteSpace: Boolean) : ParagraphHandler() {

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
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

    abstract fun getMonospaceStyler(node: Node, start: Int, end: Int): TagStyler

    override fun handlerRendersContent(): Boolean = true
}