package com.bargystvelp.world.tree.command

import com.bargystvelp.common.Color
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

/** Команда уничтожения падающего семени при столкновении. Очищает все данные и освобождает ID. */
object DestroySeedCommand {
    /**
     * Очистить позицию [packed], сбросить данные компонентов сущности [id] и уничтожить её.
     */
    fun execute(world: World, id: Int, packed: Int) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY]    ?: return
        val genomeComponent   = world.components[GENOME_COMPONENT_KEY]      ?: return
        val energyComponent   = world.components[ENERGY_COMPONENT_KEY]      ?: return
        val ageComponent      = world.components[AGE_COMPONENT_KEY]         ?: return

        /* ─── очищаем слоты родителя и высвобождаем его id ─── */
        positionComponent[PositionComponent.POS_TO_ID, packed]          = EMPTY_ID
        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packed]    = COMMAND_EMPTY
        genomeComponent[GenomeComponent.COMMANDS, id]                   = EMPTY_COMMANDS
        genomeComponent[GenomeComponent.COLOR_AT_POS, packed]           = Color.BLACK
        energyComponent[EnergyComponent.ENERGY, id]                     = DEFAULT_ENERGY
        ageComponent[AgeComponent.AGE, id]                              = MIN_AGE

        world.entityFactory.destroy(id)
    }
}
