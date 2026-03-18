package com.bargystvelp.world.tree.command

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.common.World
import com.bargystvelp.world.tree.AGE_COMPONENT_KEY
import com.bargystvelp.world.tree.ENERGY_COMPONENT_KEY
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.component.AgeComponent
import com.bargystvelp.world.tree.component.DEFAULT_ENERGY
import com.bargystvelp.world.tree.component.EnergyComponent
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.MIN_AGE
import com.bargystvelp.world.tree.component.PositionComponent
import com.bargystvelp.world.tree.component.START_COMMAND

/** Команда создания новой сущности в указанной позиции с заданным геномом. */
object CreateCommand {
    /**
     * Создать новую сущность: выделить ID, записать позицию, геном, энергию и возраст.
     * @param packedPosition упакованная стартовая позиция
     * @param commands       таблица команд генома
     * @param seedCommand    начальная команда семени (по умолчанию START_COMMAND)
     * @param color          начальный цвет клетки (по умолчанию WHITE)
     * @param energy         начальная энергия (по умолчанию DEFAULT_ENERGY)
     */
    fun execute(
        world: World,
        packedPosition: Int,
        commands: Array<ByteArray>,
        seedCommand: Byte = START_COMMAND,
        color: Color = com.bargystvelp.common.Color.WHITE,
        energy: Int = DEFAULT_ENERGY
    ) {
        val id = world.entityFactory.create()

        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return
        val energyComponent = world.components[ENERGY_COMPONENT_KEY] ?: return
        val ageComponent = world.components[AGE_COMPONENT_KEY] ?: return

        positionComponent[PositionComponent.POS_TO_ID, packedPosition] = id

        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPosition] = seedCommand
        genomeComponent[GenomeComponent.COMMANDS, id] = commands
        genomeComponent[GenomeComponent.COLOR_AT_POS, packedPosition] = color

        energyComponent[EnergyComponent.ENERGY, id] = energy

        ageComponent[AgeComponent.AGE, id] = MIN_AGE
    }
}
