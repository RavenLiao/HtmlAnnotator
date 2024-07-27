package com.ravenl.htmlannotator.view.css

import android.text.style.TypefaceSpan
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.HtmlSpanner.Companion.logger
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class FontStyleCssSpannedHandler : CSSSpannedHandler() {

    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parse(value)?.also { span ->
            list.add(SpanStyler(span))
        }
    }

    internal open fun parse(value: String) = runCatching {
        TypefaceSpan(value)
    }.onFailure {
        logFail(value)
    }.getOrNull()

    private fun logFail(value: String, throwable: Throwable? = null) {
        logger.w(MODULE, throwable) {
            "parse FontStyle fail: $value"
        }
    }

    companion object {
        const val MODULE = "FontStyleCssSpannedHandler"
    }
}