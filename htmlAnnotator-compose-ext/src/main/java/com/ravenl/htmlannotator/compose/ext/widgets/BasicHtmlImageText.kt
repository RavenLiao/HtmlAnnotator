package com.ravenl.htmlannotator.compose.ext.widgets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.compose.ext.state.HtmlContentState
import com.ravenl.htmlannotator.compose.ext.state.rememberHtmlContentState
import com.ravenl.htmlannotator.compose.styler.ImageAnnotatedStyler


@Composable
fun BasicHtmlImageText(
    html: String,
    imageContent: @Composable ColumnScope.(imgUrl: String) -> Unit,
    modifier: Modifier = Modifier,
    state: HtmlContentState = rememberHtmlContentState(listOf(ImageAnnotatedStyler.TAG_NAME)),
    renderTag: @Composable ColumnScope.(annotation: AnnotatedString.Range<String>, AnnotatedString) -> Unit = { annotation, _ ->
        imageContent(annotation.item)
    },
    renderDefault: @Composable ColumnScope.(AnnotatedString) -> Unit = { text ->
        BasicText(text, Modifier.fillMaxWidth())
    }
) = BasicHtmlContent(html, state = state, renderTag, modifier, renderDefault)
