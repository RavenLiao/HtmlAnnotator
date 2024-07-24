package com.ravenl.htmlannotator.core.model

interface TextStyler {
    val inheritance: Boolean
        get() = true
}


interface CumulativeStyler : TextStyler {
    val name: String
    val value: String

    fun buildCumulative(parent: String): CumulativeStyler
}