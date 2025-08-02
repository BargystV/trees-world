package com.bargystvelp.world.tree.command

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.common.World
import com.bargystvelp.world.tree.AGE_COMPONENT_KEY
import com.bargystvelp.world.tree.ENERGY_COMPONENT_KEY
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.component.AgeComponent
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.DEFAULT_ENERGY
import com.bargystvelp.world.tree.component.EMPTY_COMMANDS
import com.bargystvelp.world.tree.component.EMPTY_ID
import com.bargystvelp.world.tree.component.EnergyComponent
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.MIN_AGE
import com.bargystvelp.world.tree.component.PositionComponent

object DieCommand {
    fun execute(
        world: World,
        id: Int
    ) {
        world.entityFactory.destroy(id)

        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return
        val energyComponent = world.components[ENERGY_COMPONENT_KEY] ?: return
        val ageComponent = world.components[AGE_COMPONENT_KEY] ?: return

        val positions = positionComponent[PositionComponent.ID_TO_POS_LIST, id]
        positions.forEach { packedPosition ->
            positionComponent[PositionComponent.POS_TO_ID, packedPosition] = EMPTY_ID

            genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPosition] = COMMAND_EMPTY
            genomeComponent[GenomeComponent.COLOR_AT_POS, packedPosition] = Color.BLACK
        }

        genomeComponent[GenomeComponent.COMMANDS, id] = EMPTY_COMMANDS

        energyComponent[EnergyComponent.ENERGY, id] = DEFAULT_ENERGY

        ageComponent[AgeComponent.AGE, id] = MIN_AGE
    }
}
