package com.ravenl.htmlannotator.compose.handler

import androidx.compose.ui.text.ParagraphStyle
import com.ravenl.htmlannotator.compose.styler.ParagraphTextStyler
import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.TagHandler
import org.jsoup.nodes.Node

open class ParagraphTextHandler(val newStyle: () -> ParagraphStyle) : TagHandler() {

    private val paragraphStyle by lazy { newStyle() }

    override fun beforeChildren(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node
    ) {
        //paragraphStyle will add line feed character auto. so mark it.
        builder.append('\n')
    }

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        //remove mark line feed character
        builder.deleteCharAt(start)
        rangeList.add(getTagStyler(node, start, end - 1))
    }


    open fun getTagStyler(node: Node, start: Int, end: Int): TextStyler =
        ParagraphTextStyler(start, end, paragraphStyle)
}