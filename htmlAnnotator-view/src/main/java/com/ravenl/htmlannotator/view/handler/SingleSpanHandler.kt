package com.ravenl.htmlannotator.view.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class SingleSpanHandler(
    private val isParagraph: Boolean = true,
    addExtraLine: Boolean = true,
    val newSpan: () -> Any
) : ParagraphHandler(addExtraLine) {

    private val span by lazy { newSpan() }

    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        if (isParagraph) {
            super.addTagStylers(list, node, cssDeclarations)
        }
        list.add(getTagStyler(node))
    }

    open fun getTagStyler(node: Node): TextStyler = SpanStyler(span)
}