package com.ravenl.htmlannotator.compose.ext.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.compose.HtmlAnnotator
import com.ravenl.htmlannotator.compose.ext.splitByAnnotation


@Composable
fun rememberHtmlContentState(
    splitTags: List<String>,
    annotator: HtmlAnnotator = rememberHtmlAnnotator(),
    buildHtml: suspend HtmlAnnotator.(html: String) -> List<AnnotatedString> = {
        from(it).splitByAnnotation(splitTags)
    },
): HtmlContentState = remember(annotator, buildHtml) {
    HtmlContentState(annotator, splitTags, buildHtml)
}


@Stable
class HtmlContentState(
    annotator: HtmlAnnotator,
    val splitTags: List<String>,
    buildHtml: suspend HtmlAnnotator.(html: String) -> List<AnnotatedString>
) : BasicHtmlRenderState<List<AnnotatedString>>(annotator, buildHtml)