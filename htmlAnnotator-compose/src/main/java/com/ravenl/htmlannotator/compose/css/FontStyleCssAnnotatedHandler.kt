package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.compose.styler.AnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.SpanTextStyler

open class FontStyleCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCssStyler(
        rangeList: MutableList<AnnotatedStyler>,
        start: Int,
        end: Int,
        value: String
    ) {
        parse(value)?.also { style ->
            rangeList.add(SpanTextStyler(start, end, SpanStyle(fontStyle = style)))
        }
    }

    internal open fun parse(value: String): FontStyle? =
        when (value) {
            "normal" -> FontStyle.Normal
            "italic" -> FontStyle.Italic
            else -> {
                logFail(value)
                null
            }
        }

    private fun logFail(value: String, throwable: Throwable? = null) {
        logger.w(MODULE, throwable) {
            "parse FontStyle fail: $value"
        }
    }

    companion object {
        const val MODULE = "FontStyleCssAnnotatedHandler"
    }
}