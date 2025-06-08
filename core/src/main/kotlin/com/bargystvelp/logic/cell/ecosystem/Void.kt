package com.bargystvelp.logic.cell.ecosystem

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.constant.Color.VOID
import com.bargystvelp.logic.cell.common.Cell
import com.bargystvelp.logic.cell.common.Entity
import com.bargystvelp.logic.cell.common.Position
import com.bargystvelp.logic.cell.ecosystem.plant.Plant

class Void(
    override val positions: MutableList<Position>,// Всегда 1 позиция
) : Entity(
    positions = positions,
) {
    override fun render(position: Position, cells: Array<Array<Cell>>) {
        if (rendered) return

        this.rendered = true

        val entity = Plant.trySpawn(position)
        if (entity == null) return

        cells[position.x][position.y].entity = entity
    }

    override fun getColor(position: Position): Color {
        return VOID
    }
}
