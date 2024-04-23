package com.ravenl.htmlannotator.view.css

import android.text.style.LeadingMarginSpan
import com.ravenl.htmlannotator.view.HtmlSpanner.Companion.logger
import com.ravenl.htmlannotator.view.styler.SpannedStyler
import kotlin.math.roundToInt

private const val PX = "px"

open class TextIndentCssSpannedHandler : CSSSpannedHandler() {

    override fun addCssStyler(
        rangeList: MutableList<SpannedStyler>,
        start: Int,
        end: Int,
        value: String
    ) {
        parse(value)?.also { span ->
            rangeList.add(SpannedStyler(start, end, span))
        }
    }

    internal open fun parse(value: String): Any? = runCatching {
        when {
            value.endsWith(PX) -> {
                LeadingMarginSpan.Standard(value.removeSuffix(PX).toFloat().roundToInt())
            }
            else -> {
                logFail(value)
                null
            }
        }
    }.onFailure {
        logFail(value, it)
    }.getOrNull()

    private fun logFail(value: String, throwable: Throwable? = null) {
        logger.w(MODULE, throwable) {
            "parse TextIndent fail: $value"
        }
    }

    companion object {
        const val MODULE = "TextIndentCssSpannedHandler"
    }
}