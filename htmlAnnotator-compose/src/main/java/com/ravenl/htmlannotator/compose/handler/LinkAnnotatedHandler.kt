package com.ravenl.htmlannotator.compose.handler

import com.ravenl.htmlannotator.compose.styler.LinkAnnotatedStyler
import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.handler.AbsLinkHandler

class LinkAnnotatedHandler : AbsLinkHandler() {
    override fun getUrlStyler(url: String, start: Int, end: Int): TextStyler =
        LinkAnnotatedStyler(url, start, end)
}