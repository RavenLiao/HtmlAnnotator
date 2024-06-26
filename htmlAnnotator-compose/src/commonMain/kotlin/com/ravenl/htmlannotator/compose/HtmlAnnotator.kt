@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.ravenl.htmlannotator.compose

import androidx.compose.ui.text.AnnotatedString
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
import com.ravenl.htmlannotator.compose.handler.ImageAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.LinkAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.ParagraphTextHandler
import com.ravenl.htmlannotator.compose.handler.PreAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.SpanTextHandler
import com.ravenl.htmlannotator.compose.styler.AnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.ParagraphTextStyler
import com.ravenl.htmlannotator.compose.styler.buildNotOverlapList
import com.ravenl.htmlannotator.core.handler.AppendLinesHandler
import com.ravenl.htmlannotator.core.handler.ParagraphHandler
import com.ravenl.htmlannotator.core.handler.TagHandler
import com.ravenl.htmlannotator.core.toHtmlAnnotation
import com.ravenl.htmlannotator.core.util.defaultLogger
import com.ravenl.htmlannotator.core.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HtmlAnnotator(
    preTagHandlers: Map<String, TagHandler>? = defaultPreTagHandlers,
    preCSSHandlers: Map<String, CSSAnnotatedHandler>? = defaultPreCSSHandlers,
    val isStripExtraWhiteSpace: Boolean = defaultIsStripExtraWhiteSpace
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
        val (body, tagStylers, cssBlocks) = toHtmlAnnotation(doc, handlers, logger, getExternalCSS)
        AnnotatedString.Builder(body.length).apply {
            append(body)
            val paragraphStylerList = ArrayList<ParagraphTextStyler>()
            tagStylers.forEach { src ->
                val styler = src as? AnnotatedStyler
                if (styler != null) {
                    if (styler is ParagraphTextStyler) {
                        styler.priority = paragraphStylerList.size
                        paragraphStylerList.add(styler)
                    } else {
                        styler.addStyle(this)
                    }
                } else {
                    logger.e(TAG) { "TagStyler is not AnnotatedTagStyler: $src" }
                }
            }

            val cssStylerList = ArrayList<AnnotatedStyler>()
            cssBlocks.forEach { block ->
                block.declarations.forEach { declaration ->
                    with(declaration) {
                        val cssHandler = cssHandlers[property]
                        if (cssHandler != null) {
                            cssHandler.addCssStyler(cssStylerList, block.start, block.end, value)
                        } else {
                            logger.w(TAG) { "unsupported css: $property: $value" }
                        }

                    }
                }
            }
            cssStylerList.forEach { styler ->
                if (styler is ParagraphTextStyler) {
                    styler.priority = paragraphStylerList.size
                    paragraphStylerList.add(styler)
                } else {
                    styler.addStyle(this)
                }
            }

            if (paragraphStylerList.isNotEmpty()) {
                paragraphStylerList.buildNotOverlapList(length).forEach {
                    it.addStyle(this)
                }
            }

        }.toAnnotatedString()
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
            SpanTextHandler(false) { SpanStyle(fontStyle = FontStyle.Italic) }
        }

        registerHandlerIfAbsent("i") { italicHandler }
        registerHandlerIfAbsent("em") { italicHandler }
        registerHandlerIfAbsent("cite") { italicHandler }
        registerHandlerIfAbsent("dfn") { italicHandler }

        val boldHandler by lazy {
            SpanTextHandler(false) { SpanStyle(fontWeight = FontWeight.Bold) }
        }

        registerHandlerIfAbsent("b") { boldHandler }
        registerHandlerIfAbsent("strong") { boldHandler }

        val marginHandler by lazy {
            ParagraphTextHandler { ParagraphStyle(textIndent = TextIndent(4.sp, 4.sp)) }
        }
        registerHandlerIfAbsent("blockquote") { marginHandler }

        registerHandlerIfAbsent("br") { AppendLinesHandler(isStripExtraWhiteSpace, 1) }


        val pHandler by lazy { ParagraphHandler() }

        registerHandlerIfAbsent("p") { pHandler }
        registerHandlerIfAbsent("div") { pHandler }


        registerHandlerIfAbsent("h1") {
            SpanTextHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 2.em)
            }
        }
        registerHandlerIfAbsent("h2") {
            SpanTextHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.5.em)
            }
        }
        registerHandlerIfAbsent("h3") {
            SpanTextHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.17.em)
            }
        }
        registerHandlerIfAbsent("h4") {
            SpanTextHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.em)
            }
        }
        registerHandlerIfAbsent("h5") {
            SpanTextHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.83.em)
            }
        }
        registerHandlerIfAbsent("h6") {
            SpanTextHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.67.em)
            }
        }


        registerHandlerIfAbsent("tt") {
            SpanTextHandler {
                SpanStyle(fontFamily = FontFamily.Monospace)
            }
        }


        registerHandlerIfAbsent("pre") { PreAnnotatedHandler(isStripExtraWhiteSpace) }


        registerHandlerIfAbsent("big") {
            SpanTextHandler(false) {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.25.em)
            }
        }

        registerHandlerIfAbsent("small") {
            SpanTextHandler(false) {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.8.em)
            }
        }


        registerHandlerIfAbsent("sub") {
            SpanTextHandler(false) {
                SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.7.em)
            }
        }

        registerHandlerIfAbsent("sup") {
            SpanTextHandler(false) {
                SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 0.7.em)
            }
        }


        registerHandlerIfAbsent("center") {
            ParagraphTextHandler { ParagraphStyle(textAlign = TextAlign.Center) }
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
        var defaultIsStripExtraWhiteSpace: Boolean = true
    }
}