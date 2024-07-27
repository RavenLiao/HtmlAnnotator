package com.ravenl.htmlannotator.view.handler

import com.ravenl.htmlannotator.core.handler.AbsAppendLinesHandler
import com.ravenl.htmlannotator.core.model.TextStyler
import com.ravenl.htmlannotator.view.styler.NewLineStyler

class AppendLinesHandler(amount: Int) : AbsAppendLinesHandler(amount) {
    override fun getNewLineStyler(): TextStyler = NewLineStyler
}