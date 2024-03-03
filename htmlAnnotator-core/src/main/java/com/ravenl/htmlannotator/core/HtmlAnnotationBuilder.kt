package com.ravenl.htmlannotator.core

import com.ravenl.htmlannotator.core.css.CSSStyleBlock
import com.ravenl.htmlannotator.core.css.parseCssDeclarations
import com.ravenl.htmlannotator.core.handler.TagHandler
import org.jsoup.nodes.Document
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.util.Stack


fun toHtmlAnnotation(
    doc: Document,
    handles: Map<String, TagHandler>
): HtmlAnnotation {
    val body = doc.body()
    val stringBuilder = StringBuilder()
    val rangeList = mutableListOf<TagStyler>()
    val cssStack = Stack<CSSStyleBlock>()
    applySpan(stringBuilder, rangeList, cssStack, handles, body)
    return HtmlAnnotation(stringBuilder.toString(), rangeList, cssStack)
}

private fun applySpan(
    builder: StringBuilder,
    rangeList: MutableList<TagStyler>,
    cssStack: Stack<CSSStyleBlock>,
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
                applySpan(builder, rangeList, cssStack, handles, childNode)
            }
        }
    }

    val lengthAfter = builder.length
    handler?.handleTagNode(builder, rangeList, node, lengthBefore, lengthAfter)

    node.attr("style").ifBlank { null }?.let { inlineCss ->
        inlineCss.let(::parseCssDeclarations)?.let {
            CSSStyleBlock(lengthBefore, builder.length, it)
        }
    }?.also { block ->
        cssStack.push(block)
    }
}

