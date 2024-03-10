package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import com.ravenl.htmlannotator.compose.HtmlAnnotator

open class TextDecorationCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String) {
        parse(value)?.also { decoration ->
            builder.addStyle(SpanStyle(textDecoration = decoration), start, end)
        }
    }

    internal open fun parse(value: String): TextDecoration? = runCatching {
        val list = buildList {
            if (value.contains("underline")) {
                add(TextDecoration.Underline)
            }
            if (value.contains("line-through")) {
                add(TextDecoration.LineThrough)
            }
        }
        if (list.isEmpty()) {
            logFail(value)
            null
        } else {
            TextDecoration.combine(list)
        }
    }.onFailure {
        logFail(value, it)
    }.getOrNull()

    private fun logFail(value: String, throwable: Throwable? = null) {
        HtmlAnnotator.logger.w(MODULE, throwable) {
            "parse Text Decoration fail: $value"
        }
    }

    companion object {
        const val MODULE = "TextDecorationCssAnnotatedHandler"
    }
}