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
import com.ravenl.htmlannotator.compose.css.CSSHandler
import com.ravenl.htmlannotator.compose.css.ColorCssHandler
import com.ravenl.htmlannotator.compose.handler.ImageAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.LinkAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.ParagraphTextHandler
import com.ravenl.htmlannotator.compose.handler.PreAnnotatedHandler
import com.ravenl.htmlannotator.compose.handler.SpanTextHandler
import com.ravenl.htmlannotator.compose.styler.AnnotatedTagStyler
import com.ravenl.htmlannotator.core.handler.NewLineHandler
import com.ravenl.htmlannotator.core.handler.TagHandler
import com.ravenl.htmlannotator.core.toHtmlAnnotation
import com.ravenl.htmlannotator.core.util.Logger
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream

class HtmlAnnotator(
    val isStripExtraWhiteSpace: Boolean = true
) {

    private val handlers = ArrayMap<String, TagHandler>()

    private val cssHandlers = ArrayMap<String, CSSHandler>()

    init {
        registerBuiltInHandlers()
        registerBuiltInCssHandlers()
    }

    fun registerHandler(tagName: String, handler: TagHandler) {
        handlers[tagName] = handler
    }

    fun unregisterHandler(tagName: String) {
        handlers.remove(tagName)
    }

    fun registerCssHandler(property: String, handler: CSSHandler) {
        cssHandlers[property] = handler
    }

    fun unregisterCssHandler(property: String) {
        cssHandlers.remove(property)
    }

    fun from(input: InputStream, baseUri: String = "", charsetName: String? = null) =
        from(Jsoup.parse(input, charsetName, baseUri))

    fun from(html: String, baseUri: String = "") = from(Jsoup.parse(html, baseUri))

    fun from(doc: Document): AnnotatedString {
        val (body, tags, cssBlocks) = toHtmlAnnotation(doc, handlers)
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

    private fun registerBuiltInHandlers() {
        val italicHandler = SpanTextHandler { SpanStyle(fontStyle = FontStyle.Italic) }

        registerHandler("i", italicHandler)
        registerHandler("em", italicHandler)
        registerHandler("cite", italicHandler)
        registerHandler("dfn", italicHandler)

        val boldHandler = SpanTextHandler { SpanStyle(fontWeight = FontWeight.Bold) }

        registerHandler("b", boldHandler)
        registerHandler("strong", boldHandler)

        val marginHandler =
            ParagraphTextHandler { ParagraphStyle(textIndent = TextIndent(30.em, 30.em)) }

        registerHandler("blockquote", marginHandler)
        registerHandler("ul", marginHandler)
        registerHandler("ol", marginHandler)

        val brHandler = NewLineHandler(isStripExtraWhiteSpace, 1)

        registerHandler("br", brHandler)


        val pHandler = SpanTextHandler { SpanStyle() }

        registerHandler("p", pHandler)
        registerHandler("div", pHandler)


        registerHandler("h1", SpanTextHandler {
            SpanStyle(fontWeight = FontWeight.Bold, fontSize = 2.em)
        })
        registerHandler("h2", SpanTextHandler {
            SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.5.em)
        })
        registerHandler("h3", SpanTextHandler {
            SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.17.em)
        })
        registerHandler("h4", SpanTextHandler {
            SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.em)
        })
        registerHandler("h5", SpanTextHandler {
            SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.83.em)
        })
        registerHandler("h6", SpanTextHandler {
            SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.67.em)
        })


        registerHandler("tt", SpanTextHandler {
            SpanStyle(fontFamily = FontFamily.Monospace)
        })


        registerHandler("pre", PreAnnotatedHandler(isStripExtraWhiteSpace))


        registerHandler("big", SpanTextHandler {
            SpanStyle(fontWeight = FontWeight.Bold, fontSize = 1.25.em)
        })

        registerHandler("small", SpanTextHandler {
            SpanStyle(fontWeight = FontWeight.Bold, fontSize = 0.8.em)
        })


        registerHandler("sub", SpanTextHandler {
            SpanStyle(baselineShift = BaselineShift.Subscript, fontSize = 0.7.em)
        })

        registerHandler("sup", SpanTextHandler {
            SpanStyle(baselineShift = BaselineShift.Superscript, fontSize = 0.7.em)
        })


        registerHandler("center", ParagraphTextHandler {
            ParagraphStyle(textAlign = TextAlign.Center)
        })



        registerHandler("a", LinkAnnotatedHandler())
        registerHandler("img", ImageAnnotatedHandler())
    }

    private fun registerBuiltInCssHandlers() {
        registerCssHandler("color", ColorCssHandler())
    }

    companion object {
        val logger by lazy { Logger() }
    }
}