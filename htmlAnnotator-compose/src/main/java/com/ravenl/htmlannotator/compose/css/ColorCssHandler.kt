package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.core.css.CSSColorParser

open class ColorCssHandler : CSSHandler() {

    override fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String) {
        parseColor(value)?.also { color ->
            builder.addStyle(SpanStyle(color = color), start, end)
        }
    }

    private val parser by lazy { CSSColorParser(logger) }

    internal open fun parseColor(cssColor: String): Color? =
        parser.parseColor(cssColor).let { colorInt ->
            if (colorInt != null) {
                Color(colorInt)
            } else {
                logger.w(MODULE) {
                    "unsupported parse color: $cssColor"
                }
                null
            }
        }

    companion object {
        const val MODULE = "ColorCssHandler"
    }
}