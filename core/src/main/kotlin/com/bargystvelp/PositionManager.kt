package com.bargystvelp

class PositionManager(maxEntities: Int) {
    private val x = IntArray(maxEntities)
    private val y = IntArray(maxEntities)

    fun set(id: Int, px: Int, py: Int) {
        x[id] = px;
        y[id] = py
    }

    fun getX(id: Int): Int {
        return x[id]
    }

    fun getY(id: Int): Int {
        return y[id]
    }
}
