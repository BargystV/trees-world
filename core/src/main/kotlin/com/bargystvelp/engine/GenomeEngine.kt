package com.bargystvelp.engine

import com.bargystvelp.CMD_MOVE_DOWN
import com.bargystvelp.CMD_MOVE_LEFT
import com.bargystvelp.CMD_MOVE_RIGHT
import com.bargystvelp.CMD_MOVE_UP
import com.bargystvelp.CMD_WAIT
import com.bargystvelp.World
import com.bargystvelp.component.Genome
import com.bargystvelp.component.Vitality
import com.bargystvelp.constant.Color
import com.bargystvelp.util.Logger

object GenomeEngine : Engine() {
    override fun tick(world: World, delta: Float) {
        world.entityManager.forEachAlive { id ->
            val commands = world.genomeManager.getCommands(id)
            commands.forEach { command ->
                val x = world.positionManager.getX(id)
                val y = world.positionManager.getY(id)

                when (command) {
                    CMD_MOVE_UP -> {
                        world.positionManager.set(id, x, y + 1)
                    }
                    CMD_MOVE_DOWN -> {
                        world.positionManager.set(id, x, y - 1)
                    }
                    CMD_MOVE_LEFT -> {
                        world.positionManager.set(id, x - 1, y)
                    }
                    CMD_MOVE_RIGHT -> {
                        world.positionManager.set(id, x + 1, y)
                    }
                    CMD_WAIT -> {}
                }
            }
        }
    }
}

