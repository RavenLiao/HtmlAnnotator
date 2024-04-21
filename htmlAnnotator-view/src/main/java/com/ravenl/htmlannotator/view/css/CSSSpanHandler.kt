package com.ravenl.htmlannotator.view.css

import com.ravenl.htmlannotator.view.styler.SpannedStyler

abstract class CSSSpannedHandler {
    abstract fun addCssStyler(
        rangeList: MutableList<SpannedStyler>,
        start: Int,
        end: Int,
        value: String
    )
}