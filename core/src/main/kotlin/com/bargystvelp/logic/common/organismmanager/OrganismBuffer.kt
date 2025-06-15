package com.bargystvelp.logic.common.organismmanager

import com.bargystvelp.logic.common.Position
import com.bargystvelp.logic.ecosystem.organism.Organism

class OrganismBuffer {
    val toAdd: MutableMap<Position, Organism> = mutableMapOf()
    val toRemove: MutableMap<Position, Organism> = mutableMapOf()

    fun add(organism: Organism) {
        toAdd[organism.position] = organism
    }

    fun remove(organism: Organism) {
        toRemove[organism.position] = organism
    }

    fun applyTo(target: MutableMap<Position, Organism>) {
        toRemove.forEach { target.remove(it.key) }
        toAdd.forEach { target[it.key] = it.value }
        clear()
    }


    private fun clear() {
        toAdd.clear()
        toRemove.clear()
    }
}
