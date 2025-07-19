package com.bargystvelp.world.tree.component

import com.bargystvelp.common.AttrKey
import com.bargystvelp.common.Component

const val EMPTY_ID   = -1
private const val EMPTY_POS = -1         // marker in idToPos when id «не на доске»

class PositionComponent(
    private val width: Int,
    private val height: Int,
    maxEntities: Int = width * height,
) : Component {

    companion object {
        /** id  ↔︎  packedPos (Int) */
        val ID_TO_POS = AttrKey<Int, Int>(0)
        /** packedPos (Int)  ↔︎  id */
        val POS_TO_ID = AttrKey<Int, Int>(1)

        /* без-объектное кодирование (16 бит + 16 бит) */
        fun pack(x: Int, y: Int) = (y shl 16) or (x and 0xFFFF)
        fun unpackX(p: Int)      =  p        and 0xFFFF
        fun unpackY(p: Int)      = (p ushr 16) and 0xFFFF
    }

    /* id ➜ packedPos */
    private val idToPos = IntArray(maxEntities) { EMPTY_POS }
    /* (y * width + x) ➜ id */
    private val posToId = IntArray(maxEntities) { EMPTY_ID }

    /* ────────── Component API ────────── */

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V) = when (type) {
        ID_TO_POS -> commitPosition(id = key as Int, packed = value as Int)
        POS_TO_ID -> commitPosition(id = value as Int, packed = key as Int)
        else -> error("bad key")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V = when (type) {
        ID_TO_POS -> idToPos[key as Int]                    as V
        POS_TO_ID -> getIdAtPacked(key as Int)     as V
        else      -> error("bad key")
    }

    /* ────────── внутреннее ────────── */

    private fun idx(x: Int, y: Int) = y * width + x

    private fun commitPosition(id: Int, packed: Int) {
        require(id in idToPos.indices)

        val x = unpackX(packed)
        val y = unpackY(packed)
        require(x in 0 until width && y in 0 until height)

        /* 1️⃣ вытеснить прежнего обитателя клетки, если он есть */
        val victim = posToId[idx(x, y)]
        if (victim != EMPTY_ID && victim != id) {
            idToPos[victim] = EMPTY_POS
        }

        /* 2️⃣ стереть старую клетку самого id (если стоял где-то ещё) */
        val oldPacked = idToPos[id]
        if (oldPacked != EMPTY_POS) {
            posToId[idx(unpackX(oldPacked), unpackY(oldPacked))] = EMPTY_ID
        }

        /* 3️⃣ записать новую позицию */
        idToPos[id] = packed
        posToId[idx(x, y)] = id
    }

    /** Безопасно возвращает id или EMPTY_ID. */
    private fun getIdAtPacked(packed: Int): Int =
        posToId.getOrElse(idx(unpackX(packed), unpackY(packed))) { EMPTY_ID }
}
