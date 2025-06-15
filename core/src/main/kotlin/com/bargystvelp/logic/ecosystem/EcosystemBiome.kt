package com.bargystvelp.logic.ecosystem

import com.bargystvelp.logic.common.Biome
import com.bargystvelp.logic.common.Position
import com.bargystvelp.logic.common.Size
import com.bargystvelp.logic.ecosystem.organism.Organism

class EcosystemBiome(
    override val size: Size
) : Biome(
    size = size,
     organismManager = EcosystemOrganismManager(size)
) {
    override fun tick(): MutableMap<Position, Organism> {
        return organismManager.tick()
    }
}
