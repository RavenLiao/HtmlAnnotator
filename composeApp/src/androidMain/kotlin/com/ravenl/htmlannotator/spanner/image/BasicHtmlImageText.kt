package com.ravenl.htmlannotator.spanner.image

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.widget.TextView
import coil.Coil
import coil.request.ImageRequest
import com.ravenL.htmlannotator.spanner.replaceSpan
import com.ravenl.htmlannotator.view.HtmlSpanner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun buildLoadImageHtmlSpanner(
    placeHolder: Drawable,
) = run {
    val handler = ImageSpannedHandler { url, _ ->
        StatefulImageSpan.Loading(url, placeHolder)
    }
    HtmlSpanner(preTagHandlers = mapOf("img" to handler))
}

suspend fun TextView.setHtmlImageText(
    html: String,
    placeHolder: Drawable,
    error: Drawable = placeHolder,
    htmlSpanner: HtmlSpanner = buildLoadImageHtmlSpanner(placeHolder),
    buildHtml: suspend HtmlSpanner.(html: String) -> Spannable = { from(it) },
): Job = withContext(Dispatchers.Main) {
    launch {
        val spannable = htmlSpanner.buildHtml(html)
        text = spannable
        spannable.run {
            val loader = Coil.imageLoader(context)
            getSpans(0, spannable.length, StatefulImageSpan.Loading::class.java).map { span ->
                async(Dispatchers.IO) {
                    val request = ImageRequest.Builder(context)
                        .data(span.source)
                        .size(width, 1000)
                        .build()
                    replaceSpan(span, loader.execute(request).drawable.let { drawable ->
                        if (drawable != null) {
                            drawable.apply {
                                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                            }
                            StatefulImageSpan.Success(span.source!!, drawable)
                        } else {
                            StatefulImageSpan.Fail(span.source!!, error)
                        }
                    })
                }
            }.awaitAll()
        }
        text = spannable
    }
}
