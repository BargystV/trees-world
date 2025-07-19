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
import java.util.Random

const val POSITION_COMPONENT_KEY = "POSITION"
const val GENOME_COMPONENT_KEY = "GENOME"

class ThreeWorld(
    windowSize: Size,
    cellSize: Size = Size(width = 10, height = 10),
    biomeSize: Size = windowSize.div(cellSize),
) : World(
    windowSize = windowSize,
    cellSize = cellSize,
    biomeSize = biomeSize,
) {
    private val capacity = biomeSize.height * biomeSize.width

    override val renderer: Renderer = TreeRenderer(windowSize, biomeSize, cellSize)
    override val engines: List<Engine> = listOf(GrowEngine)
    override val entityFactory: EntityFactory = TreeEntityFactory(capacity)
    override val components: Map<String, Component> = mapOf(
        POSITION_COMPONENT_KEY to PositionComponent(width = biomeSize.width, height = biomeSize.height),
        GENOME_COMPONENT_KEY to GenomeComponent(capacity),
    )

    init {
        repeat(1) {
            val adam = entityFactory.create()

            val positionComponent = components[POSITION_COMPONENT_KEY] ?: return@repeat
            val genomeComponent = components[GENOME_COMPONENT_KEY] ?: return@repeat

            positionComponent[PositionComponent.ID_TO_POS, adam] = PositionComponent.pack(biomeSize.width.div(2), 0)

            genomeComponent[GenomeComponent.COMMAND_NUMBER, adam] = START_COMMAND
            genomeComponent[GenomeComponent.COMMANDS, adam] = createAdamCommands()
            genomeComponent[GenomeComponent.COLOR, adam] = Color.WHITE
        }
    }

    override fun tick(delta: Float) {
        engines.forEach { engine ->
            engine.tick(this, delta)
        }
    }

    override fun render(delta: Float) {
        renderer.begin()

        val genomeComponent = components[GENOME_COMPONENT_KEY] ?: return
        val positionComponent = components[POSITION_COMPONENT_KEY] ?: return

        entityFactory.forEachExist { id ->
            val color = genomeComponent[GenomeComponent.COLOR, id]
            val packed = positionComponent[PositionComponent.ID_TO_POS, id]

            val x = PositionComponent.unpackX(packed)
            val y = PositionComponent.unpackY(packed)


            renderer.draw(x, y, color)
        }

        renderer.end()
    }


    private fun createAdamCommands(seed: Long = System.currentTimeMillis()): Array<ByteArray> {
        val random = Random(seed)

        val commands: Array<ByteArray> = Array(COMMAND_SIZE) {
            ByteArray(DIRECTIONS_SIZE) { COMMAND_EMPTY }
        }

        // Заполняем первую команду корректно
        commands[START_COMMAND.toInt()][UP] = random.nextInt(COMMAND_SIZE - 1).toByte()
        commands[START_COMMAND.toInt()][DOWN] = COMMAND_EMPTY
        commands[START_COMMAND.toInt()][LEFT] = random.nextInt(COMMAND_SIZE - 1).toByte()
        commands[START_COMMAND.toInt()][RIGHT] = random.nextInt(COMMAND_SIZE - 1).toByte()

        for (cmd in 1 until COMMAND_SIZE) {
            for (dir in 0 until DIRECTIONS_SIZE) {
                // 50% шанс на пустую команду
                commands[cmd][dir] = if (random.nextBoolean()) {
                    random.nextInt(COMMAND_SIZE - 1).toByte()
                } else {
                    COMMAND_EMPTY
                }

                Logger.info("cmd: $cmd dir: $dir command: ${commands[cmd][dir]}")
            }
        }

        return commands
    }
}
