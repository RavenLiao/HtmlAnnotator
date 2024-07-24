@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.ravenl.htmlannotator.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Document
import com.ravenl.htmlannotator.compose.css.BackgroundColorCssAnnotatedHandler
import com.ravenl.htmlannotator.compose.css.CSSAnnotatedHandler
import com.ravenl.htmlannotator.compose.css.ColorCssAnnotatedHandler
import com.ravenl.htmlannotator.compose.css.FontSizeCssAnnotatedHandler
import com.ravenl.htmlannotator.compose.css.FontStyleCssAnnotatedHandler
import com.ravenl.htmlannotator.compose.css.FontWeightCssAnnotatedHandler
import com.ravenl.htmlannotator.compose.css.TextAlignCssAnnotatedHandler
import com.ravenl.htmlannotator.compose.css.TextDecorationCssAnnotatedHandler
import com.ravenl.htmlannotator.compose.css.TextIndentCssAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.AppendLinesHandler
import com.ravenl.htmlannotator.compose.handler.ImageAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.LinkAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.ParagraphHandler
import com.ravenl.htmlannotator.compose.handler.ParagraphStyleHandler
import com.ravenl.htmlannotator.compose.handler.PreAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.SpanStyleHandler
import com.ravenl.htmlannotator.compose.styler.IAfterChildrenAnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.IAtChildrenAfterAnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.IAtChildrenBeforeAnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.IBeforeChildrenAnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.IParagraphStyleStyler
import com.ravenl.htmlannotator.compose.styler.ISpanStyleStyler
import com.ravenl.htmlannotator.compose.styler.IStringAnnotationStyler
import com.ravenl.htmlannotator.compose.styler.IUrlAnnotationStyler
import com.ravenl.htmlannotator.compose.styler.ParagraphEndStyler
import com.ravenl.htmlannotator.compose.styler.ParagraphStartStyler
import com.ravenl.htmlannotator.compose.util.ParagraphInterval
import com.ravenl.htmlannotator.compose.util.buildNotOverlapList
import com.ravenl.htmlannotator.core.handler.TagHandler
import com.ravenl.htmlannotator.core.model.HtmlNode
import com.ravenl.htmlannotator.core.model.StringNode
import com.ravenl.htmlannotator.core.model.StyleNode
import com.ravenl.htmlannotator.core.toHtmlNode
import com.ravenl.htmlannotator.core.util.Logger
import com.ravenl.htmlannotator.core.util.defaultLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class HtmlAnnotator(
    preTagHandlers: Map<String, TagHandler>? = defaultPreTagHandlers,
    preCSSHandlers: Map<String, CSSAnnotatedHandler>? = defaultPreCSSHandlers,
) {

    private val handlers = HashMap<String, TagHandler>()

    private val cssHandlers = HashMap<String, CSSAnnotatedHandler>()

    init {
        registerBuiltInHandlers(preTagHandlers)
        registerBuiltInCssHandlers(preCSSHandlers)
    }

    fun registerHandler(tagName: String, handler: TagHandler) {
        handlers[tagName] = handler
    }

    fun unregisterHandler(tagName: String) {
        handlers.remove(tagName)
    }

    fun registerCssHandler(property: String, handler: CSSAnnotatedHandler) {
        cssHandlers[property] = handler
    }

    fun unregisterCssHandler(property: String) {
        cssHandlers.remove(property)
    }

    suspend fun from(
        html: String,
        baseUri: String = "",
        getExternalCSS: (suspend (link: String) -> String)? = null
    ): AnnotatedString = from(Ksoup.parse(html, baseUri), getExternalCSS)

    suspend fun from(
        doc: Document,
        getExternalCSS: (suspend (link: String) -> String)? = null
    ): AnnotatedString = withContext(Dispatchers.Default) {
        val root = toHtmlNode(doc, handlers, cssHandlers, logger, getExternalCSS)
        AnnotatedString.Builder().apply {
            val paragraphIntervalList = ArrayList<ParagraphInterval>()

            handleNodeParagraph(root)

            handleNode(root, paragraphIntervalList)

            paragraphIntervalList.buildNotOverlapList(length).forEach { e ->
                addStyle(e.style, e.start, e.end)
            }
        }.toAnnotatedString()
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

                    // If the current node has ParagraphStyle, remove unnecessary stylers
                    if (currentStylers.any { it is IParagraphStyleStyler }) {
                        if (isPreviousNodeHadEndNoExtraLine) {
                            previousNode?.stylers?.removeAll { it is ParagraphEndStyler }
                        }
                        if (isPreviousNodeHadEndWithExtraLine) {
                            previousNode?.stylers?.also { stylers ->
                                for (index in stylers.indices) {
                                    val styler = stylers[index]
                                    if (styler is ParagraphStartStyler && styler.extraLine) {
                                        stylers[index] = ParagraphStartStyler(false)
                                    }
                                }
                            }
                        }

                        for (index in currentStylers.lastIndex downTo 0) {
                            val styler = currentStylers[index]
                            if (styler is ParagraphStartStyler) {
                                if (styler.extraLine) {
                                    currentStylers[index] = ParagraphStartStyler(false)
                                } else {
                                    currentStylers.remove(styler)
                                }
                            } else if (styler is ParagraphEndStyler) {
                                if (styler.extraLine) {
                                    currentStylers[index] = ParagraphEndStyler(false)
                                } else {
                                    currentStylers.remove(styler)
                                }
                            }
                        }
                    }

                    // Recursively handle children of the current node
                    handleNodeParagraph(currentNode)
                }

            }
        }
    }

    private suspend fun AnnotatedString.Builder.handleNode(
        node: HtmlNode,
        paragraphIntervalList: ArrayList<ParagraphInterval>
    ) {
        yield()
        when (node) {
            is StringNode -> {
                append(node.string)
            }
            is StyleNode -> {
                handleStyleNode(node, paragraphIntervalList)
            }
        }
    }

    @OptIn(ExperimentalTextApi::class)
    private suspend fun AnnotatedString.Builder.handleStyleNode(
        node: StyleNode,
        paragraphIntervalList: ArrayList<ParagraphInterval>
    ) {
        suspend fun appendChildren() {
            node.children?.forEach { child ->
                handleNode(child, paragraphIntervalList)
            }
        }

        val stylers = node.stylers?.asSequence()
        if (stylers == null) {
            appendChildren()
            return
        }

        val paragraphStylerList =
            stylers.filterIsInstance<IParagraphStyleStyler>().toList()
        val spanStylerList = stylers.filterIsInstance<ISpanStyleStyler>().toList()
        val stringStylerList =
            stylers.filterIsInstance<IStringAnnotationStyler>().toList()
        val urlStylerList = stylers.filterIsInstance<IUrlAnnotationStyler>().toList()

        stylers.filterIsInstance<IBeforeChildrenAnnotatedStyler>().forEach {
            it.beforeChildren(this)
        }

        spanStylerList.forEach {
            pushStyle(it.getSpanStyler())
        }
        stringStylerList.forEach {
            pushStringAnnotation(it.getTag(), it.getAnnotation())
        }
        urlStylerList.forEach {
            pushUrlAnnotation(it.getUrlAnnotation())
        }

        val popNum = spanStylerList.size + stringStylerList.size + urlStylerList.size

        val startIndex = length

        stylers.filterIsInstance<IAtChildrenBeforeAnnotatedStyler>().forEach {
            it.atChildrenBefore(this)
        }

        appendChildren()

        stylers.filterIsInstance<IAtChildrenAfterAnnotatedStyler>().forEach {
            it.atChildrenAfter(this)
        }

        repeat(popNum) {
            pop()
        }

        val endIndex = length
        paragraphStylerList.map { styler ->
            ParagraphInterval(startIndex, endIndex, styler.getParagraphStyle())
        }.forEach { e ->
            paragraphIntervalList.add(e)
            e.priority = paragraphStylerList.size
        }

        stylers.filterIsInstance<IAfterChildrenAnnotatedStyler>().forEach {
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
            SpanStyleHandler(false) { SpanStyle(fontStyle = FontStyle.Italic) }
        }

        registerHandlerIfAbsent("i") { italicHandler }
        registerHandlerIfAbsent("em") { italicHandler }
        registerHandlerIfAbsent("cite") { italicHandler }
        registerHandlerIfAbsent("dfn") { italicHandler }

        val boldHandler by lazy {
            SpanStyleHandler(false) { SpanStyle(fontWeight = FontWeight.Bold) }
        }

        registerHandlerIfAbsent("b") { boldHandler }
        registerHandlerIfAbsent("strong") { boldHandler }

        val marginHandler by lazy {
            ParagraphStyleHandler { ParagraphStyle(textIndent = TextIndent(4.sp, 4.sp)) }
        }
        registerHandlerIfAbsent("blockquote") { marginHandler }

        registerHandlerIfAbsent("br") { AppendLinesHandler(1) }


        registerHandlerIfAbsent("p") { ParagraphHandler(true) }
        registerHandlerIfAbsent("div") { ParagraphHandler(false) }


        registerHandlerIfAbsent("h1") {
            SpanStyleHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 2.em)
            }
        }
        registerHandlerIfAbsent("h2") {
            SpanStyleHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.5.em)
            }
        }
        registerHandlerIfAbsent("h3") {
            SpanStyleHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.17.em)
            }
        }
        registerHandlerIfAbsent("h4") {
            SpanStyleHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.em)
            }
        }
        registerHandlerIfAbsent("h5") {
            SpanStyleHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.83.em)
            }
        }
        registerHandlerIfAbsent("h6") {
            SpanStyleHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.67.em)
            }
        }


        registerHandlerIfAbsent("tt") {
            SpanStyleHandler {
                SpanStyle(fontFamily = FontFamily.Monospace)
            }
        }


        registerHandlerIfAbsent("pre") { PreAnnotatedHandler() }


        registerHandlerIfAbsent("big") {
            SpanStyleHandler(false) {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.25.em)
            }
        }

        registerHandlerIfAbsent("small") {
            SpanStyleHandler(false) {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.8.em)
            }
        }


        registerHandlerIfAbsent("sub") {
            SpanStyleHandler(false) {
                SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.7.em)
            }
        }

        registerHandlerIfAbsent("sup") {
            SpanStyleHandler(false) {
                SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 0.7.em)
            }
        }


        registerHandlerIfAbsent("center") {
            ParagraphStyleHandler { ParagraphStyle(textAlign = TextAlign.Center) }
        }



        registerHandlerIfAbsent("a") { LinkAnnotatedHandler() }
        registerHandlerIfAbsent("img") { ImageAnnotatedHandler() }

        registerHandlerIfAbsent("span") { TagHandler() }
    }

    private fun registerBuiltInCssHandlers(pre: Map<String, CSSAnnotatedHandler>?) {
        pre?.also { map ->
            cssHandlers.putAll(map)
        }

        fun registerHandlerIfAbsent(tag: String, getHandler: () -> CSSAnnotatedHandler) {
            if (pre?.containsKey(tag) != true) {
                registerCssHandler(tag, getHandler())
            }
        }

        registerHandlerIfAbsent("text-align") { TextAlignCssAnnotatedHandler() }
        registerHandlerIfAbsent("font-size") { FontSizeCssAnnotatedHandler() }
        registerHandlerIfAbsent("font-weight") { FontWeightCssAnnotatedHandler() }
        registerHandlerIfAbsent("font-style") { FontStyleCssAnnotatedHandler() }
        registerHandlerIfAbsent("color") { ColorCssAnnotatedHandler() }
        registerHandlerIfAbsent("background-color") { BackgroundColorCssAnnotatedHandler() }
        registerHandlerIfAbsent("text-indent") { TextIndentCssAnnotatedHandler() }
        registerHandlerIfAbsent("text-decoration") { TextDecorationCssAnnotatedHandler() }

    }

    companion object {
        private const val TAG = "HtmlAnnotator"

        var logger: Logger = defaultLogger()
        var defaultPreTagHandlers: Map<String, TagHandler>? = null
        var defaultPreCSSHandlers: Map<String, CSSAnnotatedHandler>? = null
    }
}