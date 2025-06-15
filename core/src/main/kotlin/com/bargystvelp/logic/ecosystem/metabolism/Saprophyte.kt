package com.bargystvelp.logic.ecosystem.metabolism

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.constant.Color.SAPROPHYTE
import com.bargystvelp.logic.common.Biome
import com.bargystvelp.logic.common.Metabolism
import com.bargystvelp.logic.ecosystem.organism.Organism

class Saprophyte : Metabolism {
    override val color: Color = SAPROPHYTE

    override fun tick(organism: Organism, biome: Biome) {

    }

    override fun cloneWithMutation(): Metabolism {
        return this
    }
}
