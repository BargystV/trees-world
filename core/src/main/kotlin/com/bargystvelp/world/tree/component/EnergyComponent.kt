package com.bargystvelp.world.tree.component

import com.bargystvelp.common.AttrKey
import com.bargystvelp.common.Component

const val DEFAULT_ENERGY = 300
const val ENERGY_TO_GROW = 18

/**
 * Компонент энергии сущностей.
 * Хранит запас энергии каждой сущности в IntArray.
 * Энергия тратится на рост ([ENERGY_TO_GROW] за клетку) и содержание древесины.
 */
class EnergyComponent(
    private val maxEntities: Int
): Component {
    companion object {
        val ENERGY = AttrKey<Int, Int>(0)

        /**
         * Проверить, хватает ли энергии сущности [id] хотя бы на один рост.
         * [directions] — массив направлений из таблицы команд генома.
         */
        fun hasEnoughEnergy(directions: ByteArray, id: Int, component: Component): Boolean {
            var needEnergy = 0

            directions.forEach { command ->
                if (command == COMMAND_EMPTY) return@forEach

                needEnergy += ENERGY_TO_GROW
            }

            return component[ENERGY, id] >= ENERGY_TO_GROW
        }
    }

    private val energies = IntArray(maxEntities) { DEFAULT_ENERGY }

    /* ───────────── Component API ───────────── */
    /** Записать значение атрибута. Поддерживает только [ENERGY]. */
    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V) {
        when (type) {
            ENERGY  -> energies[key as Int] = value as Int
            else    -> error("bad AttrKey for EnergyComponent")
        }
    }

    /** Прочитать значение атрибута. Поддерживает только [ENERGY]. */
    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V =
        when (type) {
            ENERGY  -> energies[key as Int] as V
            else    -> error("bad AttrKey for EnergyComponent")
        }
}
