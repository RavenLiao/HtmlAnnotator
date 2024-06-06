package com.ravenl.htmlannotator.compose.ext

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals


private const val TAG = "test"

class SplitByAnnotationTest {

    @Test
    fun `split empty string`() {
        val emptyString = AnnotatedString("")
        val result = emptyString.splitByAnnotation(TAG)
        assertEquals(listOf(emptyString), result)
    }

    @Test
    fun `split string without annotation`() {
        val string = AnnotatedString("Hello world")
        val result = string.splitByAnnotation(TAG)
        assertEquals(listOf(string), result)
    }

    @Test
    fun `split string with single annotation`() {
        val string = buildAnnotatedString {
            append("Hello ")
            pushStringAnnotation(TAG, "World")
            append("World")
            pop()
        }
        val result = string.splitByAnnotation(TAG)
        assertEquals(
            listOf(
                AnnotatedString("Hello "),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "World")
                    append("World")
                    pop()
                }
            ),
            result
        )
    }

    @Test
    fun `split string with multiple annotations`() {
        val string = buildAnnotatedString {
            append("Hello ")
            pushStringAnnotation(TAG, "World")
            append("World")
            pop()
            append(" and ")
            pushStringAnnotation(TAG, "Kotlin")
            append("Kotlin")
            pop()
        }
        val result = string.splitByAnnotation(TAG)
        assertEquals(
            listOf(
                AnnotatedString("Hello "),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "World")
                    append("World")
                    pop()
                },
                AnnotatedString(" and "),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "Kotlin")
                    append("Kotlin")
                    pop()
                }
            ),
            result
        )
    }


    @Test
    fun `split string with fully overlapping annotations1`() {
        val string = buildAnnotatedString {
            append("Hello ")
            pushStringAnnotation(TAG, "World[nested]")
            append("World")
            pushStringAnnotation(TAG, "nested")
            append(" nested")
            pop()
            pop()
        }
        val result = string.splitByAnnotation(TAG)
        assertEquals(
            listOf(
                AnnotatedString("Hello "),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "World[nested]")
                    append("World")
                    pushStringAnnotation(TAG, "nested")
                    append(" nested")
                    pop()
                    pop()
                }
            ),
            result
        )
    }


    @Test
    fun `split string with fully overlapping annotations2`() {
        val string = buildAnnotatedString {
            append("Hello ")
            pushStringAnnotation(TAG, "World[nested]")
            append("World")
            pushStringAnnotation(TAG, "nested")
            pop()
            pop()
        }
        val result = string.splitByAnnotation(TAG)
        assertEquals(
            listOf(
                AnnotatedString("Hello "),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "World[nested]")
                    append("World")
                    pop()
                },
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "nested")
                    pop()
                }
            ),
            result
        )
    }

    @Test
    fun `split string with multiple overlapping annotations`() {
        val string = buildAnnotatedString {
            append("Hello ")
            pushStringAnnotation(TAG, "World[nested][overlapped]")
            append("World")
            pushStringAnnotation(TAG, "nested")
            append("nested")
            pushStringAnnotation(TAG, "overlapped")
            pop()
            pop()
            pop()
        }
        val result = string.splitByAnnotation(TAG)
        assertEquals(
            listOf(
                AnnotatedString("Hello "),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "World[nested][overlapped]")
                    append("World")
                    pushStringAnnotation(TAG, "nested")
                    append("nested")
                    pop()
                },
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "overlapped")
                    pop()
                }
            ),
            result
        )
    }

    @Test
    fun `split string with intersecting annotations`() {
        val string = buildAnnotatedString {
            append("0123456789")
            addStringAnnotation(TAG, "234", 2, 5)
            addStringAnnotation(TAG, "2345", 2, 6)
            addStringAnnotation(TAG, "4567", 5, 8)
            addStringAnnotation(TAG, "", 10, 10)
        }
        val result = string.splitByAnnotation(TAG)
        assertEquals(
            listOf(
                AnnotatedString("01"),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "234")
                    pushStringAnnotation(TAG, "2345")
                    append("234")
                    pop()
                    pop()
                },
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "2345")
                    pushStringAnnotation(TAG, "4567")
                    append("5")
                    pop()
                    pop()
                },
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "4567")
                    append("67")
                    pop()
                },
                AnnotatedString("89"),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "")
                    pop()
                },
            ),
            result
        )
    }

    @Test
    fun `split string with intersecting annotations2`() {
        val string = buildAnnotatedString {
            append("0123456789")
            addStringAnnotation(TAG, "4567", 5, 8)
            addStringAnnotation(TAG, "2345", 2, 6)
            addStringAnnotation(TAG, "", 10, 10)
            addStringAnnotation(TAG, "234", 2, 5)
        }
        val result = string.sortAnnotations().splitByAnnotation(TAG)
        assertEquals(
            listOf(
                AnnotatedString("01"),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "234")
                    pushStringAnnotation(TAG, "2345")
                    append("234")
                    pop()
                    pop()
                },
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "2345")
                    pushStringAnnotation(TAG, "4567")
                    append("5")
                    pop()
                    pop()
                },
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "4567")
                    append("67")
                    pop()
                },
                AnnotatedString("89"),
                buildAnnotatedString {
                    pushStringAnnotation(TAG, "")
                    pop()
                },
            ),
            result
        )
    }


    @Test
    fun `split list string with overlapping annotations`() {
        val annotatedString = buildAnnotatedString {
            pushStringAnnotation("a", "Hello [World]")
            append("Hello ")
            pushStringAnnotation("b", "[World]")
            append("[World]")
            pop()
            pop()
        }
        val result = annotatedString.splitByAnnotation(listOf("a", "b"))
        assertEquals(
            listOf(
                buildAnnotatedString {
                    pushStringAnnotation("a", "Hello [World]")
                    append("Hello ")
                    pop()
                },
                buildAnnotatedString {
                    pushStringAnnotation("a", "Hello [World]")
                    pushStringAnnotation("b", "[World]")
                    append("[World]")
                    pop()
                    pop()
                }
            ),
            result
        )
    }


    @Test
    fun `split list string with overlapping annotations2`() {
        val annotatedString = buildAnnotatedString {
            pushStringAnnotation("a", "Hello [World]")
            append("Hello ")
            pop()
            pushStringAnnotation("b", "[World]")
            append("[World]")
            pop()
        }
        val result = annotatedString.splitByAnnotation(listOf("a", "b"))
        assertEquals(
            listOf(
                buildAnnotatedString {
                    pushStringAnnotation("a", "Hello [World]")
                    append("Hello ")
                    pop()
                },
                buildAnnotatedString {
                    pushStringAnnotation("b", "[World]")
                    append("[World]")
                    pop()
                }
            ),
            result
        )
    }
}