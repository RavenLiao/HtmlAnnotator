package com.ravenl.htmlannotator.view.css

import android.text.Layout
import android.text.style.AlignmentSpan
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.HtmlSpanner.Companion.logger
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class TextAlignCssSpannedHandler : CSSSpannedHandler() {

    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parse(value)?.also { align ->
            list.add(SpanStyler(AlignmentSpan.Standard(align)))
        }
    }

    internal open fun parse(value: String): Layout.Alignment? = when (value) {
        "center" -> Layout.Alignment.ALIGN_CENTER
        else -> {
            logger.w(MODULE) { "parse TextAlign fail: $value" }
            null
        }
    }


    companion object {
        const val MODULE = "TextAlignCssSpannedHandler"
    }
}