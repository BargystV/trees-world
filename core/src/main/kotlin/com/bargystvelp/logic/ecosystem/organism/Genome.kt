package com.bargystvelp.logic.ecosystem.organism

import com.bargystvelp.logic.common.Biome

data class Genome(
    val moveSpeed: Int,
    val vision: Int,
    val lifetime: Int,
) {
    constructor(): this(
        moveSpeed = 0,
        vision = 0,
        lifetime = 100
    )

    fun tick(organism: Organism, biome: Biome) {

    }

    fun mutate(): Genome = Genome(
        moveSpeed = (moveSpeed + (-1..1).random()).coerceIn(0, 10),
        vision = (vision + (-1..1).random()).coerceIn(0, 10),
        lifetime = (lifetime + (-10..10).random()).coerceIn(10, 1000)
    )

}
