package com.ravenL.htmlannotator.spanner

import android.text.Spannable


fun Spannable.replaceSpan(old: Any, new: Any) {
    val start = getSpanStart(old)
    val end = getSpanEnd(old)
    val flags = getSpanFlags(old)
    removeSpan(old)
    setSpan(new, start, end, flags)
}