package com.ravenl.htmlannotator.view.handler

import android.text.style.URLSpan
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.AbsLinkHandler
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class LinkSpannedHandler : AbsLinkHandler() {
    override fun getUrlStyler(url: String, cssDeclarations: List<CSSDeclaration>?): TextStyler =
        SpanStyler(URLSpan(url))
}