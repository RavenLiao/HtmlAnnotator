package com.ravenl.htmlannotator.spanner.image

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import com.github.panpf.sketch.AndroidBitmapImage
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.sketch
import com.ravenl.htmlannotator.spanner.replaceSpan
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
            val loader = context.sketch
            getSpans(0, spannable.length, StatefulImageSpan.Loading::class.java).map { span ->
                async(Dispatchers.IO) {
                    val request = ImageRequest.Builder(context, span.source)
                        .resize(width, width)
                        .build()
                    val result =
                        (loader.execute(request).image as? AndroidBitmapImage)?.bitmap?.toDrawable(
                            context.resources
                        )
                    replaceSpan(span, result.let { drawable ->
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
