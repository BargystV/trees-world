package com.bargystvelp.logic.biome

import com.bargystvelp.logic.cell.Cell

data class Forest(
    override val width: Int,
    override val height: Int
) : Biome(
    width = width,
    height = height
) {
    override fun createBiome(
        width: Int,
        height: Int
    ): Array<Array<Cell>> {
        TODO("Not yet implemented")
    }
}
