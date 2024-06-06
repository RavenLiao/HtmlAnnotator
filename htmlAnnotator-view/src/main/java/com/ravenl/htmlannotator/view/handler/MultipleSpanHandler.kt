package com.ravenl.htmlannotator.view.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.NewLineHandler
import com.ravenl.htmlannotator.view.styler.SpannedStyler
import com.fleeksoft.ksoup.nodes.Node

open class MultipleSpanHandler(addNewLineAtBefore: Boolean = true, val newSpans: () -> List<Any>) :
    NewLineHandler(addNewLineAtBefore) {

    private val spans by lazy { newSpans() }

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        rangeList.addAll(getTagStylers(node, start, end))
    }

    open fun getTagStylers(node: Node, start: Int, end: Int): List<TextStyler> = spans.map {
        SpannedStyler(start, end, it)
    }

}