package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import kotlin.math.roundToInt

open class FontWeightCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String) {
        parse(value)?.also { weight ->
            builder.addStyle(SpanStyle(fontWeight = weight), start, end)
        }
    }

    internal open fun parse(value: String): FontWeight? = runCatching {
        when (value) {
            "normal" -> FontWeight.Normal
            "bold" -> FontWeight.Bold
            else -> {
                val int = value.toFloatOrNull()?.roundToInt()
                if (int != null) {
                    FontWeight(int)
                } else {
                    logFail(value)
                    null
                }
            }
        }
    }.onFailure {
        logFail(value, it)
    }.getOrNull()

    private fun logFail(value: String, throwable: Throwable? = null) {
        logger.w(MODULE, throwable) {
            "parse FontWeight fail: $value"
        }
    }

    companion object {
        const val MODULE = "FontWeightCssAnnotatedHandler"
    }
}