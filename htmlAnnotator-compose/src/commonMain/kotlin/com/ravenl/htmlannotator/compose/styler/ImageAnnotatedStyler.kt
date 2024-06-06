package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString

class ImageAnnotatedStyler(private val imageUrl: String, start: Int, end: Int) :
    AnnotatedStyler(start, end) {

    override fun addStyle(builder: AnnotatedString.Builder) {
        builder.addStringAnnotation(TAG_NAME, imageUrl, start, end)
    }

    companion object {
        const val TAG_NAME = "img"
    }
}