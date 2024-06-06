package com.ravenl.htmlannotator.compose.ext.cache

interface HtmlAnnotatorCache<R> {
    fun put(src: String, result: R)
    fun get(src: String): R?
}