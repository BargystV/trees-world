package com.bargystvelp.world.tree.command

import com.bargystvelp.common.Color
import com.bargystvelp.common.World
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.GenomeComponent

object GrowUpCommand {
    fun execute(world: World, packedPosition: Int) {
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return

        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPosition] = COMMAND_EMPTY
        genomeComponent[GenomeComponent.COLOR_AT_POS, packedPosition] = Color.PHOTOSYNTHESIS
    }
}
