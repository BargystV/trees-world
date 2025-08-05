package com.bargystvelp.world.tree.command

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.common.Component
import com.bargystvelp.common.World
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.AGE_COMPONENT_KEY
import com.bargystvelp.world.tree.ENERGY_COMPONENT_KEY
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.component.AgeComponent
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.COMMAND_FALL
import com.bargystvelp.world.tree.component.COMMAND_WOOD
import com.bargystvelp.world.tree.component.DEFAULT_ENERGY
import com.bargystvelp.world.tree.component.EMPTY_COMMANDS
import com.bargystvelp.world.tree.component.EMPTY_ID
import com.bargystvelp.world.tree.component.EnergyComponent
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.MIN_AGE
import com.bargystvelp.world.tree.component.PositionComponent
import com.bargystvelp.world.tree.component.START_COMMAND

object DieCommand {
    fun execute(world: World, id: Int) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY]    ?: return
        val genomeComponent   = world.components[GENOME_COMPONENT_KEY]      ?: return
        val energyComponent   = world.components[ENERGY_COMPONENT_KEY]      ?: return
        val ageComponent      = world.components[AGE_COMPONENT_KEY]         ?: return

        // Снимок всех клеток дерева до модификаций
        val cells = positionComponent[PositionComponent.ID_TO_POS_LIST, id]
        val parentGenome = genomeComponent[GenomeComponent.COMMANDS, id]

        cells.forEach { packedPos ->
            if (genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPos] == COMMAND_WOOD) {
                clearWood(positionComponent, genomeComponent, packedPos)
            } else {
                createSeed(world, positionComponent, genomeComponent, energyComponent, ageComponent, packedPos, parentGenome)
            }
        }

        /* ─── очищаем слоты родителя и высвобождаем его id ─── */
        genomeComponent[GenomeComponent.COMMANDS, id]     = EMPTY_COMMANDS
        energyComponent[EnergyComponent.ENERGY, id]       = DEFAULT_ENERGY
        ageComponent[AgeComponent.AGE, id]                = MIN_AGE

        world.entityFactory.destroy(id)
    }


    private fun createSeed(
        world: World,
        positionComponent: Component,
        genomeComponent: Component,
        energyComponent: Component,
        ageComponent: Component,
        packedPos: Int,
        parentGenome: Array<ByteArray>
    ) {
        /* ───── семя остаётся и становится новым деревом ───── */
        val newId = world.entityFactory.create()

        // привязываем клетку к новому id
        positionComponent[PositionComponent.POS_TO_ID, packedPos] = newId

        // наследуем геном
        genomeComponent[GenomeComponent.COMMANDS, newId] = parentGenome
        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPos] = getSeedCommand(packedPos)

        // стартовые параметры нового дерева
        energyComponent[EnergyComponent.ENERGY, newId] = DEFAULT_ENERGY
        ageComponent[AgeComponent.AGE, newId]          = MIN_AGE
    }

    private fun clearWood(positionComponent: Component, genomeComponent: Component, packedPos: Int) {
        /* ───── древесина: полностью очищаем ───── */
        positionComponent[PositionComponent.POS_TO_ID, packedPos]       = EMPTY_ID
        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPos] = COMMAND_EMPTY
        genomeComponent[GenomeComponent.COLOR_AT_POS, packedPos]        = Color.BLACK
    }


    private fun getSeedCommand(packedPos: Int): Byte {
        return if (PositionUtils.unpackY(packedPos) == 0) {
            START_COMMAND
        } else {
            COMMAND_FALL
        }
    }
}

