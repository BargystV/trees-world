package com.bargystvelp.world.tree.command

import com.bargystvelp.common.Color
import com.bargystvelp.common.World
import com.bargystvelp.logger.Logger
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.COMMAND_WOOD
import com.bargystvelp.world.tree.component.GenomeComponent

object SeedToWoodCommand {
    fun execute(world: World, packedPosition: Int) {
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return

        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPosition] = COMMAND_WOOD
        genomeComponent[GenomeComponent.COLOR_AT_POS, packedPosition] = Color.PHOTOSYNTHESIS

        val x = PositionUtils.unpackX(packedPosition)
        val y = PositionUtils.unpackY(packedPosition)

//        Logger.info("x: $x y: $y")
    }
}
