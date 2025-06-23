package com.bargystvelp.engine

import com.bargystvelp.World
import com.bargystvelp.component.Vitality
import com.bargystvelp.util.Logger

object VitalityEngine : Engine() {
    override fun tick(world: World, delta: Float) {
        world.entities.forEach {
            val vitality = it.get(Vitality::class.java) ?: return@forEach
            vitality.age++
            vitality.energy += 100
            vitality.isDead = vitality.energy <= 0 || vitality.age >= 100
        }
    }
}
