package com.ravenl.htmlannotator.view.styler

import android.text.SpannableStringBuilder
import android.text.Spanned
import com.ravenl.htmlannotator.core.model.TextStyler

class SpannedStyler(start: Int, end: Int, private val span: Any) : TextStyler(start, end) {

    fun addStyle(builder: SpannableStringBuilder) {
        builder.setSpan(span, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}