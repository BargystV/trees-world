package com.bargystvelp.logic.ecosystem.metabolism

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.constant.Color.PREDATOR
import com.bargystvelp.logic.common.Biome
import com.bargystvelp.logic.common.Metabolism
import com.bargystvelp.logic.ecosystem.organism.Organism

class Predator : Metabolism {
    override val color: Color = PREDATOR

    override fun tick(organism: Organism, biome: Biome) {

    }

    override fun cloneWithMutation(): Metabolism {
        return this
    }
}
