package com.bargystvelp.world.tree.engine

import com.bargystvelp.common.Component
import com.bargystvelp.common.Engine
import com.bargystvelp.common.World
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.command.AgeUpCommand
import com.bargystvelp.world.tree.command.DieCommand
import com.bargystvelp.world.tree.command.EnergySpendCommand
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.COMMAND_WOOD
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.MAX_AGE
import com.bargystvelp.world.tree.component.PositionComponent
import kotlin.collections.forEach

object MortalEngine : Engine() {

    private const val WOOD_COST = 4

    override fun tick(world: World, delta: Float) {
        val positionComponent   = world.components[POSITION_COMPONENT_KEY]  ?: return
        val genomeComponent     = world.components[GENOME_COMPONENT_KEY]    ?: return

        world.entityFactory.forEachExist { id ->
//            if (id != 0) return@forEachExist

            if (!ageProcessing(world, id)) {
                DieCommand.execute(world, id)
                return@forEachExist
            }

            if (!energyProcessing(world, positionComponent, genomeComponent, id)) {
                DieCommand.execute(world, id)
                return@forEachExist
            }
        }
    }

    private fun energyProcessing(world: World, positionComponent: Component, genomeComponent: Component, id: Int): Boolean {
        var energyCost = 0

        val cells = positionComponent[PositionComponent.ID_TO_POS_LIST, id]
        cells.forEach { packedPos ->
            // семечко не тратит энергию
            if (genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPos] == COMMAND_WOOD) {
                energyCost += WOOD_COST
            }
        }

        val energy: Int? = if (energyCost != 0) {
            EnergySpendCommand.execute(world, id, energyCost)
        } else {
            null
        }

        return if (energy == null) true else energy > 0
    }

    private fun ageProcessing(world: World, id: Int): Boolean {
        val age: Int? = AgeUpCommand.execute(world, id)
        return age != null && age < MAX_AGE
    }
}

