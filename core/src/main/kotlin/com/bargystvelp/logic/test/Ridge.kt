package com.bargystvelp.logic.test

import com.bargystvelp.logic.cell.Height
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

// ⛰️ Хребет задаётся как линия от одной точки к другой на карте
data class Ridge(
    val xFrom: Int,                                                 // Начальная X координата хребта
    val yFrom: Int,                                                 // Начальная Y координата
    val xTo: Int,                                                   // Конечная X координата
    val yTo: Int,                                                   // Конечная Y координата
    val maxHeight: Double = Height.maxHeight().value.toDouble()     // Максимальная высота хребта
) {
    // Чем ближе точка к хребту — тем выше значение высоты (экспоненциальный спад)
    // → Чем меньше distance → тем ближе к 0 → exp ближе к 1 → contribution ближе к maxHeight
    // → Чем дальше distance → тем exp ближе к 0 → высота стремится к нулю
    fun calculateContribution(x: Int, y: Int): Double {
        val distance = distanceToSegment(x, y)
        return maxHeight * exp(-distance / 10.0) // экспоненциальное затухание
    }

    // Вычисляем расстояние от текущей точки (x, y) до линии хребта
    private fun distanceToSegment(x: Int, y: Int): Double {
        val px = xTo - xFrom
        val py = yTo - yFrom
        val norm = px * px + py * py
        val u = ((x - xFrom) * px + (y - yFrom) * py).toDouble() / norm
        val clampedU = u.coerceIn(0.0, 1.0)
        val closestX = xFrom + clampedU * px
        val closestY = yFrom + clampedU * py
        return sqrt((x - closestX).pow(2) + (y - closestY).pow(2))
    }
}
