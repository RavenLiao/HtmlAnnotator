package com.ravenl.htmlannotator.compose.ext.cache

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class LruAnnotatorCache<R>(lifecycle: Lifecycle, maxSize: Int = 3) : HtmlAnnotatorCache<R> {
    private val lruCache = object : LinkedHashMap<String, R>(maxSize, 1f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, R>?): Boolean {
            return size > maxSize
        }
    }

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                lruCache.clear()
                owner.lifecycle.removeObserver(this)
            }
        })
    }

    override fun put(src: String, result: R) {
        lruCache[src] = result
    }

    override fun get(src: String) = lruCache[src]
}

@Composable
fun <R> rememberLruAnnotatorCache(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) = remember(lifecycleOwner) {
    LruAnnotatorCache<R>(lifecycleOwner.lifecycle)
}