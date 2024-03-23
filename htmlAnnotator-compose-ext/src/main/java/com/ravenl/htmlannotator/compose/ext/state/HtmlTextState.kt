package com.ravenl.htmlannotator.compose.ext.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.compose.HtmlAnnotator


@Composable
fun rememberHtmlTextState(
    annotator: HtmlAnnotator = rememberHtmlAnnotator(),
    buildHtml: suspend HtmlAnnotator.(html: String) -> AnnotatedString = { from(it) },
): HtmlTextState = remember(annotator, buildHtml) {
    HtmlTextState(annotator, buildHtml)
}


@Stable
class HtmlTextState(
    annotator: HtmlAnnotator,
    buildHtml: suspend HtmlAnnotator.(html: String) -> AnnotatedString
) : BasicHtmlRenderState<AnnotatedString>(annotator, buildHtml)