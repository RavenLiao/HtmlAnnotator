package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger

open class TextAlignCssAnnotatedHandler : CSSAnnotatedHandler() {

    override fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String) {
        parse(value)?.also { align ->
            builder.addStyle(ParagraphStyle(textAlign = align), start, end)
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