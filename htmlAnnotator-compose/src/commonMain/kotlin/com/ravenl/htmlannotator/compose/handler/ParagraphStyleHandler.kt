package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.ParagraphStyle
import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.compose.styler.ParagraphStyleStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.TextStyler


class ParagraphStyleHandler(
    private val isParagraph: Boolean = true,
    addExtraLine: Boolean = true,
    private val newStyle: () -> ParagraphStyle
) : ParagraphHandler(addExtraLine) {
    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        if (isParagraph) {
            super.addTagStylers(list, node, cssDeclarations)
        }
        list.add(ParagraphStyleStyler(newStyle))
    }
}