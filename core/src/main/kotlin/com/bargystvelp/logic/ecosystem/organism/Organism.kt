package com.bargystvelp.logic.ecosystem.organism

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.Logger
import com.bargystvelp.logic.common.Metabolism
import com.bargystvelp.logic.common.Position
import com.bargystvelp.logic.common.Size
import com.bargystvelp.logic.common.isWithin
import com.bargystvelp.logic.common.organismmanager.OrganismManager
import com.bargystvelp.logic.ecosystem.metabolism.Photosynthesis

data class Organism(
    var position: Position,
    var energy: Int = 1000,
    var age: Int = 0,
) {
    companion object {
        private const val LIFE_TICK = 100
        private const val REPRODUCTION_THRESHOLD = 2000
        private val DIRECTIONS = listOf(
            Position(-1, 0),
            Position(1, 0),
            Position(0, -1),
            Position(0, 1)
        ).shuffled()
    }

    val genome: Genome = Genome()
    val metabolism: Metabolism = Photosynthesis()
    val color: Color get() =  if (isDead) com.bargystvelp.constant.Color.ORGANIC else metabolism.color
    val isDead: Boolean get() = energy <= 0 || age >= genome.lifetime





    fun tickLife() {
        energy += LIFE_TICK
        age++
    }

    fun tryReproduce(organismManager: OrganismManager) {
        if (isDead || !readyToReproduce()) return

        val start = System.nanoTime()

        val neighbor = findEmptyNeighbor(position, organismManager)

        val end = System.nanoTime()
        val ms = (end - start) / 1_000_000.0

//        Logger.info("%.3f ms (Organisms: ${organismManager.organisms.size})".format(ms))
        if (neighbor != null) {
            energy /= 2 // передаём половину потомку

            organismManager.organismBuffer.add(
                Organism(
                    position = neighbor,
                )
            )
        }
    }

    private fun readyToReproduce(): Boolean = energy > REPRODUCTION_THRESHOLD && age > 10


    private fun findEmptyNeighbor(
        position: Position,
        organismManager: OrganismManager
    ): Position? {
        for (dir in DIRECTIONS) {
            val neighbor = Position(position.x + dir.x, position.y + dir.y)
            if (neighbor.isWithin(organismManager.biomeSize) &&
                !isPositionOccupied(neighbor, organismManager.organisms, organismManager.organismBuffer.toAdd)
            ) {
                return neighbor
            }
        }
        return null
    }


    fun isPositionOccupied(
        position: Position,
        organisms: Map<Position, Organism>,
        toAdd: Map<Position, Organism>
    ): Boolean {
        return position in organisms || position in toAdd
    }
}
