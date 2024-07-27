package com.ravenl.htmlannotator.core.model

sealed class HtmlNode

data class StyleNode(val stylers: MutableList<TextStyler>?, val children: MutableList<HtmlNode>?) :
    HtmlNode()

data class StringNode(val string: String) : HtmlNode()