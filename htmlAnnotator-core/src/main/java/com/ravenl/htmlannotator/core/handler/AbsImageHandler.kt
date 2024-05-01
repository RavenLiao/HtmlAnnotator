package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import org.jsoup.nodes.Node

abstract class AbsImageHandler : TagHandler() {

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TextStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        builder.append(PLACE_HOLDER)
        rangeList.add(
            getImageStyler(
                node.attr("src"),
                cssDeclarations,
                end,
                end + PLACE_HOLDER.length
            )
        )
    }

    abstract fun getImageStyler(
        imageUrl: String,
        cssDeclarations: List<CSSDeclaration>?,
        start: Int,
        end: Int
    ): TextStyler

    companion object {
        const val PLACE_HOLDER = "\uD83D\uDDBC\uFE0F"
    }
}