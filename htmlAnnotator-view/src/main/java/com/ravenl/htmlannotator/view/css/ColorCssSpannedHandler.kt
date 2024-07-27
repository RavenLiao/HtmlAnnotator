package com.ravenl.htmlannotator.view.css

import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.HtmlSpanner.Companion.logger
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class ColorCssSpannedHandler : CSSSpannedHandler() {

    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parseColor(value)?.also { color ->
            list.add(SpanStyler(ForegroundColorSpan(color)))
        }
    }

    private val parser by lazy { CSSColorParser(logger) }

    @ColorInt
    internal open fun parseColor(cssColor: String): Int? =
        parser.parseColor(cssColor).let { colorInt ->
            if (colorInt != null) {
                colorInt
            } else {
                logger.w(MODULE) {
                    "unsupported parse color: $cssColor"
                }
                null
            }
        }

    companion object {
        const val MODULE = "ColorCssSpannedHandler"
    }
}