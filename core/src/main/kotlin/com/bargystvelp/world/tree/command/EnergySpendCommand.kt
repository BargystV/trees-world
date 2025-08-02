package com.bargystvelp.world.tree.command

import com.bargystvelp.common.World
import com.bargystvelp.world.tree.ENERGY_COMPONENT_KEY
import com.bargystvelp.world.tree.component.EnergyComponent

object EnergySpendCommand {
    fun execute(
        world: World,
        id: Int,
        energyCost: Int
    ): Int? {
        val energyComponent = world.components[ENERGY_COMPONENT_KEY] ?: return null
        val energy = energyComponent[EnergyComponent.ENERGY, id] - energyCost

        energyComponent[EnergyComponent.ENERGY, id] = energy

        return energy
    }
}
