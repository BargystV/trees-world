package com.bargystvelp.world.tree.command

import com.bargystvelp.common.World
import com.bargystvelp.world.tree.AGE_COMPONENT_KEY
import com.bargystvelp.world.tree.component.AgeComponent

/** Команда увеличения возраста сущности на 1. Возвращает новый возраст или null если компонент недоступен. */
object AgeUpCommand {
    /**
     * Увеличить возраст сущности [id] на 1 и вернуть новое значение.
     * @return новый возраст, или null если AgeComponent недоступен
     */
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
