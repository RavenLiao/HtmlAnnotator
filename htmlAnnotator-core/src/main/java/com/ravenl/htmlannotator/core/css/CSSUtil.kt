package com.ravenl.htmlannotator.core.css

/**
 * @return null if empty
 */
fun parseCssDeclarations(declarations: String): List<CSSDeclaration>? = buildList {
    val srcList = declarations.split(';')
    srcList.forEach { src ->
        val declaration = src.split(':')
        if (declaration.size == 2) {
            add(CSSDeclaration(declaration[0].trim(), declaration[1].trim()))
        }
    }
}.ifEmpty { null }