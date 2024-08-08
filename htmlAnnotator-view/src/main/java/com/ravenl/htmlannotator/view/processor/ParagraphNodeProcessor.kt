package com.ravenl.htmlannotator.view.processor

import com.ravenl.htmlannotator.core.model.HtmlNode
import com.ravenl.htmlannotator.core.model.NodeProcessor
import com.ravenl.htmlannotator.core.model.StringNode
import com.ravenl.htmlannotator.core.model.StyleNode
import com.ravenl.htmlannotator.view.styler.ParagraphEndStyler
import com.ravenl.htmlannotator.view.styler.ParagraphStartStyler
import kotlinx.coroutines.yield

object ParagraphNodeProcessor : NodeProcessor {

    override suspend fun processNode(node: HtmlNode) {
        yield()
        when (node) {
            is StringNode -> {
                return
            }
            is StyleNode -> {
                val children = node.children
                if (children.isNullOrEmpty()) {
                    return
                }

                for (i in children.indices) {
                    val currentNode = children[i]

                    if (currentNode !is StyleNode) continue
                    val currentStylers = currentNode.stylers ?: continue
                    val previousNode = if (i > 0) {
                        children[i - 1] as? StyleNode
                    } else {
                        null
                    }

                    fun isPreviousNodeHadEnd(extraLine: Boolean) =
                        previousNode?.stylers?.any { it is ParagraphEndStyler && it.extraLine == extraLine }

                    val isPreviousNodeHadEndWithExtraLine = isPreviousNodeHadEnd(true) == true
                    val isPreviousNodeHadEndNoExtraLine = isPreviousNodeHadEnd(false) == true

                    if (isPreviousNodeHadEndWithExtraLine
                        || isPreviousNodeHadEndNoExtraLine && currentStylers.any { it is ParagraphStartStyler && !it.extraLine }
                    ) {
                        currentStylers.removeAll { it is ParagraphStartStyler }
                    }
                    if (isPreviousNodeHadEndNoExtraLine) {
                        for (index in currentStylers.indices) {
                            val styler = currentStylers[index]
                            if (styler is ParagraphStartStyler && styler.extraLine) {
                                currentStylers[index] = ParagraphStartStyler(false)
                            }
                        }
                    }

                    // Recursively handle children of the current node
                    processNode(currentNode)
                }
            }
        }
    }
}