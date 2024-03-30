package com.ravenl.htmlannotator.core

import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.css.model.CSSDeclarationWithPriority
import com.ravenl.htmlannotator.core.css.model.CSSRuleSet
import com.ravenl.htmlannotator.core.css.model.CSSStyleBlock
import com.ravenl.htmlannotator.core.css.model.StyleOrigin
import com.ravenl.htmlannotator.core.css.parseCssDeclarations
import com.ravenl.htmlannotator.core.css.parseCssRuleBlock
import com.ravenl.htmlannotator.core.handler.TagHandler
import com.ravenl.htmlannotator.core.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import java.util.Stack

private const val TAG = "HtmlAnnotationBuilder"

suspend fun toHtmlAnnotation(
    doc: Document,
    handles: Map<String, TagHandler>,
    logger: Logger,
    getExternalCSS: (suspend (link: String) -> String)?
): HtmlAnnotation = withContext(Dispatchers.Default) {
    val body = doc.body()
    yield()
    val stringBuilder = StringBuilder()
    val rangeList = mutableListOf<TextStyler>()
    val cssStack = Stack<CSSStyleBlock>()
    val internalCSS = buildInternalCSSBlock(doc)
    yield()
    val externalCss = getExternalCSS?.let { get ->
        buildExternalCSSBlock(get, doc)
    }
    yield()
    val cssMap = if (internalCSS != null || externalCss != null) {
        val map = mutableMapOf<Element, MutableList<CSSRuleSet>>()
        fun List<CSSRuleSet>.putToCssMap() {
            forEach { rule ->
                runCatching {
                    body.select(rule.selector).forEach { e ->
                        map.getOrPut(e) { mutableListOf() }.add(rule)
                    }
                }.onFailure {
                    logger.e(TAG, it) { "unsupported css selector: ${rule.selector}" }
                }
            }
        }
        internalCSS?.putToCssMap()
        externalCss?.putToCssMap()
        yield()
        map
    } else {
        null
    }

    suspend fun handleNode(node: Node) {
        yield()
        val name = node.nodeName()
        val handler = handles[name]
        if (handler == null && name != "body" && name != "#comment") {
            logger.w(TAG) {
                "unsupported node:${node.nodeName()}"
            }
        }
        val cssDeclarations = buildFinalCSS(cssMap, node)

        val lengthBefore = stringBuilder.length
        handler?.beforeChildren(stringBuilder, rangeList, cssDeclarations, node)

        if (handler?.handlerRendersContent() != true) {
            for (childNode in node.childNodes()) {
                if (childNode is TextNode) {
                    stringBuilder.append(childNode.text())
                } else {
                    handleNode(childNode)
                }
            }
        }

        val lengthAfter = stringBuilder.length
        handler?.handleTagNode(
            stringBuilder,
            rangeList,
            cssDeclarations,
            node,
            lengthBefore,
            lengthAfter
        )

        cssDeclarations?.also { list ->
            cssStack.push(CSSStyleBlock(lengthBefore, stringBuilder.length, list))
        }
    }

    handleNode(body)

    HtmlAnnotation(stringBuilder.toString(), rangeList, cssStack)
}

private suspend fun buildExternalCSSBlock(
    getExternalCSS: (suspend (link: String) -> String),
    doc: Document
): List<CSSRuleSet> = withContext(Dispatchers.Default) {
    doc.select("link[rel=stylesheet]").map { e ->
        async(Dispatchers.IO) {
            getExternalCSS(e.attr("href"))
        }
    }.awaitAll().joinToString("\n").let { css ->
        parseCssRuleBlock(StyleOrigin.EXTERNAL, css)
    }
}

private fun buildInternalCSSBlock(doc: Document): List<CSSRuleSet>? =
    parseCssRuleBlock(StyleOrigin.INTERNAL, doc.select("style").joinToString("\n") {
        it.html()
    }).ifEmpty { null }

private fun buildFinalCSS(
    cssMap: Map<Element, MutableList<CSSRuleSet>>?,
    node: Node
): List<CSSDeclaration>? {
    val noInlineCSS = cssMap?.get(node)
    val inlineCSS = node.attr("style").ifBlank { null }?.let(::parseCssDeclarations)

    if (inlineCSS == null && noInlineCSS == null) {
        return null
    } else if (inlineCSS != null && noInlineCSS == null) {
        return inlineCSS
    }

    val finalCssMap = mutableMapOf<String, CSSDeclarationWithPriority>()

    fun CSSDeclarationWithPriority.compareAndPutMap() {
        val mapValue = finalCssMap[property]
        if (mapValue == null || this >= mapValue) {
            finalCssMap[property] = this
        }
    }

    inlineCSS?.forEach { declaration ->
        CSSDeclarationWithPriority(declaration, StyleOrigin.INLINE).compareAndPutMap()
    }
    noInlineCSS?.mapIndexed { index, ruleSet ->
        ruleSet.declarations.forEach { declaration ->
            CSSDeclarationWithPriority(
                declaration,
                ruleSet.origin,
                ruleSet.selector,
                index
            ).compareAndPutMap()
        }
    }
    return finalCssMap.values.toList()
}

