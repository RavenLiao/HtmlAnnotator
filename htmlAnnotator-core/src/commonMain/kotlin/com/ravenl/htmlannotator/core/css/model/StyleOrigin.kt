package com.ravenl.htmlannotator.core.css.model

internal enum class StyleOrigin(val value: Int) {
    EXTERNAL(0),
    INTERNAL(1),
    INLINE(2)
}