package com.ravenl.htmlannotator.view.css

import android.text.Layout
import android.text.style.AlignmentSpan
import com.ravenl.htmlannotator.view.HtmlSpanner.Companion.logger
import com.ravenl.htmlannotator.view.styler.SpannedStyler

open class TextAlignCssSpannedHandler : CSSSpannedHandler() {

    override fun addCssStyler(
        rangeList: MutableList<SpannedStyler>,
        start: Int,
        end: Int,
        value: String
    ) {
        parse(value)?.also { align ->
            rangeList.add(SpannedStyler(start, end, AlignmentSpan.Standard(align)))
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