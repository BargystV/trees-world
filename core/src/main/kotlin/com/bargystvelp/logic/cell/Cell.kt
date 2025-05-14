package com.bargystvelp.logic.cell

import com.badlogic.gdx.graphics.Color

abstract class Cell(
    open val x: Int,
    open val y: Int,
    open val height: Height
) {
    abstract fun getColor(): Color
}
