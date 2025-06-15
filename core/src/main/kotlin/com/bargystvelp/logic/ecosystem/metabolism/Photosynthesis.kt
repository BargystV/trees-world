package com.bargystvelp.logic.ecosystem.metabolism

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.constant.Color.PHOTOSYNTHESIS
import com.bargystvelp.logic.common.Biome
import com.bargystvelp.logic.common.Metabolism
import com.bargystvelp.logic.ecosystem.organism.Organism
import kotlin.math.min

class Photosynthesis() : Metabolism {
    override val color: Color = PHOTOSYNTHESIS

    companion object {
        const val ENERGY_TICK = 100
    }

    override fun tick(organism: Organism, biome: Biome) {
//        val transferAmount = min(ENERGY_TICK, biome.biomeEnergy)
//
//        biome.biomeEnergy -= transferAmount
//        organism.energy += transferAmount

        organism.energy += ENERGY_TICK
    }

    override fun cloneWithMutation(): Metabolism {
        return this
    }
}
