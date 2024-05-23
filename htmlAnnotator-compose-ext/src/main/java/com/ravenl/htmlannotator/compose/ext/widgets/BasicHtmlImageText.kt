package com.ravenl.htmlannotator.compose.ext.widgets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.ravenl.htmlannotator.compose.ext.handler.ListItemAnnotatedHandler
import com.ravenl.htmlannotator.compose.ext.state.HtmlContentState
import com.ravenl.htmlannotator.compose.ext.state.rememberHtmlAnnotator
import com.ravenl.htmlannotator.compose.ext.state.rememberHtmlContentState
import com.ravenl.htmlannotator.compose.ext.styler.OrderedListStyler
import com.ravenl.htmlannotator.compose.ext.styler.UnorderedListStyler
import com.ravenl.htmlannotator.compose.styler.ImageAnnotatedStyler


@Composable
fun BasicHtmlImageText(
    html: String,
    imageContent: @Composable ColumnScope.(imgUrl: String) -> Unit,
    modifier: Modifier = Modifier,
    defaultStyle: TextStyle = TextStyle.Default,
    state: HtmlContentState = rememberHtmlContentState(
        splitTags = listOf(
            ImageAnnotatedStyler.TAG_NAME,
            OrderedListStyler.TAG_NAME,
            UnorderedListStyler.TAG_NAME
        ),
        annotator = rememberHtmlAnnotator(preTagHandlers = mapOf("li" to ListItemAnnotatedHandler()))
    ),
    renderTag: @Composable ColumnScope.(annotation: AnnotatedString.Range<String>, AnnotatedString) -> Unit = { annotation, string ->
        when (annotation.tag) {
            ImageAnnotatedStyler.TAG_NAME -> {
                imageContent(annotation.item)
            }
            OrderedListStyler.TAG_NAME, UnorderedListStyler.TAG_NAME -> {
                Row(Modifier.fillMaxWidth()) {
                    BasicText(
                        text = annotation.item,
                        Modifier
                            .width(20.dp)
                            .alignByBaseline(),
                        defaultStyle
                    )
                    BasicText(
                        text = string,
                        Modifier
                            .fillMaxWidth()
                            .alignByBaseline(),
                        defaultStyle
                    )
                }
            }
        }
    },
    renderDefault: @Composable ColumnScope.(AnnotatedString) -> Unit = { text ->
        BasicText(text, Modifier.fillMaxWidth(), defaultStyle)
    }
) = BasicHtmlContent(html, state = state, renderTag, modifier, defaultStyle, renderDefault)
