package com.bargystvelp.component

import com.bargystvelp.Size

data class Position(var x: Int = 0, var y: Int = 0): Component

fun Position.isWithin(size: Size): Boolean {
    return x in 0 until size.width && y in 0 until size.height
}
