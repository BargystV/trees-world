package com.bargystvelp.engine

import com.bargystvelp.Entity
import com.bargystvelp.Factory
import com.bargystvelp.World
import com.bargystvelp.component.Position
import com.bargystvelp.component.Vitality
import com.bargystvelp.component.isWithin
import com.bargystvelp.component.readyToReproduce
import com.bargystvelp.util.Logger

object ReproduceEngine : Engine() {
    private val DIRECTIONS = listOf(
        Position(-1, 0),
        Position(1, 0),
        Position(0, -1),
        Position(0, 1)
    ).shuffled()

    private val entitiesToAdd = mutableListOf<Entity>()

    override fun tick(world: World, delta: Float) {
        world.entities.forEach { entity ->
            val position = entity.get(Position::class.java) ?: return@forEach
            val vitality = entity.get(Vitality::class.java) ?: return@forEach

            if (vitality.isDead || !vitality.readyToReproduce()) return@forEach

            val neighborPosition = findEmptyNeighbor(position, world, entitiesToAdd)
            if (neighborPosition != null) {
                vitality.energy /= 2 // передаём половину потомку

                entitiesToAdd.add(Factory.organism(neighborPosition))
            }
        }

        world.entities.addAll(entitiesToAdd)

        entitiesToAdd.clear()
    }


    private fun findEmptyNeighbor(
        position: Position,
        world: World,
        toAdd: MutableList<Entity>
    ): Position? {
        for (dir in DIRECTIONS) {
            val neighborPosition = Position(position.x + dir.x, position.y + dir.y)
            if (neighborPosition.isWithin(world.size) &&
                !isPositionOccupied(neighborPosition, world.entities, toAdd)
            ) {
                return neighborPosition
            }
        }
        return null
    }

//    fun isPositionOccupied(
//        position: Position,
//        entities: MutableList<Entity>,
//        toAdd: MutableList<Entity>
//    ): Boolean {
//        return entities.any { it.get(Position::class.java) == position } || toAdd.any { it.get(Position::class.java) == position }
//    }

    fun isPositionOccupied(
        position: Position,
        entities: List<Entity>,
        toAdd: List<Entity>
    ): Boolean {
        for (entity in entities) {
            val pos = entity.get(Position::class.java)
            if (pos != null && pos == position) return true
        }

        for (entity in toAdd) {
            val pos = entity.get(Position::class.java)
            if (pos != null && pos == position) return true
        }

        return false
    }

}
