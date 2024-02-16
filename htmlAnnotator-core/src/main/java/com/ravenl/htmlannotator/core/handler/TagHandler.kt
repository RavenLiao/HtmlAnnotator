package com.ravenl.htmlannotator.core.handler

import com.ravenl.htmlannotator.core.TagStyler
import org.jsoup.nodes.Node

abstract class TagHandler {

    open fun beforeChildren(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        node: Node
    ) {
    }

    open fun handlerRendersContent(): Boolean = false

    abstract fun handleTagNode(
        builder: StringBuilder,
        rangeList: MutableList<TagStyler>,
        node: Node,
        start: Int,
        end: Int
    )

}