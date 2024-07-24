package com.ravenl.htmlannotator.compose.handler

import com.ravenl.htmlannotator.compose.styler.NewLineStyler
import com.ravenl.htmlannotator.core.handler.AbsAppendLinesHandler
import com.ravenl.htmlannotator.core.model.TextStyler

class AppendLinesHandler(amount: Int) : AbsAppendLinesHandler(amount) {
    override fun getNewLineStyler(): TextStyler = NewLineStyler
}