package com.bargystvelp.world.tree.engine

import com.bargystvelp.common.Engine
import com.bargystvelp.common.World
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.ENERGY_COMPONENT_KEY
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.command.GrowCommand
import com.bargystvelp.world.tree.command.SeedToWoodCommand
import com.bargystvelp.world.tree.component.*

object GrowEngine : Engine() {
    private val DIRECTIONS = mapOf(
        LEFT to Delta(-1, 0),
        UP to Delta(0, 1),
        RIGHT to Delta(1, 0),
        DOWN to Delta(0, -1),
    )


    override fun tick(world: World, delta: Float) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return
        val energyComponent = world.components[ENERGY_COMPONENT_KEY] ?: return

        world.entityFactory.forEachExist { id ->
//            if (id != 0) return@forEachExist

            val commands = genomeComponent[GenomeComponent.COMMANDS, id]
            val positions = positionComponent[PositionComponent.ID_TO_POS_LIST, id]

            if (!EnergyComponent.hasEnoughEnergy(energyComponent[EnergyComponent.ENERGY, id])) return@forEachExist

            for (packed in positions) {
                val commandNumber = genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packed]
                if (commandNumber == COMMAND_WOOD || commandNumber == COMMAND_FALL) return@forEachExist

                val directions = commands[commandNumber.toInt()]
                if (directions === EMPTY_DIRECTIONS) {
                    SeedToWoodCommand.execute(world = world, packedPosition = packed)
                    return@forEachExist
                }

                val x = PositionUtils.unpackX(packed)
                val y = PositionUtils.unpackY(packed)

                var transformToWood = false

                directions.forEachIndexed { direction, command ->
                    if (command == COMMAND_EMPTY) return@forEachIndexed

                    DIRECTIONS[direction]?.let { (dx, dy) ->
                        val newX = wrap(x + dx, world.biomeSize.width)
                        val newY = clamp(y + dy, world.biomeSize.height)

                        if (PositionComponent.isOccupied(newX, newY, positionComponent)) return@forEachIndexed
                        if (!EnergyComponent.hasEnoughEnergy(energyComponent[EnergyComponent.ENERGY, id])) return@forEachExist

                        GrowCommand.execute(
                            world = world,
                            id = id,
                            packedPosition = PositionUtils.pack(newX, newY),
                            seedCommand = command,
                        )

                        transformToWood = true
                    }
                }

                if (transformToWood) {
                    SeedToWoodCommand.execute(world = world, packedPosition = packed)
                }
            }
        }
    }

    /** Обёртка 0‥size-1 (тор). */
    private fun wrap(c: Int, size: Int): Int =
        when {
            c >= size -> c - size
            c < 0 -> c + size
            else -> c
        }

    /** Ограничение 0‥size-1 (зажим). */
    private fun clamp(c: Int, size: Int): Int =
        when {
            c >= size -> size - 1
            c < 0 -> 0
            else -> c
        }

    private data class Delta(val dx: Int, val dy: Int)
}
