package com.ravenl.htmlannotator.core

import com.ravenl.htmlannotator.core.handler.TagHandler
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode


fun toHtmlAnnotation(
    doc: Document,
    handles: Map<String, TagHandler>
): HtmlAnnotation {
    val body = doc.body()
    val stringBuilder = StringBuilder()
    val rangeList = mutableListOf<TagStyler>()
    applySpan(stringBuilder, rangeList, handles, body)
    return HtmlAnnotation(stringBuilder.toString(), rangeList)
}

private fun applySpan(
    builder: StringBuilder,
    rangeList: MutableList<TagStyler>,
    handles: Map<String, TagHandler>,
    node: Node
) {
    val handler = handles[node.nodeName()]

    val lengthBefore = builder.length
    handler?.beforeChildren(builder, rangeList, node)

    if (handler?.handlerRendersContent() != true) {
        for (childNode in node.childNodes()) {
            if (childNode is TextNode) {
                builder.append(childNode.text())
            } else {
                applySpan(builder, rangeList, handles, childNode)
            }
        }
    }

    val lengthAfter = builder.length
    handler?.handleTagNode(builder, rangeList, node, lengthBefore, lengthAfter)
}

