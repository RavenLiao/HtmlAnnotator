package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.core.css.CSSColorParser

open class BackgroundColorCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String) {
        parseColor(value)?.also { color ->
            builder.addStyle(SpanStyle(background = color), start, end)
        }
    }

    private val parser by lazy { CSSColorParser(logger) }

    internal open fun parseColor(cssColor: String): Color? = if (cssColor == "transparent") {
        Color.Transparent
    } else {
        parser.parseColor(cssColor).let { colorInt ->
            if (colorInt != null) {
                Color(colorInt)
            } else {
                logger.w(MODULE) {
                    "unsupported parse background color: $cssColor"
                }
                null
            }
        }
    }


    companion object {
        const val MODULE = "BackgroundColorCssAnnotatedHandler"
    }
}