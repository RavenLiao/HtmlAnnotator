package com.ravenl.htmlannotator.view.handler

import android.text.style.TypefaceSpan
import com.ravenl.htmlannotator.core.handler.AbsPreHandler
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.styler.SpanStyler
import com.ravenl.htmlannotator.view.styler.SpannedStyler

class PreSpannedHandler : AbsPreHandler() {

    override fun getMonospaceStyler(): TextStyler =
        SpanStyler(TypefaceSpan("monospace"))
}