package com.ravenl.htmlannotator.core.model

interface NodeProcessor {
    suspend fun processNode(node: HtmlNode)
}