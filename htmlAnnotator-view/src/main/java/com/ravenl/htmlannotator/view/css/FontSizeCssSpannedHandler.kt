package com.ravenl.htmlannotator.view.css

import android.text.style.AbsoluteSizeSpan
import android.text.style.RelativeSizeSpan
import com.ravenl.htmlannotator.view.HtmlSpanner.Companion.logger
import com.ravenl.htmlannotator.view.styler.SpannedStyler
import kotlin.math.roundToInt

private const val EM = "em"
private const val PX = "px"

open class FontSizeCssSpannedHandler : CSSSpannedHandler() {

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

    internal open fun parse(value: String) = runCatching {
        when {
            value.endsWith(EM) -> {
                RelativeSizeSpan(value.removeSuffix(EM).toFloat())
            }
            value.endsWith(PX) -> {
                AbsoluteSizeSpan(value.removeSuffix(PX).toFloat().roundToInt())
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
            "parse FontSize fail: $value"
        }
    }

    companion object {
        const val MODULE = "FontSizeCssSpannedHandler"
    }
}

