package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import org.jsoup.nodes.Node

abstract class AbsImageHandler : TagHandler() {

    override fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        cssDeclarations: List<CSSDeclaration>?,
        node: Node,
        start: Int,
        end: Int
    ) {
        builder.append(PLACE_HOLDER)
        rangeList.add(getImageStyler(node.attr("src"), end, end + PLACE_HOLDER.length))
    }

    abstract fun getImageStyler(imageUrl: String, start: Int, end: Int): TagStyler

    companion object {
        const val PLACE_HOLDER = "\uD83D\uDDBC\uFE0F"
    }
}