package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger
import com.ravenl.htmlannotator.compose.styler.ParagraphStyleStyler
import com.ravenl.htmlannotator.core.model.TextStyler

open class TextAlignCssAnnotatedHandler : CSSAnnotatedHandler() {
    override fun addStyle(list: MutableList<TextStyler>, value: String) {
        parse(value)?.also { align ->
            list.add(ParagraphStyleStyler { ParagraphStyle(textAlign = align) })
        }
    }

    internal open fun parse(value: String): TextAlign? = when (value) {
        "start" -> TextAlign.Start
        "end" -> TextAlign.End
        "left" -> TextAlign.Left
        "right" -> TextAlign.Right
        "center" -> TextAlign.Center
        "justify", "justify-all" -> TextAlign.Justify
        else -> {
            logger.w(MODULE) {
                "parse TextAlign fail: $value"
            }
            null
        }
    }


    companion object {
        const val MODULE = "TextAlignCssAnnotatedHandler"
    }
}