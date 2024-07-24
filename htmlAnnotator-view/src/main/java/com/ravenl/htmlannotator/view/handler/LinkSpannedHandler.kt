package com.ravenl.htmlannotator.view.handler

import android.text.style.URLSpan
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.core.handler.AbsLinkHandler
import com.ravenl.htmlannotator.view.styler.SpannedStyler

open class LinkSpannedHandler : AbsLinkHandler() {
    override fun getUrlStyler(url: String, start: Int, end: Int): TextStyler =
        SpannedStyler(start, end, URLSpan(url))
}