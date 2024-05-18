package com.ravenL.htmlannotator

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.ravenL.htmlannotator.spanner.image.setHtmlImageText
import com.ravenL.htmlannotator.ui.theme.HtmlAnnotatorTheme
import com.ravenl.htmlannotator.compose.ext.widgets.BasicHtmlImageText
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HtmlAnnotatorTheme {
                Screen()
            }
        }
    }
}

val htmlList = listOf(
    "colors" to """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Color CSS Handler Test</title>
            <style>
                .color-text {
                    font-size: 18px;
                    margin: 5px;
                }
            </style>
        </head>
        <body>
            <h2>Named Colors</h2>
            <div class="color-text" style="color: red;">This text is in red color.</div>
            <div class="color-text" style="color: orange;">This text is in orange color.</div>
            <div class="color-text" style="color: tan;">This text is in tan color.</div>
            <div class="color-text" style="color: MediumPurple;">This text is in MediumPurple color.</div>

            <h2>Hex Colors</h2>
            <div class="color-text" style="color: #090;">This text is in #090 color.</div>
            <div class="color-text" style="color: #009900;">This text is in #009900 color.</div>
            <div class="color-text" style="color: #090a;">This text is in #090a color.</div>
            <div class="color-text" style="color: #009900aa;">This text is in #009900aa color.</div>

            <h2>RGB Colors</h2>
            <div class="color-text" style="color: rgb(34, 12, 64, 0.6);">This text is in rgb(34, 12, 64, 0.6) color.</div>
            <div class="color-text" style="color: rgba(34, 12, 64, 0.6);">This text is in rgba(34, 12, 64, 0.6) color.</div>
            <div class="color-text" style="color: rgb(34 12 64 / 0.6);">This text is in rgb(34 12 64 / 0.6) color.</div>
            <div class="color-text" style="color: rgba(34 12 64 / 0.3);">This text is in rgba(34 12 64 / 0.3) color.</div>
            <div class="color-text" style="color: rgb(34 12 64 / 60%);">This text is in rgb(34 12 64 / 60%) color.</div>
            <div class="color-text" style="color: rgba(34.6 12 64 / 30%);">This text is in rgba(34.6 12 64 / 30%) color.</div>

            <h2>HSL Colors</h2>
            <div class="color-text" style="color: hsl(30, 100%, 50%, 0.6);">This text is in hsl(30, 100%, 50%, 0.6) color.</div>
            <div class="color-text" style="color: hsla(30, 100%, 50%, 0.6);">This text is in hsla(30, 100%, 50%, 0.6) color.</div>
            <div class="color-text" style="color: hsl(30 100% 50% / 0.6);">This text is in hsl(30 100% 50% / 0.6) color.</div>
            <div class="color-text" style="color: hsla(30 100% 50% / 0.6);">This text is in hsla(30 100% 50% / 0.6) color.</div>
            <div class="color-text" style="color: hsl(30 100% 50% / 60%);">This text is in hsl(30 100% 50% / 60%) color.</div>
            <div class="color-text" style="color: hsla(30.2 100% 50% / 60%);">This text is in hsla(30.2 100% 50% / 60%) color.</div>

            <h2>HWB Colors</h2>
            <div class="color-text" style="color: hwb(90 10% 10%);">This text is in hwb(90 10% 10%) color.</div>
            <div class="color-text" style="color: hwb(90 10% 10% / 0.5);">This text is in hwb(90 10% 10% / 0.5) color.</div>
            <div class="color-text" style="color: hwb(90deg 10% 10%);">This text is in hwb(90deg 10% 10%) color.</div>
            <div class="color-text" style="color: hwb(1.5708rad 60% 0%);">This text is in hwb(1.5708rad 60% 0%) color.</div>
            <div class="color-text" style="color: hwb(0.25turn 0% 40% / 50%);">This text is in hwb(0.25turn 0% 40% / 50%) color.</div>
        </body>
        </html>
    """.trimIndent(),
    "fonts" to """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="UTF-8">
        <title>HTML Example</title>
        <style>
            em, i, cite, dfn { font-style: italic; }
            b, strong { font-weight: bold; }
            blockquote, ul, ol { text-indent: 3em; }
            h1 { font-weight: bold; font-size: 2em; }
            h2 { font-weight: bold; font-size: 1.5em; }
            h3 { font-weight: bold; font-size: 1.17em; }
            h4 { font-weight: bold; font-size: 1em; }
            h5 { font-weight: bold; font-size: 0.83em; }
            h6 { font-weight: bold; font-size: 0.67em; }
            tt { font-family: monospace; }
            big { font-weight: bold; font-size: 1.25em; }
            small { font-weight: bold; font-size: 0.8em; }
            sub { vertical-align: sub; font-size: 0.7em; }
            sup { vertical-align: super; font-size: 0.7em; }
            .center { text-align: center; }
        </style>
        </head>
        <body>
        
        <em>Italic Text</em><br>
        <b>Bold Text</b><br>
        <blockquote>Blockquote Text</blockquote>
        <ul>
            <li>List Item</li>
        </ul>
        <ol>
            <li>Ordered List Item</li>
        </ol>
        <p>Paragraph Text</p>
        <div>Div Text</div>
        <h1>Heading 1</h1>
        <h2>Heading 2</h2>
        <h3>Heading 3</h3>
        <h4>Heading 4</h4>
        <h5>Heading 5</h5>
        <h6>Heading 6</h6>
        <tt>Teletype Text</tt>
        <pre>Preformatted Text</pre>
        <big>Big Text</big>
        <small>Small Text</small>
        <sub>Subscript Text</sub>
        <sup>Superscript Text</sup>
        <div class="center">Centered Text</div>
        </body>
        </html>
    """.trimIndent(),
    "CSS Specificity" to """
        <!DOCTYPE html>
        <html>
        <head>
            <title>CSS Specificity Example</title>
            <style>
                p {
                    color: green;
                }
        
                .paragraph {
                    color: blue;
                }
        
                #special-paragraph {
                    color: red;
                }
        
                span {
                    color: purple;
                }
        
                div {
                    color: cyan !important;
                }
            </style>
        </head>
        <body>
            <p class="paragraph" id="special-paragraph">This is a paragraph with different specificity rules.</p>
            <span style="color: black;">This is a span with an inline style.</span>
            <div>This is a div with an !important rule.</div>
        </body>
        </html>
 
    """.trimIndent(),
    "image" to """
    <p>Microsoft is a renowned technology company founded in 1975, headquartered in Redmond, Washington, United States. It primarily operates in the fields of computer software, consumer electronics, and personal computers.</p>
    <a href="https://www.microsoft.com" title="Microsoft Website" target="_blank" rel="noopener noreferrer">
      <img alt="Microsoft Logo" itemprop="logo" class="c-image" src="https://img-prod-cms-rt-microsoft-com.akamaized.net/cms/api/am/imageFileData/RE1Mu3b?ver=5c31" role="presentation" aria-label="Microsoft Logo" style="overflow-x: visible; max-width: 100%; height: auto;">
    </a>
    <p>Microsoft's core products include the Windows operating system, Office productivity suite, Internet Explorer and Edge web browsers, and the Xbox gaming console. In recent years, Microsoft has also made significant investments and innovations in cloud computing, artificial intelligence, and other areas.</p>
    <p>As one of the leading companies in the technology industry, Microsoft has been driving the development and application of information technology, providing outstanding products and services to billions of users worldwide. Its influence is not only reflected in the business realm but has also deeply integrated into people's work and daily lives.</p>   
    """.trimIndent(),
    "ol & ul" to   """
        <!DOCTYPE html>
        <html>
        <body>

        <h1>The ol and ul elements</h1>

        <p>The ol element defines an ordered list:</p>
        <ol>
          <li>Coffee11111111111111111111111111111111111111111111111111111111111</li>
          <li>Tea</li>
          <ul>
          <li>Coffee</li>
          <li>Tea</li>
          <li>Milk</li>
        </ul>
          <li>Milk2222222222222222222222222222222222222222222222222222</li>
        </ol>

        <p>The ul element defines an unordered list:</p>
        <ul>
          <li>Coffee</li>
          <li>Tea</li>
          <ol>
          <li>Coffee</li>
          <li>Tea</li>
          <ul>
          <li>Coffee1111111111111111111111111111111111111111111111111111111111111111111111111111111</li>
          <li>Tea</li>
          <li>Milk</li>
        </ul>
          <li>Milk</li>
        </ol>
          <li>Milk</li>
        </ul>

        </body>
        </html>
    """.trimIndent()
)

@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    HtmlAnnotatorTheme {
        Screen()
    }
}

@Composable
fun Screen() {
    var srcHtml by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        TextField(
            value = srcHtml,
            onValueChange = { srcHtml = it },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
        )

        LazyRow(
            Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(htmlList) {
                Button(onClick = { srcHtml = it.second }) {
                    Text(text = it.first)
                }
            }
        }

        LazyVerticalGrid(
            GridCells.Fixed(2),
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                BorderBox("Compose") {
                    HtmlImageText(
                        html = srcHtml,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                BorderBox("view") {
                    HtmlImageTextView(
                        html = srcHtml,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun BorderBox(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title)
        Column(Modifier.border(1.dp, Color.Blue)) {
            content()
        }
    }
}

@Composable
fun HtmlImageText(
    html: String,
    modifier: Modifier = Modifier,
    imageContent: @Composable ColumnScope.(imgUrl: String) -> Unit = {
        AsyncImage(
            it,
            null,
            Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentScale = ContentScale.FillWidth
        )
    },
    renderDefault: @Composable ColumnScope.(AnnotatedString) -> Unit = { text ->
        BasicText(
            text,
            Modifier.fillMaxWidth(),
            style = TextStyle.Default.copy(color = Color.Black)
        )
    }
) = BasicHtmlImageText(
    html = html,
    imageContent = imageContent,
    modifier = modifier,
    renderDefault = renderDefault
)


@Composable
fun HtmlImageTextView(
    html: String,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    AndroidView(
        factory = { c ->
            TextView(c).apply {
                setTextColor(Color.Black.toArgb())
            }
        },
        modifier = modifier,
        onReset = {}
    ) { view ->
        if (view.tag != html) {
            view.tag = html
            val placeholder = view.context.getDrawable(R.drawable.ic_launcher_foreground)!!.apply {
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            }
            scope.launch {
                view.setHtmlImageText(html, placeholder)
            }
        }
    }
}