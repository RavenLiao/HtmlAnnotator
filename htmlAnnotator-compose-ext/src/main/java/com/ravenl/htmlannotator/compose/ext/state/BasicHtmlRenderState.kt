package com.ravenl.htmlannotator.compose.ext.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.ravenl.htmlannotator.compose.HtmlAnnotator
import com.ravenl.htmlannotator.compose.HtmlAnnotatorCache
import com.ravenl.htmlannotator.compose.css.CSSAnnotatedHandler
import com.ravenl.htmlannotator.compose.ext.cache.rememberLruAnnotatorCache
import com.ravenl.htmlannotator.core.handler.TagHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun rememberHtmlAnnotator(
    preTagHandlers: Map<String, TagHandler>? = HtmlAnnotator.defaultPreTagHandlers,
    preCSSHandlers: Map<String, CSSAnnotatedHandler>? = HtmlAnnotator.defaultPreCSSHandlers,
    isStripExtraWhiteSpace: Boolean = HtmlAnnotator.defaultIsStripExtraWhiteSpace,
    cache: HtmlAnnotatorCache = rememberLruAnnotatorCache()
): HtmlAnnotator = remember(preTagHandlers, preCSSHandlers, isStripExtraWhiteSpace, cache) {
    HtmlAnnotator(cache, preTagHandlers, preCSSHandlers, isStripExtraWhiteSpace)
}

@Stable
abstract class BasicHtmlRenderState<R>(
    private val annotator: HtmlAnnotator,
    var buildHtml: suspend HtmlAnnotator.(html: String) -> R
) : RememberObserver {

    private var rememberedCount = 0
    private var coroutineScope: CoroutineScope? = null

    var srcHtml: String? by mutableStateOf(null)
    var isRendering: Boolean by mutableStateOf(false)
        private set
    var resultHtml: R? by mutableStateOf(null)
        private set

    override fun onRemembered() {
        rememberedCount++
        if (rememberedCount != 1) return

        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate).apply {
            coroutineScope = this
            launch {
                snapshotFlow { srcHtml }.filterNotNull().collectLatest { src ->
                    isRendering = true
                    resultHtml = withContext(Dispatchers.Default) {
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