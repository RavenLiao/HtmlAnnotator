package com.ravenl.htmlannotator.compose.ext.widgets

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ravenl.htmlannotator.compose.ext.handler.ListItemAnnotatedHandler
import com.ravenl.htmlannotator.compose.ext.state.HtmlContentState
import com.ravenl.htmlannotator.compose.ext.state.rememberHtmlAnnotator
import com.ravenl.htmlannotator.compose.ext.state.rememberHtmlContentState
import com.ravenl.htmlannotator.compose.ext.styler.OrderedListStyler
import com.ravenl.htmlannotator.compose.ext.styler.UnorderedListStyler
import com.ravenl.htmlannotator.compose.styler.ImageAnnotatedStyler
import com.ravenl.htmlannotator.core.handler.TagHandler


@OptIn(ExperimentalTextApi::class)
@Composable
fun BasicHtmlImageText(
    html: String?,
    imageContent: @Composable ColumnScope.(imgUrl: String) -> Unit,
    modifier: Modifier = Modifier,
    defaultStyle: TextStyle = TextStyle.Default,
    getClickUrlAction: (() -> (url: String) -> Unit)? = null,
    state: HtmlContentState = rememberHtmlContentState(
        splitTags = listOf(
            ImageAnnotatedStyler.TAG_NAME,
            OrderedListStyler.TAG_NAME,
            UnorderedListStyler.TAG_NAME
        ),
        annotator = rememberHtmlAnnotator(
            preTagHandlers = mapOf(
                "li" to ListItemAnnotatedHandler(),
                "ul" to TagHandler(),
                "ol" to TagHandler()
            )
        )
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
    },
    renderDefault: @Composable ColumnScope.(AnnotatedString) -> Unit = { text ->
        ClickableText(text, Modifier.fillMaxWidth(), defaultStyle) { index ->
            text.getUrlAnnotations(index, index).firstOrNull()?.item?.url?.also { url ->
                getClickUrlAction?.invoke()?.invoke(url)
            }
        }
    },
    transitionSpec: AnimatedContentTransitionScope<List<AnnotatedString>?>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(220)).togetherWith(fadeOut(animationSpec = tween(90)))
    },
    animatedAlignment: Alignment = Alignment.Center,
    placeHolder: (@Composable ColumnScope.() -> Unit)? = null
) = BasicHtmlContent(
    html,
    state = state,
    renderTag,
    modifier,
    defaultStyle,
    renderDefault,
    transitionSpec,
    animatedAlignment,
    placeHolder
)
