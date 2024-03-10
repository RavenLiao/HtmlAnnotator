package com.ravenl.htmlannotator.core.css

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.ravenl.htmlannotator.core.util.Logger
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val MODULE = "CSSColorParser"

class CSSColorParser(private val logger: Logger) {
    private val blankRegex by lazy { "\\s+".toRegex() }

    @ColorInt
    fun parseColor(cssColor: String): Int? = when {
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
                val r = rgba[0].toFloat().roundToInt()
                val g = rgba[1].toFloat().roundToInt()
                val b = rgba[2].toFloat().roundToInt()
                val a = if (rgba.size == 4) {
                    (parseAlpha(rgba[3]) * 0xFF).toInt()
                } else {
                    0xFF
                }
                Color.argb(a, r, g, b)
            }.onFailure {
                logger.e(MODULE, it) {
                    "Failed to parse the rgba color: $cssColor"
                }
            }.getOrNull()
        }
        cssColor.startsWith("hsl") || cssColor.startsWith("hsla") -> {
            runCatching {
                val hsla = splitColorValue(cssColor)
                val h = parseHue(hsla[0])
                val s = hsla[1].removeSuffix("%").toFloat() / 100
                val l = hsla[2].removeSuffix("%").toFloat() / 100
                val a = if (hsla.size == 4) parseAlpha(hsla[3]) else 1f
                parseHSLColor(h, s, l, a)
            }.onFailure {
                logger.e(MODULE, it) {
                    "Failed to parse the hsla color: $cssColor"
                }
            }.getOrNull()
        }
        cssColor.startsWith("hwb") -> {
            runCatching {
                val hwba = splitColorValue(cssColor)
                val h = parseHue(hwba[0])
                val w = hwba[1].removeSuffix("%").toFloat() / 100
                val b = hwba[2].removeSuffix("%").toFloat() / 100
                val a = if (hwba.size == 4) parseAlpha(hwba[3]) else 1f
                parseHWBColor(h, w, b, a)
            }.onFailure {
                logger.e(MODULE, it) {
                    "Failed to parse the hwb color: $cssColor"
                }
            }.getOrNull()
        }
        else -> parseColorName(cssColor)
    }

    private fun splitColorValue(cssColor: String) = cssColor.substringAfter("(")
        .substringBefore(")")
        .replace(",", " ")
        .replace("/", " ")
        .split(blankRegex)

    private fun parseHue(hue: String): Float = when {
        hue.endsWith("deg") -> hue.removeSuffix("deg").toFloat()
        hue.endsWith("rad") -> Math.toDegrees(hue.removeSuffix("rad").toDouble()).toFloat()
        hue.endsWith("turn") -> hue.removeSuffix("turn").toFloat() * 360
        else -> hue.toFloat()
    }

    private fun parseAlpha(alpha: String): Float =
        if (alpha.endsWith("%")) {
            alpha.removeSuffix("%").toFloat() / 100
        } else {
            alpha.toFloat()
        }

    @ColorInt
    private fun parseHexColor(r: String, g: String, b: String, a: String) =
        Color.argb(
            Integer.parseInt(a, 16),
            Integer.parseInt(r, 16),
            Integer.parseInt(g, 16),
            Integer.parseInt(b, 16),
        )

    @ColorInt
    private fun parseHSLColor(
        @FloatRange(0.0, 360.0) hue: Float,
        @FloatRange(0.0, 1.0) saturation: Float,
        @FloatRange(0.0, 1.0) lightness: Float,
        @FloatRange(0.0, 1.0) alpha: Float
    ): Int {
        fun hslToRgbComponent(n: Int, h: Float, s: Float, l: Float): Float {
            val k = (n.toFloat() + h / 30f) % 12f
            val a = s * min(l, 1f - l)
            return l - a * max(-1f, minOf(k - 3, 9 - k, 1f))
        }

        val red = (hslToRgbComponent(0, hue, saturation, lightness) * 0xFF).toInt()
        val green = (hslToRgbComponent(8, hue, saturation, lightness) * 0xFF).toInt()
        val blue = (hslToRgbComponent(4, hue, saturation, lightness) * 0xFF).toInt()
        return Color.argb((alpha * 0xFF).toInt(), red, green, blue)
    }

    @ColorInt
    private fun parseHWBColor(
        @FloatRange(0.0, 360.0) hub: Float,
        @FloatRange(0.0, 1.0) white: Float,
        @FloatRange(0.0, 1.0) black: Float,
        @FloatRange(0.0, 1.0) alpha: Float
    ): Int {
        val sum = white + black
        var w = white
        var b = black
        if (sum > 1) {
            val scale = 1 / sum
            w *= scale
            b *= scale
        }
        val l = (1 - black) / 2
        val s = if (l == 0f || l == 1f) 0f else (1 - w - b) / (1 - abs(2 * l - 1))
        return parseHSLColor(hub, s, l, alpha)
    }


    private fun parseColorName(cssColor: String) = when (cssColor.trim().lowercase()) {
        "aliceblue" -> Color.rgb(240, 248, 255)
        "antiquewhite" -> Color.rgb(250, 235, 215)
        "aqua" -> Color.rgb(0, 255, 255)
        "aquamarine" -> Color.rgb(127, 255, 212)
        "azure" -> Color.rgb(240, 255, 255)
        "beige" -> Color.rgb(245, 245, 220)
        "bisque" -> Color.rgb(255, 228, 196)
        "black" -> Color.rgb(0, 0, 0)
        "blanchedalmond" -> Color.rgb(255, 235, 205)
        "blue" -> Color.rgb(0, 0, 255)
        "blueviolet" -> Color.rgb(138, 43, 226)
        "brown" -> Color.rgb(165, 42, 42)
        "burlywood" -> Color.rgb(222, 184, 135)
        "cadetblue" -> Color.rgb(95, 158, 160)
        "chartreuse" -> Color.rgb(127, 255, 0)
        "chocolate" -> Color.rgb(210, 105, 30)
        "coral" -> Color.rgb(255, 127, 80)
        "cornflowerblue" -> Color.rgb(100, 149, 237)
        "cornsilk" -> Color.rgb(255, 248, 220)
        "crimson" -> Color.rgb(220, 20, 60)
        "cyan" -> Color.rgb(0, 255, 255)
        "darkblue" -> Color.rgb(0, 0, 139)
        "darkcyan" -> Color.rgb(0, 139, 139)
        "darkgoldenrod" -> Color.rgb(184, 134, 11)
        "darkgray" -> Color.rgb(169, 169, 169)
        "darkgreen" -> Color.rgb(0, 100, 0)
        "darkkhaki" -> Color.rgb(189, 183, 107)
        "darkmagenta" -> Color.rgb(139, 0, 139)
        "darkolivegreen" -> Color.rgb(85, 107, 47)
        "darkorange" -> Color.rgb(255, 140, 0)
        "darkorchid" -> Color.rgb(153, 50, 204)
        "darkred" -> Color.rgb(139, 0, 0)
        "darksalmon" -> Color.rgb(233, 150, 122)
        "darkseagreen" -> Color.rgb(143, 188, 143)
        "darkslateblue" -> Color.rgb(72, 61, 139)
        "darkslategray" -> Color.rgb(47, 79, 79)
        "darkturquoise" -> Color.rgb(0, 206, 209)
        "darkviolet" -> Color.rgb(148, 0, 211)
        "deeppink" -> Color.rgb(255, 20, 147)
        "deepskyblue" -> Color.rgb(0, 191, 255)
        "dimgray" -> Color.rgb(105, 105, 105)
        "dimgrey" -> Color.rgb(105, 105, 105)
        "dodgerblue" -> Color.rgb(30, 144, 255)
        "firebrick" -> Color.rgb(178, 34, 34)
        "floralwhite" -> Color.rgb(255, 250, 240)
        "forestgreen" -> Color.rgb(34, 139, 34)
        "fuchsia" -> Color.rgb(255, 0, 255)
        "gainsboro" -> Color.rgb(220, 220, 220)
        "ghostwhite" -> Color.rgb(248, 248, 255)
        "gold" -> Color.rgb(255, 215, 0)
        "goldenrod" -> Color.rgb(218, 165, 32)
        "gray" -> Color.rgb(128, 128, 128)
        "green" -> Color.rgb(0, 128, 0)
        "greenyellow" -> Color.rgb(173, 255, 47)
        "honeydew" -> Color.rgb(240, 255, 240)
        "hotpink" -> Color.rgb(255, 105, 180)
        "indianred" -> Color.rgb(205, 92, 92)
        "indigo" -> Color.rgb(75, 0, 130)
        "ivory" -> Color.rgb(255, 255, 240)
        "khaki" -> Color.rgb(240, 230, 140)
        "lavender" -> Color.rgb(230, 230, 250)
        "lavenderblush" -> Color.rgb(255, 240, 245)
        "lawngreen" -> Color.rgb(124, 252, 0)
        "lemonchiffon" -> Color.rgb(255, 250, 205)
        "lightblue" -> Color.rgb(173, 216, 230)
        "lightcoral" -> Color.rgb(240, 128, 128)
        "lightcyan" -> Color.rgb(224, 255, 255)
        "lightgoldenrodyellow" -> Color.rgb(250, 250, 210)
        "lightgray" -> Color.rgb(211, 211, 211)
        "lightgreen" -> Color.rgb(144, 238, 144)
        "lightpink" -> Color.rgb(255, 182, 193)
        "lightsalmon" -> Color.rgb(255, 160, 122)
        "lightseagreen" -> Color.rgb(32, 178, 170)
        "lightskyblue" -> Color.rgb(135, 206, 250)
        "lightslategray" -> Color.rgb(119, 136, 153)
        "lightsteelblue" -> Color.rgb(176, 196, 222)
        "lightyellow" -> Color.rgb(255, 255, 224)
        "lime" -> Color.rgb(0, 255, 0)
        "limegreen" -> Color.rgb(50, 205, 50)
        "linen" -> Color.rgb(250, 240, 230)
        "magenta" -> Color.rgb(255, 0, 255)
        "maroon" -> Color.rgb(128, 0, 0)
        "mediumaquamarine" -> Color.rgb(102, 205, 170)
        "mediumblue" -> Color.rgb(0, 0, 205)
        "mediumorchid" -> Color.rgb(186, 85, 211)
        "mediumpurple" -> Color.rgb(147, 112, 219)
        "mediumseagreen" -> Color.rgb(60, 179, 113)
        "mediumslateblue" -> Color.rgb(123, 104, 238)
        "mediumspringgreen" -> Color.rgb(0, 250, 154)
        "mediumturquoise" -> Color.rgb(72, 209, 204)
        "mediumvioletred" -> Color.rgb(199, 21, 133)
        "midnightblue" -> Color.rgb(25, 25, 112)
        "mintcream" -> Color.rgb(245, 255, 250)
        "mistyrose" -> Color.rgb(255, 228, 225)
        "moccasin" -> Color.rgb(255, 228, 181)
        "navajowhite" -> Color.rgb(255, 222, 173)
        "navy" -> Color.rgb(0, 0, 128)
        "oldlace" -> Color.rgb(253, 245, 230)
        "olive" -> Color.rgb(128, 128, 0)
        "olivedrab" -> Color.rgb(107, 142, 35)
        "orange" -> Color.rgb(255, 165, 0)
        "orangered" -> Color.rgb(255, 69, 0)
        "orchid" -> Color.rgb(218, 112, 214)
        "palegoldenrod" -> Color.rgb(238, 232, 170)
        "palegreen" -> Color.rgb(152, 251, 152)
        "paleturquoise" -> Color.rgb(175, 238, 238)
        "palevioletred" -> Color.rgb(219, 112, 147)
        "papayawhip" -> Color.rgb(255, 239, 213)
        "peachpuff" -> Color.rgb(255, 218, 185)
        "peru" -> Color.rgb(205, 133, 63)
        "pink" -> Color.rgb(255, 192, 203)
        "plum" -> Color.rgb(221, 160, 221)
        "powderblue" -> Color.rgb(176, 224, 230)
        "purple" -> Color.rgb(128, 0, 128)
        "red" -> Color.rgb(255, 0, 0)
        "rosybrown" -> Color.rgb(188, 143, 143)
        "royalblue" -> Color.rgb(65, 105, 225)
        "saddlebrown" -> Color.rgb(139, 69, 19)
        "salmon" -> Color.rgb(250, 128, 114)
        "sandybrown" -> Color.rgb(244, 164, 96)
        "seagreen" -> Color.rgb(46, 139, 87)
        "seashell" -> Color.rgb(255, 245, 238)
        "sienna" -> Color.rgb(160, 82, 45)
        "silver" -> Color.rgb(192, 192, 192)
        "skyblue" -> Color.rgb(135, 206, 235)
        "slateblue" -> Color.rgb(106, 90, 205)
        "slategray" -> Color.rgb(112, 128, 144)
        "snow" -> Color.rgb(255, 250, 250)
        "springgreen" -> Color.rgb(0, 255, 127)
        "steelblue" -> Color.rgb(70, 130, 180)
        "tan" -> Color.rgb(210, 180, 140)
        "teal" -> Color.rgb(0, 128, 128)
        "thistle" -> Color.rgb(216, 191, 216)
        "tomato" -> Color.rgb(255, 99, 71)
        "turquoise" -> Color.rgb(64, 224, 208)
        "violet" -> Color.rgb(238, 130, 238)
        "wheat" -> Color.rgb(245, 222, 179)
        "white" -> Color.rgb(255, 255, 255)
        "whitesmoke" -> Color.rgb(245, 245, 245)
        "yellow" -> Color.rgb(255, 255, 0)
        "yellowgreen" -> Color.rgb(154, 205, 50)
        else -> null
    }
}