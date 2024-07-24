package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextIndent
import com.ravenl.htmlannotator.compose.HtmlAnnotator
import com.ravenl.htmlannotator.compose.styler.ParagraphStyleStyler
import com.ravenl.htmlannotator.compose.util.TextUnitParser
import com.ravenl.htmlannotator.core.model.TextStyler

open class TextIndentCssAnnotatedHandler : CSSAnnotatedHandler() {
    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parse(value)?.also { indent ->
            list.add(ParagraphStyleStyler { ParagraphStyle(textIndent = indent) })
        }
    }

    internal open fun parse(value: String): TextIndent? = runCatching {
        TextUnitParser.parse(value).let { size ->
            if (size == null) {
                logFail(value)
                null
            } else {
                TextIndent(firstLine = size)
            }
        }
    }.onFailure {
        logFail(value, it)
    }.getOrNull()

    private fun logFail(value: String, throwable: Throwable? = null) {
        HtmlAnnotator.logger.w(MODULE, throwable) {
            "parse TextIndent fail: $value"
        }
    }

    companion object {
        const val MODULE = "TextIndentCssAnnotatedHandler"
    }
}