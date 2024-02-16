package com.ravenl.htmlannotator.core.util

/**
 * @param isStripExtraWhiteSpace if it true, making sure that there are never more than 2 consecutive newlines in the text
 */
fun StringBuilder.appendNewLine(isStripExtraWhiteSpace: Boolean): Boolean {
    if (isStripExtraWhiteSpace) {
        if (length > 2 && get(length - 1) == '\n' && get(length - 2) == '\n') {
            return false
        }
    }
    append('\n')
    return true
}