package com.bargystvelp.logic.biome

import com.bargystvelp.OpenSimplexNoise
import com.bargystvelp.logic.cell.Cell
import com.bargystvelp.logic.cell.Height
import com.bargystvelp.logic.cell.Stone
import com.bargystvelp.logic.cell.Water
import com.bargystvelp.logic.test.Ridge
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

abstract class Biome(
    open val width: Int,
    open val height: Int,
) {
    val cells: Array<Array<Cell>>

    init {
        cells = createBiome(width, height)
    }

    fun render(callback: (Cell) -> Unit) {
        for (x in 0 until width) {
            for (y in 0 until height) {
                callback(cells[x][y])
            }
        }
    }

    protected abstract fun createBiome(width: Int, height: Int): Array<Array<Cell>>
}
