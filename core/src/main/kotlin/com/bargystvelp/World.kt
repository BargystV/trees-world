package com.bargystvelp

import com.bargystvelp.engine.GenomeEngine
import com.bargystvelp.engine.ReproduceEngine
import com.bargystvelp.engine.VitalityEngine
import com.bargystvelp.component.Position
import kotlin.random.Random

class World(val size: Size) {
//    private val systems = listOf(VitalityEngine, GenomeEngine, ReproduceEngine)
    private val systems = listOf(VitalityEngine)
//    internal val entities = mutableListOf(createAdam())
    internal val entities = createAdam()

    fun tick(delta: Float): List<Entity> {
        systems.forEach { it.tick(this, delta) }

        return entities
    }

//    private fun createAdam() = Factory.organism(
////        Position(
////            Random.nextInt(0, size.width),
////            Random.nextInt(0, size.height)
////        )
//        Position(
//            size.width / 2,
//            size.height / 2
//        )
//    )

    private fun createAdam(): MutableList<Entity> {
        val list = mutableListOf<Entity>()

        repeat(2000000) {
            list.add(Factory.organism(
                Position(
                    Random.nextInt(0, size.width),
                    Random.nextInt(0, size.height)
                )
            ))
        }

        return list
    }
}
