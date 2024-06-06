package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.compose.styler.AnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.SpanTextStyler

private const val EM = "em"
private const val PX = "px"

open class FontSizeCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCssStyler(
        rangeList: MutableList<AnnotatedStyler>,
        start: Int,
        end: Int,
        value: String
    ) {
        parse(value)?.also { size ->
            rangeList.add(SpanTextStyler(start, end, SpanStyle(fontSize = size)))
        }
    }

    internal open fun parse(value: String): TextUnit? = runCatching {
        when {
            value.endsWith(EM) -> {
                value.removeSuffix(EM).toFloat().em
            }
            value.endsWith(PX) -> {
                value.removeSuffix(PX).toFloat().sp
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
        const val MODULE = "FontSizeCssAnnotatedHandler"
    }
}

