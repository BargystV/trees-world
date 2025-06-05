package com.bargystvelp.logic.biome

import com.bargystvelp.OpenSimplexNoise
import com.bargystvelp.logic.cell.Cell
import com.bargystvelp.logic.cell.Height
import com.bargystvelp.logic.cell.Stone
import com.bargystvelp.logic.test.Ridge
import kotlin.math.roundToInt

class Mountains (
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
        val ridge = Ridge(xFrom = 0, yFrom = height / 3, xTo = width, yTo = height * 2 / 3)

        return Array(width) { x ->
            Array(height) { y ->
                val ridgeContribution = ridge.calculateContribution(x, y)

                // Добавляем детализирующий шум, чтобы сделать поверхность "живой"
                val noise = OpenSimplexNoise.noise2(x = x / 10.0, y = y / 10.0) * 0.5   // Масштабируем амплитуду шума (максимум ±0.5)
                // Суммируем высоту от хребта и от шума
                val rawHeight = ridgeContribution + noise
                // Приводим результат к целому числу и ограничиваем в допустимом диапазоне
                val z = rawHeight.roundToInt().coerceIn(Height.minHeight().value, Height.maxHeight().value)
                // Создаём каменную клетку на позиции (x, y) с высотой z
                Stone(x, y, Height.fromInt(z))
            }
        }
    }
}
