package com.ravenL.htmlannotator.spanner.image

import android.text.style.ImageSpan
import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.AbsImageHandler
import com.ravenl.htmlannotator.view.styler.SpannedStyler

class ImageSpannedHandler(
    val buildImageSpan: (url: String, css: List<CSSDeclaration>?) -> ImageSpan
) : AbsImageHandler() {
    override fun getImageStyler(
        imageUrl: String,
        cssDeclarations: List<CSSDeclaration>?,
        start: Int,
        end: Int
    ): TextStyler = SpannedStyler(start, end, buildImageSpan(imageUrl, cssDeclarations))
}