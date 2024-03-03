package com.ravenl.htmlannotator.core.css

data class CSSStyleBlock(val start: Int, val end: Int, val declarations: List<CSSDeclaration>)