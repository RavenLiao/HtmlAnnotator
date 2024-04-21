package com.ravenl.htmlannotator.view.styler

import android.text.SpannableStringBuilder
import com.ravenl.htmlannotator.core.TextStyler

abstract class SpannedStyler(start: Int, end: Int) : TextStyler(start, end) {

    abstract fun addStyle(builder: SpannableStringBuilder)
}