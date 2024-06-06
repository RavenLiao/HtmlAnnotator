package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.TextUnit

class ParagraphTextStyler(start: Int, end: Int, val paragraphStyle: ParagraphStyle) :
    AnnotatedStyler(start, end) {

    var priority: Int = 0

    override fun addStyle(builder: AnnotatedString.Builder) {
        builder.addStyle(paragraphStyle, start, end)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParagraphTextStyler) return false

        if (start != other.start) return false
        if (end != other.end) return false
        if (paragraphStyle != other.paragraphStyle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = paragraphStyle.hashCode()
        result = 31 * result + start
        result = 31 * result + end
        return result
    }

    override fun toString(): String {
        return "ParagraphTextStyler(start=$start, end=$end, paragraphStyle=$paragraphStyle)"
    }
}

/**
 * Additional line breaks may be introduced
 * @see ParagraphStyle Once a portion of the text is marked with a ParagraphStyle, that portion will be separated from the remaining as if a line feed character was added.
 */
fun List<ParagraphTextStyler>.buildNotOverlapList(totalLength: Int): List<ParagraphTextStyler> {
    if (size <= 1) {
        return this
    }

    val indexes = buildSet {
        add(0)
        this@buildNotOverlapList.forEach { styler ->
            add(styler.start)
            add(styler.end)
        }
        add(totalLength)
    }.sorted()


    var start = indexes.firstOrNull() ?: return this

    val sortedList = sortedBy { it.start }

    return buildList {
        indexes.forEach { end ->
            sortedList.filterIncludeList(start, end)?.mergeWithPriority(start, end)?.also(::add)

            start = end
        }
    }
}

internal fun List<ParagraphTextStyler>.filterIncludeList(
    start: Int,
    end: Int
): List<ParagraphTextStyler>? = filter { styler ->
    styler.start < end && styler.end > start
}.ifEmpty { null }


internal fun List<ParagraphTextStyler>.mergeWithPriority(
    start: Int,
    end: Int
): ParagraphTextStyler? {
    if (isEmpty()) {
        return null
    }
    if (size == 1) {
        return ParagraphTextStyler(start, end, first().paragraphStyle)
    }

    val sorted = sortedByDescending { it.priority }

    val textAlign = sorted.getFirstValidOrNull(TextAlign.Unspecified) { textAlign }
    val textDirection = sorted.getFirstValidOrNull(TextDirection.Unspecified) { textDirection }
    val lineHeight = sorted.getFirstValidOrNull(TextUnit.Unspecified) { lineHeight }
    val textIndent = sorted.getFirstValidOrNull(null) { textIndent }
    val platformStyle = sorted.getFirstValidOrNull(null) { platformStyle }
    val lineHeightStyle = sorted.getFirstValidOrNull(null) { lineHeightStyle }
    val lineBreak = sorted.getFirstValidOrNull(LineBreak.Unspecified) { lineBreak }
    val hyphens = sorted.getFirstValidOrNull(Hyphens.Unspecified) { hyphens }
    val textMotion = sorted.getFirstValidOrNull(null) { textMotion }


    if (textAlign == null && textDirection == null && lineHeight == null && textIndent == null && platformStyle == null && lineHeightStyle == null && lineBreak == null && hyphens == null && textMotion == null) {
        return null
    }

    return ParagraphTextStyler(
        start = start,
        end = end,
        paragraphStyle = ParagraphStyle(
            textAlign = textAlign ?: TextAlign.Unspecified,
            textDirection = textDirection ?: TextDirection.Unspecified,
            lineHeight = lineHeight ?: TextUnit.Unspecified,
            textIndent = textIndent,
            platformStyle = platformStyle,
            lineHeightStyle = lineHeightStyle,
            lineBreak = lineBreak ?: LineBreak.Unspecified,
            hyphens = hyphens ?: Hyphens.Unspecified,
            textMotion = textMotion
        )
    )
}

internal inline fun <T> List<ParagraphTextStyler>.getFirstValidOrNull(
    defaultValue: T,
    map: ParagraphStyle.() -> T
): T? {
    for (e in this) {
        val value = e.paragraphStyle.map()
        if (value != defaultValue) {
            return value
        }
    }
    return null
}