package com.bargystvelp

import com.bargystvelp.engine.GenomeEngine
import com.bargystvelp.util.Logger

class World(val size: Size) {
    private val capacity = size.height * size.width

    private val engines = listOf(GenomeEngine)

    val entityManager = EntityManager(capacity)
    val positionManager = PositionManager(capacity)
    val genomeManager = GenomeManager(capacity)


    init {
        repeat(1) {
            val adam = entityManager.create()

            Logger.info("adam: $adam")

            positionManager.set(adam, size.width / 2, size.height / 2)
            genomeManager.set(adam, ALL_COMMANDS.shuffledCopy())
        }
    }

    fun tick(delta: Float) {
        engines.forEach { engine ->
            engine.tick(this, delta)
        }
    }
}
