package com.bargystvelp.world.tree.command

import com.bargystvelp.common.World
import com.bargystvelp.world.tree.AGE_COMPONENT_KEY
import com.bargystvelp.world.tree.component.AgeComponent

object AgeUpCommand {
    fun execute(
        world: World,
        id: Int
    ): Int?  {
        val ageComponent = world.components[AGE_COMPONENT_KEY] ?: return null
        val age = ageComponent[AgeComponent.AGE, id] + 1

        ageComponent[AgeComponent.AGE, id] = age

        return age
    }
}
