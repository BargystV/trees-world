package com.bargystvelp.world.tree

import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.COMMAND_SIZE
import com.bargystvelp.world.tree.component.DIRECTIONS_SIZE
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.PositionComponent
import com.bargystvelp.world.tree.component.UP
import com.bargystvelp.world.tree.component.DOWN
import com.bargystvelp.world.tree.component.LEFT
import com.bargystvelp.world.tree.component.RIGHT
import com.bargystvelp.world.tree.component.START_COMMAND
import com.bargystvelp.world.tree.engine.GrowEngine
import com.bargystvelp.world.tree.entity.TreeEntityFactory
import com.bargystvelp.common.*
import com.bargystvelp.logger.Logger
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.util.Randomizer
import com.bargystvelp.world.tree.command.CreateCommand
import com.bargystvelp.world.tree.component.AgeComponent
import com.bargystvelp.world.tree.component.EnergyComponent
import com.bargystvelp.world.tree.engine.PhotosynthesisEngine
import com.bargystvelp.world.tree.engine.MortalEngine

const val POSITION_COMPONENT_KEY    = "POSITION"
const val GENOME_COMPONENT_KEY      = "GENOME"
const val ENERGY_COMPONENT_KEY      = "ENERGY"
const val AGE_COMPONENT_KEY         = "AGE"

class TreeWorld(
    windowSize: Size,
    cellSize: Size = Size(width = 10, height = 10),
    biomeSize: Size = windowSize.div(cellSize),
) : World(
    windowSize = windowSize,
    cellSize = cellSize,
    biomeSize = biomeSize,
) {
    private val maxEntities = biomeSize.width * 3

    override val renderer: Renderer = TreeRenderer(windowSize, biomeSize, cellSize)
    override val engines: List<Engine> = listOf(PhotosynthesisEngine, MortalEngine, GrowEngine)
    override val entityFactory: EntityFactory = TreeEntityFactory(maxEntities = maxEntities)
    override val components: Map<String, Component> = mapOf(
        POSITION_COMPONENT_KEY to PositionComponent(maxEntities = maxEntities, width = biomeSize.width, height = biomeSize.height),
        GENOME_COMPONENT_KEY to GenomeComponent(maxEntities = maxEntities, width =  biomeSize.width, height = biomeSize.height),
        ENERGY_COMPONENT_KEY to EnergyComponent(maxEntities = maxEntities),
        AGE_COMPONENT_KEY to AgeComponent(maxEntities = maxEntities),
    )

    init {
        repeat(1) {
            CreateCommand.execute(
                world = this,
                packedPosition = PositionUtils.pack(biomeSize.width.div(2), 0),
                commands = createAdamCommands(),
            )
        }
    }

    override fun tick(delta: Float) {
        engines.forEach { engine ->
            engine.tick(this, delta)
        }

        entityFactory.forEachExist { id ->
            val energy = components[ENERGY_COMPONENT_KEY]?.get(EnergyComponent.ENERGY, id)
            Logger.info("id: $id energy: $energy")
        }
    }

    override fun render(delta: Float) {
        renderer.begin()

        val genomeComponent = components[GENOME_COMPONENT_KEY] ?: return
        val positionComponent = components[POSITION_COMPONENT_KEY] ?: return

        entityFactory.forEachExist { id ->
            val positions = positionComponent[PositionComponent.ID_TO_POS_LIST, id] // IntArray

            for (packed in positions) {
                val x = PositionUtils.unpackX(packed)
                val y = PositionUtils.unpackY(packed)
                val color = genomeComponent[GenomeComponent.COLOR_AT_POS, packed]
                renderer.draw(x, y, color)
            }
        }


        renderer.end()
    }


    private fun createAdamCommands(): Array<ByteArray> {
        val commands: Array<ByteArray> = Array(COMMAND_SIZE) {
            ByteArray(DIRECTIONS_SIZE) { COMMAND_EMPTY }
        }

        // Заполняем первую команду корректно
        commands[START_COMMAND.toInt()][UP] = Randomizer.get.nextInt(COMMAND_SIZE - 1).toByte()
        commands[START_COMMAND.toInt()][DOWN] = COMMAND_EMPTY
        commands[START_COMMAND.toInt()][LEFT] = Randomizer.get.nextInt(COMMAND_SIZE - 1).toByte()
        commands[START_COMMAND.toInt()][RIGHT] = Randomizer.get.nextInt(COMMAND_SIZE - 1).toByte()

        for (cmd in 1 until COMMAND_SIZE) {
            for (dir in 0 until DIRECTIONS_SIZE) {
                val next = Randomizer.get.nextInt(COMMAND_EMPTY.toInt()).toByte()
                commands[cmd][dir] = if (next > COMMAND_SIZE - 1) {
                    COMMAND_EMPTY
                } else {
                    next
                }
            }
        }

        for (cmd in 0 until COMMAND_SIZE) {
            for (dir in 0 until DIRECTIONS_SIZE) {
                Logger.info("cmd: $cmd dir: $dir command: ${commands[cmd][dir]}")
            }
        }

        return commands
    }
}
