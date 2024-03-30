package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.ravenl.htmlannotator.compose.HtmlAnnotator
import com.ravenl.htmlannotator.compose.styler.AnnotatedStyler
import com.ravenl.htmlannotator.compose.styler.ParagraphTextStyler

private const val EM = "em"
private const val PX = "px"

open class TextIndentCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCssStyler(
        rangeList: MutableList<AnnotatedStyler>,
        start: Int,
        end: Int,
        value: String
    ) {
        parse(value)?.also { indent ->
            rangeList.add(ParagraphTextStyler(start, end, ParagraphStyle(textIndent = indent)))
        }
    }

    internal open fun parse(value: String): TextIndent? = runCatching {
        when {
            value.endsWith(EM) -> {
                TextIndent(firstLine = value.removeSuffix(EM).toFloat().em)
            }
            value.endsWith(PX) -> {
                TextIndent(firstLine = value.removeSuffix(PX).toFloat().sp)
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
        HtmlAnnotator.logger.w(MODULE, throwable) {
            "parse TextIndent fail: $value"
        }
    }

    companion object {
        const val MODULE = "TextIndentCssAnnotatedHandler"
    }
}