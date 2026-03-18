package com.bargystvelp.util

/**
 * Утилиты для работы с 2D-позициями, упакованными в один Int.
 * Формат: старшие 16 бит — Y, младшие 16 бит — X.
 */
object PositionUtils {

    /* 16 бит +16 бит */
    /** Упаковать координаты (x, y) в один Int. */
    fun pack(x: Int, y: Int) = (y shl 16) or (x and 0xFFFF)
    /** Извлечь X-координату из упакованной позиции. */
    fun unpackX(p: Int)      =  p         and 0xFFFF
    /** Извлечь Y-координату из упакованной позиции. */
    fun unpackY(p: Int)      = (p ushr 16) and 0xFFFF

    /** Линейный индекс в массиве по упакованной позиции и ширине сетки. */
    fun idx(packed: Int, width: Int): Int {
        val x = unpackX(packed);
        val y = unpackY(packed)
        return y * width + x
    }

    /** Линейный индекс в массиве по координатам (x, y) и ширине сетки. */
    fun idx(x: Int, y: Int, width: Int) = y * width + x
}
