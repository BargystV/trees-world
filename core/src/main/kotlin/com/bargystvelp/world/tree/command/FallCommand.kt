package com.bargystvelp.world.tree.command

import com.bargystvelp.common.Color
import com.bargystvelp.common.World
import com.bargystvelp.logger.Logger
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.COMMAND_FALL
import com.bargystvelp.world.tree.component.EMPTY_ID
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.PositionComponent

/** Команда перемещения падающего семени на одну клетку вниз. */
object FallCommand {
    /**
     * Переместить семя сущности [id] с позиции [fromPosition] на [toPosition].
     * Очищает старую позицию, обновляет новую.
     */
    fun execute(world: World, id: Int, fromPosition: Int, toPosition: Int) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return

        positionComponent[PositionComponent.POS_TO_ID, fromPosition] = EMPTY_ID
        positionComponent[PositionComponent.POS_TO_ID, toPosition] = id

        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, fromPosition] = COMMAND_EMPTY
        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, toPosition] = COMMAND_FALL
        genomeComponent[GenomeComponent.COLOR_AT_POS, fromPosition] = Color.BLACK
        genomeComponent[GenomeComponent.COLOR_AT_POS, toPosition] = Color.WHITE
    }
}
