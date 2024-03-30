package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.compose.styler.AnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.SpanTextStyler
import com.ravenl.htmlannotator.core.css.CSSColorParser

open class ColorCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCssStyler(
        rangeList: MutableList<AnnotatedStyler>,
        start: Int,
        end: Int,
        value: String
    ) {
        parseColor(value)?.also { color ->
            rangeList.add(SpanTextStyler(start, end, SpanStyle(color = color)))
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
        const val MODULE = "ColorCssAnnotatedHandler"
    }
}