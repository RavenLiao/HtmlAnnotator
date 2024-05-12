package com.ravenl.htmlannotator.view.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import com.ravenl.htmlannotator.view.util.UnitUtil

abstract class ListItemSpan : LeadingMarginSpan {
    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int, top: Int,
        baseline: Int, bottom: Int, text: CharSequence, start: Int, end: Int,
        first: Boolean, l: Layout
    ) {
        if ((text as Spanned).getSpanStart(this) == start - 1) {
            val style = p.style

            p.style = Paint.Style.FILL
            c.drawText(getDrawText(), (x + dir).toFloat(), baseline.toFloat(), p)

            p.style = style
        }
    }

    abstract fun getDrawText(): String

}

open class OrderedListItemSpan(
    index: Int,
    drawText: (index: Int) -> String = { "$it." },
    private val margin: Int = UnitUtil.dpToPixel(20f)
) : ListItemSpan() {
    override fun getLeadingMargin(first: Boolean): Int = margin

    private val drawString = drawText(index)
    override fun getDrawText(): String = drawString
}

open class UnorderedListItemSpan(
    private val drawText: String = "\u2022",
    private val margin: Int = UnitUtil.dpToPixel(16f)
) : ListItemSpan() {
    override fun getLeadingMargin(first: Boolean): Int = margin
    override fun getDrawText(): String = drawText
}