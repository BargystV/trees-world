package com.bargystvelp.logic.common.organismmanager

import com.bargystvelp.logic.common.Position
import com.bargystvelp.logic.common.Size
import com.bargystvelp.logic.ecosystem.organism.Organism

abstract class OrganismManager(
    val biomeSize: Size,
    val organisms: MutableMap<Position, Organism>
) {
    val organismBuffer = OrganismBuffer()

    abstract fun tick(): MutableMap<Position, Organism>
}
