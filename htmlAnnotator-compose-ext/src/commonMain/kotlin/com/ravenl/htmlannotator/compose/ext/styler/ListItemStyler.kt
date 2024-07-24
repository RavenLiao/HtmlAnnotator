package com.ravenl.htmlannotator.compose.ext.styler

import com.ravenl.htmlannotator.compose.styler.IStringAnnotationStyler

class UnorderedListStyler : IStringAnnotationStyler {
    override fun getTag(): String = TAG_NAME

    override fun getAnnotation(): String = "•"

    companion object {
        const val TAG_NAME = "li-ul"
    }
}

class OrderedListStyler(private val index: Int) : IStringAnnotationStyler {
    override fun getTag(): String = TAG_NAME

    override fun getAnnotation(): String = "$index."

    companion object {
        const val TAG_NAME = "li-ol"
    }
}