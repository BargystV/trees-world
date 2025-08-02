package com.bargystvelp.world.tree.component

import com.bargystvelp.common.AttrKey
import com.bargystvelp.common.Component

const val MAX_AGE = 100
const val MIN_AGE = 0

class AgeComponent(
    private val maxEntities: Int
): Component {
    companion object {
        val AGE = AttrKey<Int, Int>(0)
    }

    private val ages = IntArray(maxEntities) { MIN_AGE }

    /* ───────────── Component API ───────────── */
    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V) {
        when (type) {
            AGE     -> ages[key as Int] = value as Int
            else    -> error("bad AttrKey for AgeComponent")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V =
        when (type) {
            AGE     -> ages[key as Int] as V
            else    -> error("bad AttrKey for AgeComponent")
        }
}
