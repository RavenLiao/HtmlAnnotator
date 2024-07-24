package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.compose.styler.SpanStyleStyler
import com.ravenl.htmlannotator.compose.util.TextUnitParser
import com.ravenl.htmlannotator.core.model.TextStyler

open class FontSizeCssAnnotatedHandler : CSSAnnotatedHandler() {
    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parse(value)?.also { size ->
            list.add(SpanStyleStyler { SpanStyle(fontSize = size) })
        }
    }

    internal open fun parse(value: String): TextUnit? = runCatching {
        TextUnitParser.parse(value).also {
            if (it == null) {
                logFail(value)
            }
        }
    }.onFailure {
        logFail(value, it)
    }.getOrNull()

    private fun logFail(value: String, throwable: Throwable? = null) {
        logger.w(MODULE, throwable) {
            "parse FontSize fail: $value"
        }
    }

    companion object {
        const val MODULE = "FontSizeCssAnnotatedHandler"
    }
}

