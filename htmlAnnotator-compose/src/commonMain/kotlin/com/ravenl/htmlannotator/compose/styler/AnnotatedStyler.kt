package com.ravenl.htmlannotator.compose.styler

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.UrlAnnotation
import com.ravenl.htmlannotator.core.model.TextStyler

interface AnnotatedStyler : TextStyler

interface ISpanStyleStyler : AnnotatedStyler {
    fun getSpanStyler(): SpanStyle
}

interface IParagraphStyleStyler : AnnotatedStyler {
    fun getParagraphStyle(): ParagraphStyle
}

interface IStringAnnotationStyler : AnnotatedStyler {
    fun getTag(): String

    fun getAnnotation(): String
}


interface IUrlAnnotationStyler : AnnotatedStyler {
    @OptIn(ExperimentalTextApi::class)
    fun getUrlAnnotation(): UrlAnnotation
}

interface IBeforeChildrenAnnotatedStyler : AnnotatedStyler {

    fun beforeChildren(builder: AnnotatedString.Builder)
}

interface IAfterChildrenAnnotatedStyler : AnnotatedStyler {

    fun afterChildren(builder: AnnotatedString.Builder)
}

interface IAtChildrenBeforeAnnotatedStyler : AnnotatedStyler {

    fun atChildrenBefore(builder: AnnotatedString.Builder)
}

interface IAtChildrenAfterAnnotatedStyler : AnnotatedStyler {

    fun atChildrenAfter(builder: AnnotatedString.Builder)
}