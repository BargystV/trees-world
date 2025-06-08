package com.bargystvelp.logic.biome.common

import com.bargystvelp.logic.cell.common.Cell
import com.bargystvelp.logic.cell.common.Size

abstract class Biome(
    open val size: Size
) {
    val cells: Array<Array<Cell>> by lazy { create() }

    fun render(): Array<Array<Cell>> {
        prepare()

        for (x in 0 until size.width) {
            for (y in 0 until size.height) {
                cells[x][y].entity.render(position = cells[x][y].position, cells)
            }
        }

        return cells
    }

    private fun prepare() {
        for (x in 0 until size.width) {
            for (y in 0 until size.height) {
                cells[x][y].entity.rendered = false
            }
        }
    }


    protected abstract fun create(): Array<Array<Cell>>
}

