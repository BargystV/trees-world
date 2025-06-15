package com.bargystvelp.logic.ecosystem

import com.bargystvelp.Logger
import com.bargystvelp.logic.common.organismmanager.OrganismManager
import com.bargystvelp.logic.common.Position
import com.bargystvelp.logic.common.Size
import com.bargystvelp.logic.ecosystem.organism.Organism
import kotlin.collections.remove
import kotlin.random.Random

class EcosystemOrganismManager(
    biomeSize: Size
) : OrganismManager(
    biomeSize = biomeSize,
    organisms = createFirst(biomeSize = biomeSize)
) {
    companion object {
        private fun createFirst(biomeSize: Size): MutableMap<Position, Organism> {
            val position = Position(
                x = Random.nextInt(biomeSize.width),
                y = Random.nextInt(biomeSize.height)
            )

            return mutableMapOf(position to Organism(position))
        }
    }

    override fun tick(): MutableMap<Position, Organism> {
        val start = System.nanoTime()

        organisms.values.forEach { organism ->
            organism.tickLife()
            organism.tryReproduce(this)
        }

        organismBuffer.applyTo(organisms)

        val end = System.nanoTime()
        val ms = (end - start) / 1_000_000.0

//        Logger.info("%.3f ms (Organisms: ${organisms.size})".format(ms))

        return organisms
    }

}
