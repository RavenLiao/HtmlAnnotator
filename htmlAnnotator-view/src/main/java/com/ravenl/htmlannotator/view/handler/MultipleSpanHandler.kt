package com.ravenl.htmlannotator.view.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.styler.SpanStyler

open class MultipleSpanHandler(
    private val isParagraph: Boolean = true,
    addExtraLine: Boolean = true,
    val newSpans: () -> List<Any>
) : ParagraphHandler(addExtraLine) {

    private val spans by lazy { newSpans() }

    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        if (isParagraph) {
            super.addTagStylers(list, node, cssDeclarations)
        }
        list.addAll(getTagStylers(node))
    }

    open fun getTagStylers(node: Node): List<TextStyler> = spans.map {
        SpanStyler(it)
    }

}