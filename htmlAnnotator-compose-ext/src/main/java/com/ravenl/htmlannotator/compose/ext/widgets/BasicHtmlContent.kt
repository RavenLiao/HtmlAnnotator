package com.ravenl.htmlannotator.compose.ext.widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.ravenl.htmlannotator.compose.ext.state.HtmlContentState


@Composable
fun BasicHtmlContent(
    html: String?,
    state: HtmlContentState,
    renderTag: @Composable ColumnScope.(annotation: AnnotatedString.Range<String>, AnnotatedString) -> Unit,
    modifier: Modifier = Modifier,
    defaultStyle: TextStyle = TextStyle.Default,
    renderDefault: @Composable ColumnScope.(AnnotatedString) -> Unit = { text ->
        BasicText(text, Modifier.fillMaxWidth(), defaultStyle)
    },
    transitionSpec: AnimatedContentTransitionScope<List<AnnotatedString>?>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(220)).togetherWith(fadeOut(animationSpec = tween(90)))
    },
    animatedAlignment: Alignment = Alignment.Center,
    placeHolder: (@Composable ColumnScope.() -> Unit)? = null
): Unit = with(state) {
    srcHtml = html
    AnimatedContent(
        targetState = resultHtml,
        label = "HtmlSwitch",
        modifier = modifier,
        transitionSpec = transitionSpec,
        contentAlignment = animatedAlignment
    ) { result ->
        Column(Modifier.fillMaxWidth()) {
            if (result == null) {
                placeHolder?.invoke(this)
            } else {
                val tags = state.splitTags
                result.forEach { string ->
                    var annotation: AnnotatedString.Range<String>? = null
                    for (tag in tags) {
                        annotation =
                            string.getStringAnnotations(tag, 0, string.length).firstOrNull()
                        if (annotation != null) {
                            break
                        }
                    }
                    if (annotation == null) {
                        renderDefault(string)
                    } else {
                        renderTag(annotation, string)
                    }
                }
            }
        }
    }
}