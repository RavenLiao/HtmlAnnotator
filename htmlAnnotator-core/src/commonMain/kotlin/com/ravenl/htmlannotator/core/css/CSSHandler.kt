package com.ravenl.htmlannotator.core.css

import com.ravenl.htmlannotator.core.model.TextStyler

interface CSSHandler {
    fun addStyle(list: MutableList<TextStyler>, value: String)
}