package com.ravenl.htmlannotator.view.styler

import android.text.SpannableStringBuilder


data class ParagraphStartStyler(val extraLine: Boolean) : IBeforeChildrenSpannedStyler {
    override fun beforeChildren(builder: SpannableStringBuilder) {
        if (builder.isNotEmpty()) {
            builder.appendLine()
            if (extraLine) {
                builder.appendLine()
            }
        }
    }
}

data class ParagraphEndStyler(val extraLine: Boolean) : IAfterChildrenSpannedStyler {
    override fun afterChildren(builder: SpannableStringBuilder) {
        builder.appendLine()
        if (extraLine) {
            builder.appendLine()
        }
    }
}
