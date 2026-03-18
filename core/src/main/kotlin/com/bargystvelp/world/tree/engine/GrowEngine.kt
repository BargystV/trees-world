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

/**
 * Движок роста деревьев.
 * Для каждой активной семенной клетки читает таблицу команд генома
 * и в свободные соседние клетки добавляет новые семена через [GrowCommand].
 * После роста семя превращается в древесину через [SeedToWoodCommand].
 *
 * X-ось: торoidальное обёртывание (мир закольцован по горизонтали).
 * Y-ось: clamped к [0, height-1] (мир ограничен по вертикали).
 */
object GrowEngine : Engine() {
    private val DIRECTIONS = mapOf(
        LEFT to Delta(-1, 0),
        UP to Delta(0, 1),
        RIGHT to Delta(1, 0),
        DOWN to Delta(0, -1),
    )


    /** Выполнить рост для всех живых сущностей. */
    override fun tick(world: World, delta: Float) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return
        val energyComponent = world.components[ENERGY_COMPONENT_KEY] ?: return

        world.entityFactory.forEachExist { id ->
//            if (id != 0) return@forEachExist

            val commands = genomeComponent[GenomeComponent.COMMANDS, id]
            val positions = positionComponent[PositionComponent.ID_TO_POS_LIST, id]

//            if (!EnergyComponent.hasEnoughEnergy(energyComponent[EnergyComponent.ENERGY, id])) return@forEachExist

            for (packed in positions) {
                val commandNumber = genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packed]
                if (commandNumber == COMMAND_WOOD || commandNumber == COMMAND_FALL || commandNumber == COMMAND_EMPTY) return@forEachExist

                val directions = commands[commandNumber.toInt()]
                if (directions === EMPTY_DIRECTIONS) {
                    SeedToWoodCommand.execute(world = world, packedPosition = packed)
                    return@forEachExist
                }

                if (!EnergyComponent.hasEnoughEnergy(directions, id, energyComponent)) return@forEachExist

                val x = PositionUtils.unpackX(packed)
                val y = PositionUtils.unpackY(packed)

                var transformToWood = false

                directions.forEachIndexed { direction, command ->
                    if (command == COMMAND_EMPTY) return@forEachIndexed

                    DIRECTIONS[direction]?.let { (dx, dy) ->
                        val newX = wrap(x + dx, world.biomeSize.width)
                        val newY = clamp(y + dy, world.biomeSize.height)

                        if (PositionComponent.isOccupied(newX, newY, positionComponent)) return@forEachIndexed

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

    /** Обернуть координату по модулю [size] (тор по X). */
    private fun wrap(c: Int, size: Int): Int =
        when {
            c >= size -> c - size
            c < 0 -> c + size
            else -> c
        }

    /** Зажать координату в диапазон [0, size-1] (граница по Y). */
    private fun clamp(c: Int, size: Int): Int =
        when {
            c >= size -> size - 1
            c < 0 -> 0
            else -> c
        }

    /** Смещение координат для одного направления роста. */
    private data class Delta(val dx: Int, val dy: Int)
}
