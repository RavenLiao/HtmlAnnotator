package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger

private const val EM = "em"
private const val PX = "px"

open class FontSizeCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String) {
        parse(value)?.also { size ->
            builder.addStyle(SpanStyle(fontSize = size), start, end)
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

