package com.ravenl.htmlannotator.core.handler


import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.TextStyler

abstract class AbsImageHandler : TagHandler() {
    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        list.add(
            getImageStyler(
                node.attr("src"),
                cssDeclarations
            )
        )
    }

    abstract fun getImageStyler(
        imageUrl: String,
        cssDeclarations: List<CSSDeclaration>?,
    ): ImageStyler
}

abstract class ImageStyler(val imageUrl: String) : TextStyler {
    companion object {
        const val PLACE_HOLDER = "\uD83D\uDDBC\uFE0F"
    }
}