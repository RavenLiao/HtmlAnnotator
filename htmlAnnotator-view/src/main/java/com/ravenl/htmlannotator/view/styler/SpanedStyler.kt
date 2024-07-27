package com.ravenl.htmlannotator.view.styler

import android.text.SpannableStringBuilder
import com.ravenl.htmlannotator.core.model.TextStyler

interface SpannedStyler : TextStyler

interface IBeforeChildrenSpannedStyler : SpannedStyler {

    fun beforeChildren(builder: SpannableStringBuilder)
}

interface IAfterChildrenSpannedStyler : SpannedStyler {

    fun afterChildren(builder: SpannableStringBuilder)
}

interface IAtChildrenBeforeSpannedStyler : SpannedStyler {

    fun atChildrenBefore(builder: SpannableStringBuilder)
}

interface IAtChildrenAfterSpannedStyler : SpannedStyler {

    fun atChildrenAfter(builder: SpannableStringBuilder)
}