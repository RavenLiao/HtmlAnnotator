@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.ravenl.htmlannotator.view

import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.Spannable
import android.text.Spannable.SPAN_INCLUSIVE_EXCLUSIVE
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import android.text.style.LeadingMarginSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TypefaceSpan
import android.util.ArrayMap
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.ravenl.htmlannotator.core.handler.TagHandler
import com.ravenl.htmlannotator.core.model.HtmlNode
import com.ravenl.htmlannotator.core.model.StringNode
import com.ravenl.htmlannotator.core.model.StyleNode
import com.ravenl.htmlannotator.core.toHtmlNode
import com.ravenl.htmlannotator.core.util.Logger
import com.ravenl.htmlannotator.core.util.defaultLogger
import com.ravenl.htmlannotator.view.css.BackgroundColorCssSpannedHandler
import com.ravenl.htmlannotator.view.css.CSSSpannedHandler
import com.ravenl.htmlannotator.view.css.ColorCssSpannedHandler
import com.ravenl.htmlannotator.view.css.FontSizeCssSpannedHandler
import com.ravenl.htmlannotator.view.css.FontStyleCssSpannedHandler
import com.ravenl.htmlannotator.view.css.TextAlignCssSpannedHandler
import com.ravenl.htmlannotator.view.css.TextDecorationCssSpannedHandler
import com.ravenl.htmlannotator.view.css.TextIndentCssSpannedHandler
import com.ravenl.htmlannotator.view.handler.AppendLinesHandler
import com.ravenl.htmlannotator.view.handler.LinkSpannedHandler
import com.ravenl.htmlannotator.view.handler.ListItemSpannedHandler
import com.ravenl.htmlannotator.view.handler.MultipleSpanHandler
import com.ravenl.htmlannotator.view.handler.ParagraphHandler
import com.ravenl.htmlannotator.view.handler.PreSpannedHandler
import com.ravenl.htmlannotator.view.handler.SingleSpanHandler
import com.ravenl.htmlannotator.view.styler.IAfterChildrenSpannedStyler
import com.ravenl.htmlannotator.view.styler.IAtChildrenAfterSpannedStyler
import com.ravenl.htmlannotator.view.styler.IAtChildrenBeforeSpannedStyler
import com.ravenl.htmlannotator.view.styler.IBeforeChildrenSpannedStyler
import com.ravenl.htmlannotator.view.styler.ParagraphEndStyler
import com.ravenl.htmlannotator.view.styler.ParagraphStartStyler
import com.ravenl.htmlannotator.view.styler.SpanStyler
import com.ravenl.htmlannotator.view.styler.SpannedStyler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class HtmlSpanner(
    preTagHandlers: Map<String, TagHandler>? = defaultPreTagHandlers,
    preCSSHandlers: Map<String, CSSSpannedHandler>? = defaultPreCSSHandlers,
) {

    private val handlers: MutableMap<String, TagHandler>

    private val cssHandlers: MutableMap<String, CSSSpannedHandler>

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            handlers = ArrayMap()
            cssHandlers = ArrayMap()
        } else {
            handlers = HashMap()
            cssHandlers = HashMap()
        }

        registerBuiltInHandlers(preTagHandlers)
        registerBuiltInCssHandlers(preCSSHandlers)
    }

    fun registerHandler(tagName: String, handler: TagHandler) {
        handlers[tagName] = handler
    }

    fun unregisterHandler(tagName: String) {
        handlers.remove(tagName)
    }

    fun registerCssHandler(property: String, handler: CSSSpannedHandler) {
        cssHandlers[property] = handler
    }

    fun unregisterCssHandler(property: String) {
        cssHandlers.remove(property)
    }

    suspend fun from(
        html: String,
        baseUri: String = "",
        getExternalCSS: (suspend (link: String) -> String)? = null
    ): Spannable = from(Ksoup.parse(html, baseUri), getExternalCSS)

    suspend fun from(
        doc: Document,
        getExternalCSS: (suspend (link: String) -> String)? = null
    ): Spannable = withContext(Dispatchers.Default) {
        val root = toHtmlNode(doc, handlers, cssHandlers, logger, getExternalCSS)
        SpannableStringBuilder().apply {
            handleNodeParagraph(root)

            handleNode(root)
        }
    }

    private suspend fun handleNodeParagraph(node: HtmlNode) {
        yield()
        when (node) {
            is StringNode -> {
                return
            }
            is StyleNode -> {
                val children = node.children
                if (children.isNullOrEmpty()) {
                    return
                }

                for (i in children.indices) {
                    val currentNode = children[i]

                    if (currentNode !is StyleNode) continue
                    val currentStylers = currentNode.stylers ?: continue
                    val previousNode = if (i > 0) {
                        children[i - 1] as? StyleNode
                    } else {
                        null
                    }

                    fun isPreviousNodeHadEnd(extraLine: Boolean) =
                        previousNode?.stylers?.any { it is ParagraphEndStyler && it.extraLine == extraLine }

                    val isPreviousNodeHadEndWithExtraLine = isPreviousNodeHadEnd(true) == true
                    val isPreviousNodeHadEndNoExtraLine = isPreviousNodeHadEnd(false) == true

                    if (isPreviousNodeHadEndWithExtraLine
                        || isPreviousNodeHadEndNoExtraLine && currentStylers.any { it is ParagraphStartStyler && !it.extraLine }
                    ) {
                        currentStylers.removeAll { it is ParagraphStartStyler }
                    }
                    if (isPreviousNodeHadEndNoExtraLine) {
                        for (index in currentStylers.indices) {
                            val styler = currentStylers[index]
                            if (styler is ParagraphStartStyler && styler.extraLine) {
                                currentStylers[index] = ParagraphStartStyler(false)
                            }
                        }
                    }

                    // Recursively handle children of the current node
                    handleNodeParagraph(currentNode)
                }

            }
        }
    }


    private suspend fun SpannableStringBuilder.handleNode(node: HtmlNode) {
        yield()
        when (node) {
            is StringNode -> {
                append(node.string)
            }
            is StyleNode -> {
                handleStyleNode(node)
            }
        }
    }


    private suspend fun SpannableStringBuilder.handleStyleNode(
        node: StyleNode
    ) {
        suspend fun appendChildren() {
            node.children?.forEach { child ->
                handleNode(child)
            }
        }


        val stylers = node.stylers?.asSequence()?.filterIsInstance<SpannedStyler>()
        if (stylers == null) {
            appendChildren()
            return
        }

        stylers.filterIsInstance<IBeforeChildrenSpannedStyler>().forEach {
            it.beforeChildren(this)
        }


        val spanStylers = stylers.filterIsInstance<SpanStyler>().toList()
        val start = length

        stylers.filterIsInstance<IAtChildrenBeforeSpannedStyler>().forEach {
            it.atChildrenBefore(this)
        }

        appendChildren()

        stylers.filterIsInstance<IAtChildrenAfterSpannedStyler>().forEach {
            it.atChildrenAfter(this)
        }

        for (spanStyler in spanStylers) {
            setSpan(spanStyler.span, start, length, SPAN_INCLUSIVE_EXCLUSIVE)
        }

        stylers.filterIsInstance<IAfterChildrenSpannedStyler>().forEach {
            it.afterChildren(this)
        }
    }

    private fun registerBuiltInHandlers(pre: Map<String, TagHandler>?) {
        pre?.also { map ->
            handlers.putAll(map)
        }

        fun registerHandlerIfAbsent(tag: String, getHandler: () -> TagHandler) {
            if (pre?.containsKey(tag) != true) {
                registerHandler(tag, getHandler())
            }
        }

        val italicHandler by lazy {
            SingleSpanHandler(false) { StyleSpan(Typeface.ITALIC) }
        }

        registerHandlerIfAbsent("i") { italicHandler }
        registerHandlerIfAbsent("em") { italicHandler }
        registerHandlerIfAbsent("cite") { italicHandler }
        registerHandlerIfAbsent("dfn") { italicHandler }

        val boldHandler by lazy {
            SingleSpanHandler(false) { StyleSpan(Typeface.BOLD) }
        }

        registerHandlerIfAbsent("b") { boldHandler }
        registerHandlerIfAbsent("strong") { boldHandler }

        val marginHandler by lazy {
            SingleSpanHandler { LeadingMarginSpan.Standard(30) }
        }
        registerHandlerIfAbsent("blockquote") { marginHandler }
        registerHandlerIfAbsent("ul") { marginHandler }
        registerHandlerIfAbsent("ol") { marginHandler }

        registerHandlerIfAbsent("li") {
            ListItemSpannedHandler()
        }

        registerHandlerIfAbsent("br") { AppendLinesHandler(1) }


        registerHandlerIfAbsent("p") { ParagraphHandler(true) }
        registerHandlerIfAbsent("div") { ParagraphHandler(false) }


        registerHandlerIfAbsent("h1") {
            MultipleSpanHandler { listOf(StyleSpan(Typeface.BOLD), RelativeSizeSpan(2f)) }
        }
        registerHandlerIfAbsent("h2") {
            MultipleSpanHandler { listOf(StyleSpan(Typeface.BOLD), RelativeSizeSpan(1.5f)) }
        }
        registerHandlerIfAbsent("h3") {
            MultipleSpanHandler { listOf(StyleSpan(Typeface.BOLD), RelativeSizeSpan(1.17f)) }
        }
        registerHandlerIfAbsent("h4") {
            MultipleSpanHandler { listOf(StyleSpan(Typeface.BOLD), RelativeSizeSpan(1f)) }
        }
        registerHandlerIfAbsent("h5") {
            MultipleSpanHandler { listOf(StyleSpan(Typeface.BOLD), RelativeSizeSpan(0.83f)) }
        }
        registerHandlerIfAbsent("h6") {
            MultipleSpanHandler { listOf(StyleSpan(Typeface.BOLD), RelativeSizeSpan(0.67f)) }
        }


        registerHandlerIfAbsent("tt") { SingleSpanHandler { TypefaceSpan("monospace") } }


        registerHandlerIfAbsent("pre") { PreSpannedHandler() }


        registerHandlerIfAbsent("big") {
            MultipleSpanHandler(false) { listOf(StyleSpan(Typeface.BOLD), RelativeSizeSpan(1.25f)) }
        }

        registerHandlerIfAbsent("small") {
            MultipleSpanHandler(false) { listOf(StyleSpan(Typeface.BOLD), RelativeSizeSpan(0.8f)) }
        }


        registerHandlerIfAbsent("sub") {
            MultipleSpanHandler(false) { listOf(SubscriptSpan(), RelativeSizeSpan(0.7f)) }
        }

        registerHandlerIfAbsent("sup") {
            MultipleSpanHandler(false) { listOf(SuperscriptSpan(), RelativeSizeSpan(0.7f)) }
        }


        registerHandlerIfAbsent("center") {
            SingleSpanHandler { AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER) }
        }


        registerHandlerIfAbsent("a") { LinkSpannedHandler() }

        registerHandlerIfAbsent("span") { TagHandler() }
    }

    private fun registerBuiltInCssHandlers(pre: Map<String, CSSSpannedHandler>?) {
        pre?.also { map ->
            cssHandlers.putAll(map)
        }

        fun registerHandlerIfAbsent(tag: String, getHandler: () -> CSSSpannedHandler) {
            if (pre?.containsKey(tag) != true) {
                registerCssHandler(tag, getHandler())
            }
        }

        registerHandlerIfAbsent("text-align") { TextAlignCssSpannedHandler() }
        registerHandlerIfAbsent("font-size") { FontSizeCssSpannedHandler() }
        registerHandlerIfAbsent("font-style") { FontStyleCssSpannedHandler() }
        registerHandlerIfAbsent("color") { ColorCssSpannedHandler() }
        registerHandlerIfAbsent("background-color") { BackgroundColorCssSpannedHandler() }
        registerHandlerIfAbsent("text-indent") { TextIndentCssSpannedHandler() }
        registerHandlerIfAbsent("text-decoration") { TextDecorationCssSpannedHandler() }
    }

    companion object {
        private const val TAG = "HtmlSpanner"

        var logger: Logger = defaultLogger()
        var defaultPreTagHandlers: Map<String, TagHandler>? = null
        var defaultPreCSSHandlers: Map<String, CSSSpannedHandler>? = null
    }
}