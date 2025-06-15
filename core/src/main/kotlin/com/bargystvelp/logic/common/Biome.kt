package com.bargystvelp.logic.common

import com.bargystvelp.logic.common.organismmanager.OrganismManager
import com.bargystvelp.logic.ecosystem.organism.Organism

abstract class Biome(
    open val size: Size,
    open val organismManager: OrganismManager
) {
    abstract fun tick(): MutableMap<Position, Organism>
}
