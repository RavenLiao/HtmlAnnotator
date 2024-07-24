package com.ravenl.htmlannotator.view.handler

import android.text.style.TypefaceSpan
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.core.handler.AbsPreHandler
import com.ravenl.htmlannotator.view.styler.SpannedStyler
import com.fleeksoft.ksoup.nodes.Node

class PreSpannedHandler(isStripExtraWhiteSpace: Boolean) : AbsPreHandler(isStripExtraWhiteSpace) {

    override fun getMonospaceStyler(node: Node, start: Int, end: Int): TextStyler =
        SpannedStyler(start, end, TypefaceSpan("monospace"))
}