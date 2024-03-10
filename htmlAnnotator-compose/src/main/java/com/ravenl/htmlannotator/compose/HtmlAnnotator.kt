package com.ravenl.htmlannotator.compose

import androidx.collection.ArrayMap
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
import com.ravenl.htmlannotator.compose.styler.AnnotatedTagStyler
import com.ravenl.htmlannotator.core.handler.NewLineHandler
import com.ravenl.htmlannotator.core.handler.ParagraphHandler
import com.ravenl.htmlannotator.core.handler.TagHandler
import com.ravenl.htmlannotator.core.toHtmlAnnotation
import com.ravenl.htmlannotator.core.util.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream

class HtmlAnnotator(
    preTagHandlers: Map<String, TagHandler>? = defaultPreTagHandlers,
    preCSSHandlers: Map<String, CSSAnnotatedHandler>? = defaultPreCSSHandlers,
    val isStripExtraWhiteSpace: Boolean = defaultIsStripExtraWhiteSpace
) {

    private val handlers = ArrayMap<String, TagHandler>()

    private val cssHandlers = ArrayMap<String, CSSAnnotatedHandler>()

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
        input: InputStream, baseUri: String = "", charsetName: String? = null,
        getExternalCSS: (suspend (link: String) -> String)? = null
    ) = from(Jsoup.parse(input, charsetName, baseUri), getExternalCSS)

    suspend fun from(
        html: String,
        baseUri: String = "",
        getExternalCSS: (suspend (link: String) -> String)? = null
    ) = from(Jsoup.parse(html, baseUri), getExternalCSS)

    suspend fun from(
        doc: Document,
        getExternalCSS: (suspend (link: String) -> String)? = null
    ): AnnotatedString {
        val (body, tags, cssBlocks) = toHtmlAnnotation(doc, handlers, logger, getExternalCSS)
        return AnnotatedString.Builder(body.length).apply {
            append(body)
            tags.forEach { tag ->
                tag as AnnotatedTagStyler
                tag.addStyle(this)
            }

            cssBlocks.forEach { block ->
                block.declarations.forEach { declaration ->
                    with(declaration) {
                        cssHandlers[property]?.addCss(this@apply, block.start, block.end, value)
                    }
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
            SpanTextHandler { SpanStyle(fontStyle = FontStyle.Italic) }
        }

        registerHandlerIfAbsent("i") { italicHandler }
        registerHandlerIfAbsent("em") { italicHandler }
        registerHandlerIfAbsent("cite") { italicHandler }
        registerHandlerIfAbsent("dfn") { italicHandler }

        val boldHandler by lazy {
            SpanTextHandler { SpanStyle(fontWeight = FontWeight.Bold) }
        }

        registerHandlerIfAbsent("b") { boldHandler }
        registerHandlerIfAbsent("strong") { boldHandler }

        registerHandlerIfAbsent("blockquote") {
            ParagraphTextHandler { ParagraphStyle(textIndent = TextIndent(30.em, 30.em)) }
        }

        registerHandlerIfAbsent("br") { NewLineHandler(isStripExtraWhiteSpace, 1) }


        val pHandler by lazy(boldHandler) { ParagraphHandler() }

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
            SpanTextHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.25.em)
            }
        }

        registerHandlerIfAbsent("small") {
            SpanTextHandler {
                SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.8.em)
            }
        }


        registerHandlerIfAbsent("sub") {
            SpanTextHandler {
                SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.7.em)
            }
        }

        registerHandlerIfAbsent("sup") {
            SpanTextHandler {
                SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 0.7.em)
            }
        }


        registerHandlerIfAbsent("center") {
            ParagraphTextHandler { ParagraphStyle(textAlign = TextAlign.Center) }
        }



        registerHandlerIfAbsent("a") { LinkAnnotatedHandler() }
        registerHandlerIfAbsent("img") { ImageAnnotatedHandler() }
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
        val logger by lazy { Logger() }
        var defaultPreTagHandlers: Map<String, TagHandler>? = null
        var defaultPreCSSHandlers: Map<String, CSSAnnotatedHandler>? = null
        var defaultIsStripExtraWhiteSpace: Boolean = true
    }
}