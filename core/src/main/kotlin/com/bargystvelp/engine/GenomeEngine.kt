package com.bargystvelp.engine

import com.bargystvelp.World
import com.bargystvelp.component.Genome
import com.bargystvelp.component.Vitality
import com.bargystvelp.constant.Color
import com.bargystvelp.util.Logger

object GenomeEngine : Engine() {
    override fun tick(world: World, delta: Float) {
        world.entities.forEach {
            val genome = it.get(Genome::class.java) ?: return@forEach
            val vitality = it.get(Vitality::class.java) ?: return@forEach
            genome.color = if (vitality.isDead) Color.ORGANIC else Color.PHOTOSYNTHESIS
        }
    }
}

