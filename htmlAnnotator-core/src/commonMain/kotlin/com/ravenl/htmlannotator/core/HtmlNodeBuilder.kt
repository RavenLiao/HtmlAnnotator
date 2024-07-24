package com.ravenl.htmlannotator.core

import com.fleeksoft.ksoup.nodes.Document
import com.fleeksoft.ksoup.nodes.Element
import com.fleeksoft.ksoup.nodes.Node
import com.fleeksoft.ksoup.nodes.TextNode
import com.ravenl.htmlannotator.core.css.CSSHandler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.css.model.CSSDeclarationWithPriority
import com.ravenl.htmlannotator.core.css.model.CSSRuleSet
import com.ravenl.htmlannotator.core.css.model.StyleOrigin
import com.ravenl.htmlannotator.core.css.parseCssDeclarations
import com.ravenl.htmlannotator.core.css.parseCssRuleBlock
import com.ravenl.htmlannotator.core.handler.TagHandler
import com.ravenl.htmlannotator.core.model.CumulativeStyler
import com.ravenl.htmlannotator.core.model.HtmlNode
import com.ravenl.htmlannotator.core.model.StringNode
import com.ravenl.htmlannotator.core.model.StyleNode
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.core.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

private const val TAG = "HtmlAnnotationBuilder"

suspend fun toHtmlNode(
    doc: Document,
    tagHandles: Map<String, TagHandler>,
    cssHandles: Map<String, CSSHandler>,
    logger: Logger,
    getExternalCSS: (suspend (link: String) -> String)?
): HtmlNode = withContext(Dispatchers.Default) {
    val body = doc.body()
    yield()
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

    suspend fun handleNode(node: Node, parentCumulativeStyler: List<CumulativeStyler>?): HtmlNode {
        yield()
        if (node is TextNode) {
            return StringNode(node.text())
        }

        val name = node.nodeName()
        val handler = tagHandles[name]
        if (handler == null && name != "body" && name != "#comment") {
            logger.w(TAG) {
                "unsupported node:${node.nodeName()}"
            }
        }
        val cssDeclarations = buildFinalCSS(cssMap, node)

        val stylers = ArrayList<TextStyler>().apply {
            handler?.run {
                addTagStylers(this@apply, node, cssDeclarations)
            }
            cssDeclarations?.let { list ->
                list.forEach { css ->
                    cssHandles[css.property]?.run {
                        addStyle(this@apply, css.value)
                    }
                }
            }
        }.ifEmpty { null }

        if (parentCumulativeStyler != null) {
            if (stylers != null) {
                for (index in stylers.indices) {
                    val styler = stylers[index]
                    if (styler is CumulativeStyler) {
                        parentCumulativeStyler
                            .firstOrNull { it.name == styler.name }?.also { parent ->
                                stylers[index] = styler.buildCumulative(parent.value)
                            }
                    }
                }
            }
        }

        val children = ArrayList<HtmlNode>().apply {
            handler?.handleChildrenNode.let { handle ->
                if (handle != null) {
                    handle(node, cssDeclarations)
                } else {
                    val cumulativeStyler =
                        stylers?.filterIsInstance<CumulativeStyler>()?.ifEmpty { null }
                    for (childNode in node.childNodes()) {
                        if (childNode is TextNode) {
                            childNode.text().trim().ifBlank {
                                null
                            }?.let(::StringNode)?.also(::add)
                        } else {
                            add(handleNode(childNode, cumulativeStyler))
                        }
                    }
                }
            }
        }


        return StyleNode(stylers, children)
    }

    handleNode(body, null)
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

