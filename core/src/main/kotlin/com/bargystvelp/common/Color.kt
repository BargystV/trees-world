package com.bargystvelp.common

import com.badlogic.gdx.graphics.Color

/**
 * Палитра цветов симуляции.
 * Используется при рендеринге клеток через GenomeComponent.
 */
object Color {
    val PHOTOSYNTHESIS      = Color(0.2f, 0.8f, 0.2f, 1f) // ярко-зелёный — древесина, фотосинтез
    val SAPROPHYTE          = Color(0.4f, 0.3f, 0.1f, 1f) // тёмно-коричневый, болотный
    val PREDATOR            = Color(0.8f, 0.1f, 0.1f, 1f) // кроваво-красный
    val ORGANIC             = Color(0.36f, 0.26f, 0.13f, 1f)

    val BLACK               = Color(0f, 0f, 0f, 1f) // фон, пустые клетки
    val WHITE               = Color(255f, 255f, 255f, 1f) // семена (до превращения в дерево)
}
