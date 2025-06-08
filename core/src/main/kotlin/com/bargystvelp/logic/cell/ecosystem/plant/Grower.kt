package com.bargystvelp.logic.cell.ecosystem.plant

import com.bargystvelp.logic.cell.common.Cell
import com.bargystvelp.logic.cell.common.Position

class Grower(
    val positions: MutableList<Position>
) {
    companion object {
        val DIRECTIONS = listOf(
            Position(-1, 0), // влево
            Position(1, 0),  // вправо
            Position(0, -1), // вверх
            Position(0, 1)   // вниз
        )
    }

    val frontier = positions.toMutableSet()
    private val addToFrontier = mutableListOf<Position>()
    private val removeFromFrontier = mutableListOf<Position>()

    fun render(currentPlant: Plant, cells: Array<Array<Cell>>) {
        for (frontierPosition in frontier.shuffled()) {
            var expanded = false

            for (direction in DIRECTIONS.shuffled()) {
                val neighborX = frontierPosition.x + direction.x
                val neighborY = frontierPosition.y + direction.y

                if (!cells.isWithinBounds(neighborX, neighborY)) continue

                val neighborCell = cells[neighborX][neighborY]
                val neighborPosition = neighborCell.position

                if (neighborCell.entity is Plant) continue

                // Рост
                positions.add(neighborPosition)
                addToFrontier.add(neighborPosition)
                cells[neighborX][neighborY].entity = currentPlant

                expanded = true
                break
            }

            if (!expanded && isSurroundedOnlyBySelf(currentPlant, frontierPosition, cells)) {
                removeFromFrontier.add(frontierPosition)
            }
        }

        updateFrontier()
    }


    private fun isSurroundedOnlyBySelf(currentPlant: Plant, position: Position, cells: Array<Array<Cell>>): Boolean {
        return DIRECTIONS.none { direction ->
            val adjacentX = position.x + direction.x
            val adjacentY = position.y + direction.y

            if (!cells.isWithinBounds(adjacentX, adjacentY)) return@none false

            val adjacentEntity = cells[adjacentX][adjacentY].entity
            adjacentEntity !is Plant || adjacentEntity !== currentPlant
        }
    }

    private fun updateFrontier() {
        frontier.removeAll(removeFromFrontier)
        frontier.addAll(addToFrontier)
        addToFrontier.clear()
        removeFromFrontier.clear()
    }

    private fun Array<Array<Cell>>.isWithinBounds(x: Int, y: Int): Boolean {
        return x in indices && y in this[0].indices
    }
}
