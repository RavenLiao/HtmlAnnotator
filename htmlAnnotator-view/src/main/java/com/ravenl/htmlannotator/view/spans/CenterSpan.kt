package com.ravenl.htmlannotator.view.spans

import android.text.Layout
import android.text.style.AlignmentSpan


class CenterSpan : AlignmentSpan {
    override fun getAlignment(): Layout.Alignment = Layout.Alignment.ALIGN_CENTER
}

