package com.ravenl.htmlannotator.core.css.model

internal data class CSSRuleSet(
    val origin: StyleOrigin,
    val selector: String,
    val declarations: List<CSSDeclarationWithImportant>,
)