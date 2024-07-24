package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.compose.styler.SpanStyleStyler
import com.ravenl.htmlannotator.core.model.TextStyler
import kotlin.math.roundToInt

open class FontWeightCssAnnotatedHandler : CSSAnnotatedHandler() {
    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parse(value)?.also { weight ->
            list.add(SpanStyleStyler { SpanStyle(fontWeight = weight) })
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