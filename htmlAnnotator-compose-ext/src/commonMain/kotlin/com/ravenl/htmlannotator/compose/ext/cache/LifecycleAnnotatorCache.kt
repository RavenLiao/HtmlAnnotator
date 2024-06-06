package com.ravenl.htmlannotator.compose.ext.cache

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class LifecycleAnnotatorCache<R>(lifecycle: Lifecycle) : HtmlAnnotatorCache<R> {
    private val cache = HashMap<String, R>()

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                cache.clear()
                owner.lifecycle.removeObserver(this)
            }
        })
    }

    override fun put(src: String, result: R) {
        cache[src] = result
    }

    override fun get(src: String) = cache[src]
}

@Composable
fun <R> rememberLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) = remember(lifecycleOwner) {
    LifecycleAnnotatorCache<R>(lifecycleOwner.lifecycle)
}