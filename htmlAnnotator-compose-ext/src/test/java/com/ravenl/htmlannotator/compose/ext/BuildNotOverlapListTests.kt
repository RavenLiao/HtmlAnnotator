package com.ravenl.htmlannotator.compose.ext

import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.sp
import com.ravenl.htmlannotator.compose.styler.ParagraphTextStyler
import com.ravenl.htmlannotator.compose.styler.buildNotOverlapList
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import org.junit.Test

class BuildNotOverlapListTests {

    @Test
    fun `empty list should return itself`() {
        val emptyList = emptyList<ParagraphTextStyler>()
        val result = emptyList.buildNotOverlapList(0)
        assertSame(emptyList, result)
    }

    @Test
    fun `single element list should return itself`() {
        val styler = ParagraphTextStyler(0, 10, ParagraphStyle())
        val originalList = listOf(styler)
        val result = originalList.buildNotOverlapList(10)
        assertSame(originalList, result)
    }

    @Test
    fun `overlapping ranges`() {
        val styler1 =
            ParagraphTextStyler(0, 10, ParagraphStyle(textIndent = TextIndent(20.sp))).apply {
                priority = 0
            }
        val styler2 =
            ParagraphTextStyler(5, 15, ParagraphStyle(textIndent = TextIndent(40.sp))).apply {
                priority = 1
            }
        val styler3 =
            ParagraphTextStyler(10, 20, ParagraphStyle(textIndent = TextIndent(60.sp))).apply {
                priority = 2
            }
        val originalList = listOf(styler1, styler2, styler3)

        val expectedResult = listOf(
            ParagraphTextStyler(0, 5, ParagraphStyle(textIndent = TextIndent(20.sp))),
            ParagraphTextStyler(5, 10, ParagraphStyle(textIndent = TextIndent(40.sp))),
            ParagraphTextStyler(10, 15, ParagraphStyle(textIndent = TextIndent(60.sp))),
            ParagraphTextStyler(15, 20, ParagraphStyle(textIndent = TextIndent(60.sp)))
        )

        val result = originalList.buildNotOverlapList(20)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `non-overlapping ranges`() {
        val styler1 = ParagraphTextStyler(0, 10, ParagraphStyle(textIndent = TextIndent(20.sp)))
        val styler2 = ParagraphTextStyler(20, 30, ParagraphStyle(textIndent = TextIndent(40.sp)))
        val originalList = listOf(styler1, styler2)

        val expectedResult = listOf(
            ParagraphTextStyler(0, 10, ParagraphStyle(textIndent = TextIndent(20.sp))),
            ParagraphTextStyler(20, 30, ParagraphStyle(textIndent = TextIndent(40.sp)))
        )

        val result = originalList.buildNotOverlapList(30)
        assertEquals(expectedResult, result)
    }


    @Test
    fun `complex overlapping ranges with different styles`() {
        val styler1 = ParagraphTextStyler(
            0,
            20,
            ParagraphStyle(textIndent = TextIndent(20.sp), textAlign = TextAlign.Center)
        ).apply {
            priority = 0
        }
        val styler2 = ParagraphTextStyler(
            5,
            15,
            ParagraphStyle(textIndent = TextIndent(40.sp), textDirection = TextDirection.Rtl)
        ).apply {
            priority = 1
        }
        val styler3 = ParagraphTextStyler(
            10,
            25,
            ParagraphStyle(lineHeight = 1.5.sp, hyphens = Hyphens.Auto)
        ).apply {
            priority = 2
        }
        val styler4 = ParagraphTextStyler(
            15,
            30,
            ParagraphStyle(lineBreak = LineBreak.Heading, textMotion = TextMotion.Animated)
        ).apply {
            priority = 3
        }
        val originalList = listOf(styler1, styler2, styler3, styler4)

        val expectedResult = listOf(
            ParagraphTextStyler(
                0,
                5,
                ParagraphStyle(textIndent = TextIndent(20.sp), textAlign = TextAlign.Center)
            ),
            ParagraphTextStyler(
                5,
                10,
                ParagraphStyle(
                    textIndent = TextIndent(40.sp),
                    textDirection = TextDirection.Rtl,
                    textAlign = TextAlign.Center
                )
            ),
            ParagraphTextStyler(
                10,
                15,
                ParagraphStyle(
                    textIndent = TextIndent(40.sp),
                    lineHeight = 1.5.sp,
                    hyphens = Hyphens.Auto,
                    textDirection = TextDirection.Rtl,
                    textAlign = TextAlign.Center
                )
            ),
            ParagraphTextStyler(
                15,
                20,
                ParagraphStyle(
                    textIndent = TextIndent(20.sp),
                    textAlign = TextAlign.Center,
                    lineHeight = 1.5.sp,
                    hyphens = Hyphens.Auto,
                    lineBreak = LineBreak.Heading,
                    textMotion = TextMotion.Animated
                )
            ),
            ParagraphTextStyler(
                20,
                25,
                ParagraphStyle(
                    lineHeight = 1.5.sp,
                    hyphens = Hyphens.Auto,
                    lineBreak = LineBreak.Heading,
                    textMotion = TextMotion.Animated
                )
            ),
            ParagraphTextStyler(
                25,
                30,
                ParagraphStyle(
                    lineBreak = LineBreak.Heading,
                    textMotion = TextMotion.Animated
                )
            )
        )

        val result = originalList.buildNotOverlapList(30)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `adjacent ranges with different styles`() {
        val styler1 = ParagraphTextStyler(
            0,
            10,
            ParagraphStyle(textIndent = TextIndent(20.sp), textAlign = TextAlign.Center)
        )
        val styler2 = ParagraphTextStyler(
            10,
            20,
            ParagraphStyle(textIndent = TextIndent(40.sp), textDirection = TextDirection.Rtl)
        )
        val originalList = listOf(styler1, styler2)

        val expectedResult = listOf(
            ParagraphTextStyler(
                0,
                10,
                ParagraphStyle(textIndent = TextIndent(20.sp), textAlign = TextAlign.Center)
            ),
            ParagraphTextStyler(
                10,
                20,
                ParagraphStyle(textIndent = TextIndent(40.sp), textDirection = TextDirection.Rtl)
            )
        )

        val result = originalList.buildNotOverlapList(20)
        assertEquals(expectedResult, result)
    }
}