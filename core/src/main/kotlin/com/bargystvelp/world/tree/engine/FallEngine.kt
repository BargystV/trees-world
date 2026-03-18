package com.bargystvelp.world.tree.engine

import com.bargystvelp.common.Engine
import com.bargystvelp.common.World
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.command.DestroySeedCommand
import com.bargystvelp.world.tree.command.FallCommand
import com.bargystvelp.world.tree.command.SeedOnGroundCommand
import com.bargystvelp.world.tree.component.COMMAND_FALL
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.PositionComponent

/**
 * Каждый тик «семена-падальщики» (COMMAND_FALL) смещаются
 * на одну клетку вниз, пока не:
 *   • достигнут земли (y == 0) → SeedOnGroundCommand
 *   • столкнутся с существом / другим семенем → SeedDestroyCommand
 */
object FallEngine : Engine() {
    /**
     * Обработать падение всех семян с маркером [COMMAND_FALL]:
     *  - столкновение снизу → [DestroySeedCommand]
     *  - Y == 1 (следующий шаг — земля) → [SeedOnGroundCommand]
     *  - иначе → [FallCommand] (сдвинуть вниз)
     */
    override fun tick(world: World, delta: Float) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return

        world.entityFactory.forEachExist { id ->
            val positions = positionComponent[PositionComponent.ID_TO_POS_LIST, id]

            for (packed in positions) {
                val seed = genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packed]
                if (seed != COMMAND_FALL) return@forEachExist
//
                val x = PositionUtils.unpackX(packed)
                val y = PositionUtils.unpackY(packed)
                val belowY = y - 1

                if (PositionComponent.isOccupied(x, belowY, positionComponent)) {
                    DestroySeedCommand.execute(world, id, packed)
                    return@forEachExist
                }

                // на земле
                if (belowY == 0) {
                    SeedOnGroundCommand.execute(world, packed)
                    return@forEachExist
                }

                FallCommand.execute(world, id, packed, PositionUtils.pack(x, belowY))
            }
        }
    }
}

