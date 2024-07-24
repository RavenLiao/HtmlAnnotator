package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.SpanStyle
import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.compose.styler.SpanStyleStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.TextStyler

open class SpanStyleHandler(
    private val isParagraph: Boolean = true,
    addExtraLine: Boolean = true,
    private val newSpanStyle: () -> SpanStyle
) : ParagraphHandler(addExtraLine) {

    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        if (isParagraph) {
            super.addTagStylers(list, node, cssDeclarations)
        }
        list.add(SpanStyleStyler(newSpanStyle))
    }
}