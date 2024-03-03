package com.ravenl.htmlannotator.compose.css

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import com.ravenl.htmlannotator.compose.HtmlAnnotator.Companion.logger

open class ColorCssHandler : CSSHandler() {

    override fun addCss(builder: AnnotatedString.Builder, start: Int, end: Int, value: String) {
        parseColor(value)?.also { color ->
            builder.addStyle(SpanStyle(color = color), start, end)
        }
    }

    private val blankRegex by lazy { "\\s+".toRegex() }

    private fun parseColor(cssColor: String): Color? = when {
        cssColor.startsWith("#") -> {
            runCatching {
                val cleanHex = cssColor.removePrefix("#")
                when (cleanHex.length) {
                    3, 4 -> {
                        //#RGB or #RGBA
                        val r = cleanHex[0].toString().repeat(2)
                        val g = cleanHex[1].toString().repeat(2)
                        val b = cleanHex[2].toString().repeat(2)
                        val a = if (cleanHex.length == 4) cleanHex[3].toString().repeat(2) else "FF"
                        parseHexColor(r, g, b, a)
                    }
                    6, 8 -> {
                        // #RRGGBB or #RRGGBBAA
                        val r = cleanHex.substring(0, 2)
                        val g = cleanHex.substring(2, 4)
                        val b = cleanHex.substring(4, 6)
                        val a = if (cleanHex.length == 8) cleanHex.substring(6, 8) else "FF"
                        parseHexColor(r, g, b, a)
                    }
                    else -> throw IllegalArgumentException("Illegal length")
                }
            }.onFailure {
                logger.e(MODULE, it) {
                    "Failed to parse the hex color: $cssColor"
                }
            }.getOrNull()
        }
        cssColor.startsWith("rgb") || cssColor.startsWith("rgba") -> {
            runCatching {
                val rgba = splitColorValue(cssColor)
                val r = rgba[0].toFloat() / 0xFF
                val g = rgba[1].toFloat() / 0xFF
                val b = rgba[2].toFloat() / 0xFF
                val a = if (rgba.size == 4) parseAlpha(rgba[3]) else 1f
                Color(r, g, b, a)
            }.onFailure {
                logger.e(MODULE, it) {
                    "Failed to parse the rgba color: $cssColor"
                }
            }.getOrNull()
        }
        cssColor.startsWith("hsl") || cssColor.startsWith("hsla") -> {
            runCatching {
                val hsla = splitColorValue(cssColor)
                val h = hsla[0].toFloat()
                val s = hsla[1].removeSuffix("%").toFloat() / 100
                val l = hsla[2].removeSuffix("%").toFloat() / 100
                val a = if (hsla.size == 4) parseAlpha(hsla[3]) else 1f
                Color.hsl(h, s, l, a)
            }.onFailure {
                logger.e(MODULE, it) {
                    "Failed to parse the hsla color: $cssColor"
                }
            }.getOrNull()
        }
        else -> when (cssColor.trim().lowercase()) {
            "black" -> Color.Black
            "darkgray" -> Color.DarkGray
            "gray" -> Color.Gray
            "lightgray" -> Color.LightGray
            "white" -> Color.White
            "red" -> Color.Red
            "green" -> Color.Green
            "blue" -> Color.Blue
            "yellow" -> Color.Yellow
            "cyan" -> Color.Cyan
            "magenta" -> Color.Magenta
            else -> parseOtherColor(cssColor)
        }
    }

    private fun splitColorValue(cssColor: String) = cssColor.substringAfter("(")
        .substringBefore(")")
        .replace(",", " ")
        .replace("/", " ")
        .split(blankRegex)

    open fun parseOtherColor(cssColor: String): Color? {
        logger.w(MODULE) {
            "unsupported parse color: $cssColor"
        }
        return null
    }

    private fun parseAlpha(alpha: String): Float =
        if (alpha.endsWith("%")) {
            alpha.removeSuffix("%").toFloat() / 100
        } else {
            alpha.toFloat()
        }

    private fun parseHexColor(r: String, g: String, b: String, a: String) =
        Color(
            Integer.parseInt(r, 16),
            Integer.parseInt(g, 16),
            Integer.parseInt(b, 16),
            Integer.parseInt(a, 16)
        )

    companion object {
        const val MODULE = "ColorCssHandler"
    }
}