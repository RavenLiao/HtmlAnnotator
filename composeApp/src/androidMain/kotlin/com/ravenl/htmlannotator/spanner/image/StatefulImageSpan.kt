package com.ravenl.htmlannotator.spanner.image

import android.graphics.drawable.Drawable
import android.text.style.ImageSpan

sealed class StatefulImageSpan(url: String, drawable: Drawable) : ImageSpan(drawable, url) {
    class Loading(url: String, drawable: Drawable) : StatefulImageSpan(url, drawable)

    class Success(url: String, drawable: Drawable) : StatefulImageSpan(url, drawable)

    class Fail(url: String, drawable: Drawable) : StatefulImageSpan(url, drawable)
}