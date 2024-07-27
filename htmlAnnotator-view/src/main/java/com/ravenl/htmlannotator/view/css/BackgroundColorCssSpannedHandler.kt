package com.ravenl.htmlannotator.view.css

import android.graphics.Color
import android.text.style.BackgroundColorSpan
import androidx.annotation.ColorInt
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.HtmlSpanner.Companion.logger
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class BackgroundColorCssSpannedHandler : CSSSpannedHandler() {

    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parseColor(value)?.also { color ->
            list.add(SpanStyler(BackgroundColorSpan(color)))
        }
    }

    private val parser by lazy { CSSColorParser(logger) }

    @ColorInt
    internal open fun parseColor(cssColor: String): Int? = if (cssColor == "transparent") {
        Color.TRANSPARENT
    } else {
        parser.parseColor(cssColor).let { colorInt ->
            if (colorInt != null) {
                colorInt
            } else {
                logger.w(MODULE) {
                    "unsupported parse background color: $cssColor"
                }
                null
            }
        }
    }


    companion object {
        const val MODULE = "BackgroundColorCssSpannedHandler"
    }
}