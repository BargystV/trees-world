package com.bargystvelp.logic.common

import com.bargystvelp.constant.Height

data class Position(
    val x: Int,
    val y: Int,
    val height: Height = Height.Companion.defaultHeight()
)
