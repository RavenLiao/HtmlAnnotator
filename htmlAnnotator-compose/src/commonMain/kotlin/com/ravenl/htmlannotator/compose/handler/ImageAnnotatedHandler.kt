package com.ravenl.htmlannotator.compose.handler

import com.ravenl.htmlannotator.compose.styler.ImageAnnotatedStyler
import com.ravenl.htmlannotator.core.css.model.CSSDeclaration
import com.ravenl.htmlannotator.core.handler.AbsImageHandler
import com.ravenl.htmlannotator.core.handler.ImageStyler

class ImageAnnotatedHandler : AbsImageHandler() {
    override fun getImageStyler(
        imageUrl: String,
        cssDeclarations: List<CSSDeclaration>?
    ): ImageStyler = ImageAnnotatedStyler(imageUrl)
}