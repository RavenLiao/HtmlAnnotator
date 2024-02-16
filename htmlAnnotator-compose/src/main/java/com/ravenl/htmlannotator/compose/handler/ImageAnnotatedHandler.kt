package com.ravenl.htmlannotator.compose.handler

import com.ravenl.htmlannotator.compose.styler.ImageAnnotatedStyler
import com.ravenl.htmlannotator.core.TagStyler
import com.ravenl.htmlannotator.core.handler.AbsImageHandler

class ImageAnnotatedHandler : AbsImageHandler() {
    override fun getImageStyler(imageUrl: String, start: Int, end: Int): TagStyler =
        ImageAnnotatedStyler(imageUrl, start, end)
}