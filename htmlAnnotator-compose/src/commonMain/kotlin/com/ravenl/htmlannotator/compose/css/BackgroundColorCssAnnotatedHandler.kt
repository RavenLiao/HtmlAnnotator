package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.compose.styler.SpanStyleStyler
import com.ravenl.htmlannotator.core.model.TextStyler

open class BackgroundColorCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parseColor(value)?.also { color ->
            list.add(SpanStyleStyler { SpanStyle(background = color) })
        }
    }

    private val parser by lazy { CSSColorParser(logger) }

    internal open fun parseColor(cssColor: String): Color? = if (cssColor == "transparent") {
        Color.Transparent
    } else {
        parser.parseColor(cssColor).also { color ->
            if (color == null) {
                logger.w(MODULE) {
                    "unsupported parse background color: $cssColor"
                }
            }
        }
    }


    companion object {
        const val MODULE = "BackgroundColorCssAnnotatedHandler"
    }
}