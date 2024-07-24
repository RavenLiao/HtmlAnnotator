package com.ravenl.htmlannotator.compose.ext.widgets

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ravenl.htmlannotator.compose.ext.handler.AnnotatedMarginHandler
import com.ravenl.htmlannotator.compose.ext.handler.ListItemAnnotatedHandler
import com.ravenl.htmlannotator.compose.ext.state.HtmlContentState
import com.ravenl.htmlannotator.compose.ext.state.rememberHtmlAnnotator
import com.ravenl.htmlannotator.compose.ext.state.rememberHtmlContentState
import com.ravenl.htmlannotator.compose.ext.styler.MarginStyler
import com.ravenl.htmlannotator.compose.ext.styler.OrderedListStyler
import com.ravenl.htmlannotator.compose.ext.styler.UnorderedListStyler
import com.ravenl.htmlannotator.compose.styler.ImageAnnotatedStyler
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf


@OptIn(ExperimentalTextApi::class)
@Composable
fun BasicHtmlImageText(
    html: String?,
    imageContent: @Composable (ColumnScope.(imgUrl: String) -> Unit),
    modifier: Modifier = Modifier,
    defaultStyle: TextStyle = TextStyle.Default,
    getClickUrlAction: (() -> (url: String) -> Unit)? = null,
    splitTags: ImmutableList<String> = persistentListOf(
        ImageAnnotatedStyler.TAG_NAME,
        OrderedListStyler.TAG_NAME,
        UnorderedListStyler.TAG_NAME,
        MarginStyler.TOP,
        MarginStyler.LEFT,
        MarginStyler.RIGHT,
        MarginStyler.BOTTOM,
    ),
    state: HtmlContentState = rememberHtmlContentState(
        splitTags = splitTags,
        annotator = rememberHtmlAnnotator(
            preTagHandlers = mapOf(
                "li" to ListItemAnnotatedHandler(),
                "ul" to AnnotatedMarginHandler {
                    listOf(MarginStyler.Left("4em"))
                },
                "ol" to AnnotatedMarginHandler {
                    listOf(MarginStyler.Left("4em"))
                }
            )
        )
    ),
    renderDefault: @Composable ColumnScope.(AnnotatedString) -> Unit = { text ->
        ClickableText(text, Modifier.fillMaxWidth(), defaultStyle) { index ->
            text.getUrlAnnotations(index, index).firstOrNull()?.item?.url?.also { url ->
                getClickUrlAction?.invoke()?.invoke(url)
            }
        }
    },
    renderTag: @Composable ColumnScope.(annotation: AnnotatedString.Range<String>, AnnotatedString) -> Unit = { annotation, string ->
        defaultImageTextRenderTag(
            annotation,
            string,
            imageContent,
            defaultStyle,
            getClickUrlAction,
            renderDefault
        )
    }
) = BasicHtmlContent(
    html,
    state = state,
    renderTag,
    modifier,
    defaultStyle,
    renderDefault
)

@OptIn(ExperimentalTextApi::class)
@Composable
fun ColumnScope.defaultImageTextRenderTag(
    annotation: AnnotatedString.Range<String>,
    string: AnnotatedString,
    imageContent: @Composable (ColumnScope.(imgUrl: String) -> Unit),
    defaultStyle: TextStyle = TextStyle.Default,
    getClickUrlAction: (() -> (url: String) -> Unit)? = null,
    renderDefault: @Composable (ColumnScope.(AnnotatedString) -> Unit)
) {
    when (annotation.tag) {
        ImageAnnotatedStyler.TAG_NAME -> {
            imageContent(annotation.item)
        }
        OrderedListStyler.TAG_NAME, UnorderedListStyler.TAG_NAME -> {
            MarginWrapper(string, defaultStyle) {
                Row(Modifier.fillMaxWidth()) {
                    BasicText(
                        text = annotation.item,
                        Modifier
                            .width(20.dp)
                            .alignByBaseline(),
                        defaultStyle.copy(textAlign = TextAlign.Center)
                    )
                    ClickableText(
                        text = string,
                        Modifier
                            .fillMaxWidth()
                            .alignByBaseline(),
                        defaultStyle
                    ) { index ->
                        string.getUrlAnnotations(index, index)
                            .firstOrNull()?.item?.url?.also { url ->
                                getClickUrlAction?.invoke()?.invoke(url)
                            }
                    }
                }
            }
        }
        MarginStyler.TOP, MarginStyler.LEFT, MarginStyler.RIGHT, MarginStyler.BOTTOM -> {
            MarginWrapper(string, defaultStyle) {
                renderDefault(this@defaultImageTextRenderTag, string)
            }
        }
    }
}