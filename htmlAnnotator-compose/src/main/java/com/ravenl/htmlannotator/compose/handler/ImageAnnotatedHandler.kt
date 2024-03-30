package com.ravenl.htmlannotator.compose.handler

import com.ravenl.htmlannotator.compose.styler.ImageAnnotatedStyler
import com.ravenl.htmlannotator.core.TextStyler
import com.ravenl.htmlannotator.core.handler.AbsImageHandler

class ImageAnnotatedHandler : AbsImageHandler() {
    override fun getImageStyler(imageUrl: String, start: Int, end: Int): TextStyler =
        ImageAnnotatedStyler(imageUrl, start, end)
}