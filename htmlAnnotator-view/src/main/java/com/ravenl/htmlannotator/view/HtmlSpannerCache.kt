package com.ravenl.htmlannotator.view

import android.text.Spannable

interface HtmlSpannerCache {
    fun put(src: String, result: Spannable)
    fun get(src: String): Spannable?
}