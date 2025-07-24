package com.bargystvelp.world.tree.command

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.common.World
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.PositionComponent
import com.bargystvelp.world.tree.component.START_COMMAND

object GrowCommand {
    fun execute(
        world: World,
        id: Int,
        packedPosition: Int,
        seedCommand: Byte = START_COMMAND,
        color: Color = com.bargystvelp.common.Color.WHITE
    ) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return

        positionComponent[PositionComponent.POS_TO_ID, packedPosition] = id

        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPosition] = seedCommand
        genomeComponent[GenomeComponent.COLOR_AT_POS, packedPosition] = color
    }
}
