package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString


object NewLineStyler : IAtChildrenBeforeAnnotatedStyler {

    override fun atChildrenBefore(builder: AnnotatedString.Builder) {
        builder.appendLine()
    }
}

