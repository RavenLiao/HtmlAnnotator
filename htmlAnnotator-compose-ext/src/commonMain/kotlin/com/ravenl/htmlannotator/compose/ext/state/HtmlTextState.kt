package com.ravenl.htmlannotator.compose.ext.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.compose.HtmlAnnotator
import com.ravenl.htmlannotator.compose.ext.cache.HtmlAnnotatorCache
import com.ravenl.htmlannotator.compose.ext.cache.rememberLifecycle


@Composable
fun rememberHtmlTextState(
    annotator: HtmlAnnotator = rememberHtmlAnnotator(),
    cache: HtmlAnnotatorCache<AnnotatedString> = rememberLifecycle(),
    buildHtml: suspend HtmlAnnotator.(html: String) -> AnnotatedString = { from(it) },
): HtmlTextState = remember(annotator, buildHtml) {
    HtmlTextState(annotator, cache, buildHtml)
}


@Stable
class HtmlTextState(
    annotator: HtmlAnnotator,
    cache: HtmlAnnotatorCache<AnnotatedString>,
    buildHtml: suspend HtmlAnnotator.(html: String) -> AnnotatedString
) : BasicHtmlRenderState<AnnotatedString>(annotator, cache, buildHtml)