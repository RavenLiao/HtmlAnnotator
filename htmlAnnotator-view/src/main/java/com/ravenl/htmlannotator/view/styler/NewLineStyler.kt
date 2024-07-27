package com.ravenl.htmlannotator.view.styler

import android.text.SpannableStringBuilder


object NewLineStyler : IAtChildrenBeforeSpannedStyler {

    override fun atChildrenBefore(builder: SpannableStringBuilder) {
        builder.appendLine()
    }
}

