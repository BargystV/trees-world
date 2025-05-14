package com.bargystvelp.logic.cell

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.constant.Color.SOIL

data class Soil(
    override val x: Int,
    override val y: Int,
    override val height: Height,
) : Cell(
    x = x,
    y = y,
    height = height
) {
    override fun getColor(): Color {
        return SOIL
    }
}
