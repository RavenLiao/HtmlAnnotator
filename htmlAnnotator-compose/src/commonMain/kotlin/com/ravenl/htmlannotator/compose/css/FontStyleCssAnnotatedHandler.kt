package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.compose.styler.SpanStyleStyler
import com.ravenl.htmlannotator.core.model.TextStyler

open class FontStyleCssAnnotatedHandler : CSSAnnotatedHandler() {
    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parse(value)?.also { style ->
            list.add(SpanStyleStyler { SpanStyle(fontStyle = style) })
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