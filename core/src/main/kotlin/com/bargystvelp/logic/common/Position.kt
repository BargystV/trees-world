package com.bargystvelp.logic.common

import com.bargystvelp.constant.Height

data class Position(
    val x: Int,
    val y: Int,
    val height: Height = Height.Companion.defaultHeight()
)

fun Position.isWithin(size: Size): Boolean {
    return x in 0 until size.width && y in 0 until size.height
}
