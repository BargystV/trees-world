package com.bargystvelp.world.tree.engine

import com.bargystvelp.common.Component
import com.bargystvelp.common.Engine
import com.bargystvelp.common.World
import com.bargystvelp.logger.Logger
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.command.CreateCommand
import com.bargystvelp.world.tree.command.GrowCommand
import com.bargystvelp.world.tree.command.GrowUpCommand
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

        world.entityFactory.forEachExist { id ->
            val commands = genomeComponent[GenomeComponent.COMMANDS, id]
            val positions = positionComponent[PositionComponent.ID_TO_POS_LIST, id]

            for (packed in positions) {
                val commandNumber = genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packed]
                if (commandNumber == COMMAND_EMPTY) return@forEachExist

                val directions = commands[commandNumber.toInt()]
                if (directions === EMPTY_DIRECTIONS) return@forEachExist

                val x = PositionUtils.unpackX(packed)
                val y = PositionUtils.unpackY(packed)

                var growUp = false

                directions.forEachIndexed { direction, command ->
                    if (command == COMMAND_EMPTY) return@forEachIndexed

                    DIRECTIONS[direction]?.let { (dx, dy) ->
                        val newX = wrap(x + dx, world.biomeSize.width)
                        val newY = clamp(y + dy, world.biomeSize.height)

                        if (isOccupied(newX, newY, positionComponent)) return@forEachIndexed

                        GrowCommand.execute(
                            world = world,
                            id = id,
                            packedPosition = PositionUtils.pack(newX, newY),
                            seedCommand = command,
                        )

                        growUp = true
                    }
                }

                // TODO Если семечко не проросло потому что команды пустые то оно должно превратиться в дерево все равно
                // TODO Однако если семечко не проросло потому что не хватило энергии - оно остается семечком
                if (growUp) {
                    GrowUpCommand.execute(world = world, packedPosition = packed)
                }
            }
        }
    }


    private fun isOccupied(x: Int, y: Int, positionComponent: Component): Boolean {
        return positionComponent[PositionComponent.POS_TO_ID, PositionUtils.pack(x, y)] != EMPTY_ID
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
