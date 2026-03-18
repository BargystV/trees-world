package com.bargystvelp.world.tree.command

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.common.World
import com.bargystvelp.logger.Logger
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.ENERGY_COMPONENT_KEY
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.component.ENERGY_TO_GROW
import com.bargystvelp.world.tree.component.EnergyComponent
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.PositionComponent
import com.bargystvelp.world.tree.component.START_COMMAND

/** Команда роста: добавить новую семенную клетку к существующей сущности. */
object GrowCommand {
    /**
     * Добавить клетку в позицию [packedPosition] для сущности [id].
     * Списывает [ENERGY_TO_GROW] энергии. Устанавливает команду [seedCommand] и цвет [color].
     */
    fun execute(
        world: World,
        id: Int,
        packedPosition: Int,
        seedCommand: Byte = START_COMMAND,
        color: Color = com.bargystvelp.common.Color.WHITE,
    ) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY] ?: return
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return
        val energyComponent = world.components[ENERGY_COMPONENT_KEY] ?: return

        positionComponent[PositionComponent.POS_TO_ID, packedPosition] = id

        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPosition] = seedCommand
        genomeComponent[GenomeComponent.COLOR_AT_POS, packedPosition] = color

        energyComponent[EnergyComponent.ENERGY, id] = energyComponent[EnergyComponent.ENERGY, id] - ENERGY_TO_GROW

        val x = PositionUtils.unpackX(packedPosition)
        val y = PositionUtils.unpackY(packedPosition)

//        Logger.info("id: $id x: $x y: $y")
    }
}
