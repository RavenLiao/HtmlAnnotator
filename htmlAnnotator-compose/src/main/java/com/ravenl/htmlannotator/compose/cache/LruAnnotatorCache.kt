package com.ravenl.htmlannotator.compose.cache

import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class LruAnnotatorCache(lifecycle: Lifecycle, maxSize: Int = 3) : HtmlAnnotatorCache {
    val lruCache = object : LinkedHashMap<String, AnnotatedString>(maxSize, 1f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, AnnotatedString>?): Boolean {
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

    override fun put(src: String, result: AnnotatedString) {
        lruCache[src] = result
    }

    override fun get(src: String) = lruCache[src]
}