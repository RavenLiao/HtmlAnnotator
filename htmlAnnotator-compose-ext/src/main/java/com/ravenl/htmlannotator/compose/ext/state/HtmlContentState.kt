package com.ravenl.htmlannotator.compose.ext.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.compose.HtmlAnnotator
import com.ravenl.htmlannotator.compose.ext.cache.HtmlAnnotatorCache
import com.ravenl.htmlannotator.compose.ext.cache.rememberLruAnnotatorCache
import com.ravenl.htmlannotator.compose.ext.splitByAnnotation
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList


@Composable
fun rememberHtmlContentState(
    splitTags: List<String>,
    annotator: HtmlAnnotator = rememberHtmlAnnotator(),
    cache: HtmlAnnotatorCache<List<AnnotatedString>> = rememberLruAnnotatorCache(),
    buildHtml: suspend HtmlAnnotator.(html: String) -> List<AnnotatedString> = {
        from(it).splitByAnnotation(splitTags)
    },
): HtmlContentState = remember(annotator, buildHtml) {
    HtmlContentState(annotator, cache, splitTags, buildHtml)
}


@Stable
class HtmlContentState(
    annotator: HtmlAnnotator,
    cache: HtmlAnnotatorCache<List<AnnotatedString>>,
    val splitTags: ImmutableList<String>,
    buildHtml: suspend HtmlAnnotator.(html: String) -> List<AnnotatedString>
) : BasicHtmlRenderState<List<AnnotatedString>>(annotator, cache, buildHtml) {
    constructor(
        annotator: HtmlAnnotator,
        cache: HtmlAnnotatorCache<List<AnnotatedString>>,
        splitTags: List<String>,
        buildHtml: suspend HtmlAnnotator.(html: String) -> List<AnnotatedString>
    ) : this(annotator, cache, splitTags.toImmutableList(), buildHtml)
}