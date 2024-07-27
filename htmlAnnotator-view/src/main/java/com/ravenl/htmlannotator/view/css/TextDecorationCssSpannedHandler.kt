package com.ravenl.htmlannotator.view.css

import android.text.style.StrikethroughSpan
import android.text.style.UnderlineSpan
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.HtmlSpanner.Companion.logger
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class TextDecorationCssSpannedHandler : CSSSpannedHandler() {

    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parse(value)?.also { decoration ->
            decoration.map {
                SpanStyler(decoration)
            }.also(list::addAll)
        }
    }

    internal open fun parse(value: String): List<Any>? = runCatching {
        val list = buildList {
            if (value.contains("underline")) {
                add(UnderlineSpan())
            }
            if (value.contains("line-through")) {
                add(StrikethroughSpan())
            }
        }
        list.ifEmpty {
            logFail(value)
            null
        }
    }.onFailure {
        logFail(value, it)
    }.getOrNull()

    private fun logFail(value: String, throwable: Throwable? = null) {
        logger.w(MODULE, throwable) {
            "parse Text Decoration fail: $value"
        }
    }

    companion object {
        const val MODULE = "TextDecorationCssSpannedHandler"
    }
}