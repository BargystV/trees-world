package com.bargystvelp.util

object PositionUtils {

    /* 16 бит +16 бит */
    fun pack(x: Int, y: Int) = (y shl 16) or (x and 0xFFFF)
    fun unpackX(p: Int)      =  p         and 0xFFFF
    fun unpackY(p: Int)      = (p ushr 16) and 0xFFFF


    fun idx(packed: Int, width: Int): Int {
        val x = unpackX(packed);
        val y = unpackY(packed)
        return y * width + x
    }

    fun idx(x: Int, y: Int, width: Int) = y * width + x
}
