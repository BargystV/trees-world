package com.bargystvelp.logic.common

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.logic.ecosystem.organism.Organism

interface Metabolism {
    val color: Color

    fun tick(organism: Organism, biome: Biome)
    fun cloneWithMutation(): Metabolism
}
