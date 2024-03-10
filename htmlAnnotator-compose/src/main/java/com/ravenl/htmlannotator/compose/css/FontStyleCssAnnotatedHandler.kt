package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger

open class FontStyleCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String) {
        parse(value)?.also { style ->
            builder.addStyle(SpanStyle(fontStyle = style), start, end)
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