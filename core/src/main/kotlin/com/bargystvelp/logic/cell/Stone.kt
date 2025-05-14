package com.bargystvelp.logic.cell

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.constant.Color.STONE_Z0
import com.bargystvelp.constant.Color.STONE_Z1
import com.bargystvelp.constant.Color.STONE_Z2
import com.bargystvelp.constant.Color.STONE_Z3
import com.bargystvelp.constant.Color.STONE_Z4
import com.bargystvelp.constant.Color.STONE_Z5
import com.bargystvelp.constant.Color.STONE_Z6
import com.bargystvelp.constant.Color.STONE_Z7
import com.bargystvelp.constant.Color.STONE_Z8
import com.bargystvelp.constant.Color.STONE_Z9

data class Stone(
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
            STONE_Z0, STONE_Z1, STONE_Z2, STONE_Z3, STONE_Z4,
            STONE_Z5, STONE_Z6, STONE_Z7, STONE_Z8, STONE_Z9
        )
    }

    override fun getColor(): Color = palette[height.value]
}
