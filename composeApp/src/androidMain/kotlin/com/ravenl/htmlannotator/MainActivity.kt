package com.ravenl.htmlannotator

import BorderBox
import DemoScreen
import HtmlImageText
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DemoScreen { srcHtml ->
                {
                    item(contentType = 2, span = { GridItemSpan(maxCurrentLineSpan) }) {
                        BorderBox("Compose") {
                            HtmlImageText(
                                html = srcHtml,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

//                    item(contentType = 2) {
//                        BorderBox("view") {
//                            HtmlImageTextView(
//                                html = srcHtml,
//                                modifier = Modifier.fillMaxWidth()
//                            )
//                        }
//                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    DemoScreen()
}


//@Composable
//fun HtmlImageTextView(
//    html: String,
//    modifier: Modifier = Modifier,
//) {
//    val scope = rememberCoroutineScope()
//    AndroidView(
//        factory = { c ->
//            TextView(c).apply {
//                setTextColor(Color.Black.toArgb())
//            }
//        },
//        modifier = modifier,
//        onReset = {}
//    ) { view ->
//        if (view.tag != html) {
//            view.tag = html
//            val placeholder = view.context.getDrawable(R.drawable.ic_launcher_foreground)!!.apply {
//                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
//            }
//            scope.launch {
//                view.setHtmlImageText(html, placeholder)
//            }
//        }
//    }
//}