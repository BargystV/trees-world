package com.bargystvelp.logic.biome

data class Forest(
    override val width: Int,
    override val height: Int
) : Biome(
    width = width,
    height = height
)
