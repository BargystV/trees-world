package com.bargystvelp.world.tree.command

import com.bargystvelp.common.World
import com.bargystvelp.world.tree.ENERGY_COMPONENT_KEY
import com.bargystvelp.world.tree.component.EnergyComponent

/** Команда начисления энергии фотосинтеза сущности. */
object PhotosynthesisCommand {
    /** Добавить [energy] единиц к запасу энергии сущности [id]. */
    fun execute(
        world: World,
        id: Int,
        energy: Int
    ) {
        val energyComponent = world.components[ENERGY_COMPONENT_KEY] ?: return

        energyComponent[EnergyComponent.ENERGY, id] = energyComponent[EnergyComponent.ENERGY, id] + energy
    }
}
