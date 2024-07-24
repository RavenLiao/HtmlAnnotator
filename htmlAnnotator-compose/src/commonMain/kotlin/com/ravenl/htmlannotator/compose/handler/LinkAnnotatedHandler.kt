package com.ravenl.htmlannotator.compose.handler

import com.ravenl.htmlannotator.compose.styler.LinkAnnotatedStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.AbsLinkHandler
import com.ravenl.htmlannotator.core.model.TextStyler

open class LinkAnnotatedHandler : AbsLinkHandler() {

    override fun getUrlStyler(url: String, cssDeclarations: List<CSSDeclaration>?): TextStyler =
        LinkAnnotatedStyler(url)
}