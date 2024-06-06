package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.ParagraphStyle
import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.compose.styler.ParagraphTextStyler
import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.TagHandler


open class ParagraphTextHandler(val newStyle: () -> ParagraphStyle) : TagHandler() {

    private val paragraphStyle by lazy { newStyle() }

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        rangeList.add(getTagStyler(node, start, end - 1))
    }


    open fun getTagStyler(node: Node, start: Int, end: Int): TextStyler =
        ParagraphTextStyler(start, end, paragraphStyle)
}