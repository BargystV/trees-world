package com.bargystvelp.logic.cell

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.constant.Color.WATER_Z0
import com.bargystvelp.constant.Color.WATER_Z1
import com.bargystvelp.constant.Color.WATER_Z2
import com.bargystvelp.constant.Color.WATER_Z3
import com.bargystvelp.constant.Color.WATER_Z4
import com.bargystvelp.constant.Color.WATER_Z5
import com.bargystvelp.constant.Color.WATER_Z6
import com.bargystvelp.constant.Color.WATER_Z7
import com.bargystvelp.constant.Color.WATER_Z8
import com.bargystvelp.constant.Color.WATER_Z9

data class Water(
    override val x: Int,
    override val y: Int,
    override val height: Height,
) : Cell(
    x = x,
    y = y,
    height = height
) {
    companion object {
        private val palette = arrayOf(
            WATER_Z0, WATER_Z1, WATER_Z2, WATER_Z3, WATER_Z4,
            WATER_Z5, WATER_Z6, WATER_Z7, WATER_Z8, WATER_Z9
        )
    }

    override fun getColor(): Color = palette[height.value]
}
