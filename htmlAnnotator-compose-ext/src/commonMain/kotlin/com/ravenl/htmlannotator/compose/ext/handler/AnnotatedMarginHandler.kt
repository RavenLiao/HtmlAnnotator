package com.ravenl.htmlannotator.compose.ext.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.compose.ext.styler.MarginStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.TagHandler
import com.ravenl.htmlannotator.core.model.TextStyler

open class AnnotatedMarginHandler(val addMargin: () -> List<MarginStyler>) : TagHandler() {
    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        list.addAll(addMargin())
    }
}