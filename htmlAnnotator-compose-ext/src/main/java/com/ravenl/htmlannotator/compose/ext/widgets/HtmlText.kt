package com.ravenl.htmlannotator.compose.ext.widgets

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import com.ravenl.htmlannotator.compose.ext.state.HtmlTextState
import com.ravenl.htmlannotator.compose.ext.state.rememberHtmlTextState


@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    state: HtmlTextState = rememberHtmlTextState(),
    style: TextStyle = TextStyle.Default,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
): Unit = with(state) {
    srcHtml = html

    resultHtml?.also { annotated ->
        BasicText(
            text = annotated,
            modifier = modifier,
            style = style,
            onTextLayout = onTextLayout,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            inlineContent = inlineContent
        )
    }
}