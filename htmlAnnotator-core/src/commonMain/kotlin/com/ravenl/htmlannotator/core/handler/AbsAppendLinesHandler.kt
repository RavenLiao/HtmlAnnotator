package com.ravenl.htmlannotator.core.handler

import com.fleeksoft.ksoup.nodes.Node
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.model.TextStyler

abstract class AbsAppendLinesHandler(private val amount: Int) : TagHandler() {
    override fun addTagStylers(
        list: MutableList<TextStyler>,
        node: Node,
        cssDeclarations: List<CSSDeclaration>?
    ) {
        val styler = getNewLineStyler()
        repeat(amount) {
            list.add(styler)
        }
    }

    abstract fun getNewLineStyler(): TextStyler
}