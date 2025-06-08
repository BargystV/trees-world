package com.bargystvelp.logic.cell.common

import com.badlogic.gdx.graphics.Color

abstract class Entity(
    open val positions: MutableList<Position>
) {
    var rendered = true

    abstract fun render(position: Position, cells: Array<Array<Cell>>)
    abstract fun getColor(position: Position): Color
}
