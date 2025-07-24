package com.bargystvelp.world.tree.component

import com.bargystvelp.common.AttrKey
import com.bargystvelp.common.Component
import com.bargystvelp.util.PositionUtils

const val EMPTY_ID = -1
private const val NO_POS   = -1

class PositionComponent(
    private val maxEntities: Int,
    private val width: Int,
    private val height: Int,
) : Component {

    companion object {
        val ID_TO_POS_LIST = AttrKey<Int, IntArray>(0)   // id  →  [packedPos]
        val POS_TO_ID      = AttrKey<Int, Int>(1)        // packedPos → id
    }

    /* ─────────── внутренние структуры ─────────── */

    private val headById = IntArray(maxEntities) { NO_POS }          // id → голова списка клеток
    private val nextPos  = IntArray(width * height) { NO_POS }       // idx → следующий
    private val posToId  = IntArray(width * height) { EMPTY_ID }     // idx → id

    /* кеш + «грязность» */
    private val snap  = Array(maxEntities) { IntArray(0) }
    private val dirty = BooleanArray(maxEntities)

    /* ───────────── Component API ───────────── */

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V) = when (type) {
        POS_TO_ID      -> addPosition(id = value as Int, packed = key as Int)
        ID_TO_POS_LIST -> replacePositions(id = key as Int, newList = value as IntArray)
        else           -> error("bad AttrKey")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V = when (type) {
        ID_TO_POS_LIST -> positionsOf(key as Int)   as V
        POS_TO_ID      -> getIdAtPacked(key as Int) as V
        else           -> error("bad AttrKey")
    }

    /* ─────────────── логика ─────────────── */

    /** Добавляет ещё одну позицию `id`, не удаляя предыдущие */
    private fun addPosition(id: Int, packed: Int) {
        require(id in 0 until maxEntities)

        val x = PositionUtils.unpackX(packed)
        val y = PositionUtils.unpackY(packed)
        require(x in 0 until width && y in 0 until height)

        val pIdx = PositionUtils.idx(x, y, width)

        /* если id уже стоит ровно в этой клетке → ничего не делаем */
        var cur = headById[id]
        while (cur != NO_POS) {
            if (cur == pIdx) return
            cur = nextPos[cur]
        }

        /* вытеснить чужого жильца */
        val occupant = posToId[pIdx]
        if (occupant != EMPTY_ID && occupant != id) {
            removePosFromId(occupant, pIdx)
            dirty[occupant] = true
        }

        /* вставляем pIdx в начало списка id */
        nextPos[pIdx] = headById[id]
        headById[id]  = pIdx
        posToId[pIdx] = id
        dirty[id]     = true
    }

    /** Полностью заменяет все позиции `id` на `newList` */
    private fun replacePositions(id: Int, newList: IntArray) {
        require(id in 0 until maxEntities)

        // 1️⃣ очистить старые позиции
        clearAllPositions(id)

        // 2️⃣ добавить новые
        for (packed in newList) {
            addPosition(id, packed)   // использует логику вытеснения чужих
        }
        dirty[id] = true
    }

    private fun removePosFromId(id: Int, pIdx: Int) {
        var prev = NO_POS
        var cur  = headById[id]
        while (cur != NO_POS) {
            if (cur == pIdx) {
                if (prev == NO_POS) headById[id] = nextPos[cur]
                else                nextPos[prev] = nextPos[cur]
                nextPos[cur] = NO_POS
                posToId[pIdx] = EMPTY_ID
                return
            }
            prev = cur; cur = nextPos[cur]
        }
    }

    private fun clearAllPositions(id: Int) {
        var p = headById[id]
        while (p != NO_POS) {
            val nxt = nextPos[p]
            posToId[p] = EMPTY_ID
            nextPos[p] = NO_POS
            p = nxt
        }
        headById[id] = NO_POS
        dirty[id]    = true
    }

    private fun positionsOf(id: Int): IntArray {
        if (!dirty[id]) return snap[id]

        // пересчёт
        var cnt = 0; var p = headById[id]
        while (p != NO_POS) { cnt++; p = nextPos[p] }

        var arr = snap[id]
        if (arr.size != cnt) arr = IntArray(cnt)

        p = headById[id]; var i = 0
        while (p != NO_POS) {
            arr[i++] = PositionUtils.pack(p % width, p / width)
            p = nextPos[p]
        }
        snap[id]  = arr
        dirty[id] = false
        return arr
    }

    private fun getIdAtPacked(packed: Int): Int =
        posToId.getOrElse(PositionUtils.idx(packed, width)) { EMPTY_ID }
}
