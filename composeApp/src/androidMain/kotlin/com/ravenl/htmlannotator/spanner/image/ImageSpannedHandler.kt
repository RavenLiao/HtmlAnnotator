package com.ravenl.htmlannotator.spanner.image

import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import androidx.core.text.inSpans
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.AbsImageHandler
import com.ravenl.htmlannotator.core.handler.ImageStyler
import com.ravenl.htmlannotator.view.styler.IBeforeChildrenSpannedStyler

class ImageSpannedHandler(
    val buildImageSpan: (url: String, css: List<CSSDeclaration>?) -> ImageSpan
) : AbsImageHandler() {


    override fun getImageStyler(
        imageUrl: String,
        cssDeclarations: List<CSSDeclaration>?
    ): ImageStyler = ImageSpannedStyler(imageUrl) {
        buildImageSpan(imageUrl, cssDeclarations)
    }

    class ImageSpannedStyler(imageUrl: String, val getSpan: () -> ImageSpan) :
        IBeforeChildrenSpannedStyler, ImageStyler(imageUrl) {

        override fun beforeChildren(builder: SpannableStringBuilder) {
            with(builder) {
                inSpans(getSpan()) {
                    append(PLACE_HOLDER)
                }
            }
        }
    }
}

