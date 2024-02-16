package com.ravenl.htmlannotator.compose.handler

import com.ravenl.htmlannotator.compose.styler.LinkAnnotatedStyler
import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.handler.AbsLinkHandler

class LinkAnnotatedHandler : AbsLinkHandler() {
    override fun getUrlStyler(url: String, start: Int, end: Int): TagStyler =
        LinkAnnotatedStyler(url, start, end)
}