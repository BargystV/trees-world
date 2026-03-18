package com.bargystvelp.world.tree.command

import com.bargystvelp.common.World
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.component.COMMAND_WOOD
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.PositionComponent

/**
 * Команда превращения семени в древесину: меняет команду на COMMAND_WOOD
 * и устанавливает цвет из генетического BASE_COLOR сущности.
 */
object SeedToWoodCommand {
    /**
     * Превратить семенную клетку в позиции [packedPosition] в древесную.
     * Цвет берётся из BASE_COLOR сущности, которой принадлежит клетка.
     */
    fun execute(world: World, packedPosition: Int) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent   = world.components[GENOME_COMPONENT_KEY]   ?: return

        val id = positionComponent[PositionComponent.POS_TO_ID, packedPosition]
        val color = genomeComponent[GenomeComponent.BASE_COLOR, id]

        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPosition] = COMMAND_WOOD
        genomeComponent[GenomeComponent.COLOR_AT_POS, packedPosition]        = color
    }
}
