package com.ravenl.htmlannotator.compose.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.AnnotatedString
import com.ravenl.htmlannotator.compose.HtmlAnnotator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun rememberHtmlRenderState(
    annotator: HtmlAnnotator = HtmlAnnotator(),
    buildHtml: suspend HtmlAnnotator.(html: String) -> AnnotatedString = { from(it) },
): HtmlRenderState<String> = remember(annotator, buildHtml) {
    HtmlRenderState(annotator, buildHtml)
}


@Stable
class HtmlRenderState<T>(
    private val annotator: HtmlAnnotator,
    private val buildHtml: suspend HtmlAnnotator.(html: T) -> AnnotatedString
) : RememberObserver {

    private var rememberedCount = 0
    private var coroutineScope: CoroutineScope? = null

    var srcHtml: T? by mutableStateOf(null)
    var isRendering: Boolean by mutableStateOf(false)
        private set
    var annotatedHtml: AnnotatedString? by mutableStateOf(null)
        private set

    override fun onRemembered() {
        rememberedCount++
        if (rememberedCount != 1) return

        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).apply {
            coroutineScope = this
            launch {
                snapshotFlow { srcHtml }.filterNotNull().collectLatest { src ->
                    isRendering = true
                    annotatedHtml = withContext(Dispatchers.Default) {
                        annotator.buildHtml(src)
                    }
                    isRendering = false
                }
            }
        }
    }

    override fun onAbandoned() = onForgotten()

    override fun onForgotten() {
        if (rememberedCount <= 0) return
        rememberedCount--
        if (rememberedCount != 0) return

        coroutineScope?.cancel()
        coroutineScope = null
    }
}