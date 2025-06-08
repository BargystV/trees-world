package com.bargystvelp.logic.cell.ecosystem.plant

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.constant.Color.PLANT
import com.bargystvelp.constant.Color.PLANT_FRONT
import com.bargystvelp.logic.cell.common.Cell
import com.bargystvelp.logic.cell.common.Entity
import com.bargystvelp.logic.cell.common.Position
import kotlin.random.Random

data class Plant(
    override val positions: MutableList<Position>
) : Entity(positions) {

    companion object {
        const val SPAWN_CHANCE = 0.000001 // 0.0001%

        fun trySpawn(position: Position): Entity? =
            if (Random.Default.nextDouble() < SPAWN_CHANCE)
                Plant(mutableListOf(position))
            else null
    }

    private val grower = Grower(positions)

    override fun render(position: Position, cells: Array<Array<Cell>>) {
        if (rendered || grower.frontier.isEmpty()) return

        grower.render(this, cells)

        rendered = true
    }

    override fun getColor(position: Position): Color {
        return if (grower.frontier.contains(position)) PLANT_FRONT else PLANT
    }
}
