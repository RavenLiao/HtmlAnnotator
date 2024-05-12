package com.ravenl.htmlannotator.view.util

import android.content.res.Resources


object UnitUtil {
    val density: Float
        get() = Resources.getSystem().displayMetrics.density


    fun dpToPixel(dp: Float): Int = (dp * density).toInt()

    fun pixelToDp(px: Float): Int = (px / density).toInt()

}