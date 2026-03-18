package com.bargystvelp.world.tree.component

import com.bargystvelp.common.AttrKey
import com.bargystvelp.common.Component
import com.bargystvelp.util.PositionUtils

const val EMPTY_ID = -1
const val NO_POS = -1                     // sentinel for end-of-list

/**
 * Компонент позиций сущностей.
 * Каждая сущность может занимать несколько клеток (тело дерева).
 * Хранение — двусвязные списки на примитивных массивах; все операции O(1), без аллокаций.
 *
 * Ключевые операции:
 *  - [POS_TO_ID]      — позиция → entity ID (или [EMPTY_ID])
 *  - [ID_TO_POS_LIST] — entity ID → список всех занятых позиций
 */
class PositionComponent(
    private val maxEntities: Int,
    private val width: Int,
    private val height: Int,
) : Component {

    companion object {
        /** id → IntArray(packedPos) */
        val ID_TO_POS_LIST = AttrKey<Int, IntArray>(0)
        /** packedPos → id (or EMPTY_ID) */
        val POS_TO_ID      = AttrKey<Int, Int>(1)

        /** Проверить, занята ли клетка (x, y) любой сущностью. */
        fun isOccupied(x: Int, y: Int, pc: Component): Boolean =
            pc[POS_TO_ID, PositionUtils.pack(x, y)] != EMPTY_ID
    }

    /* ─────────── storage ─────────── */

    private val gridSize = width * height
    private val posToId  = IntArray(gridSize) { EMPTY_ID }   // packedPos → id

    // single-linked lists per tree
    private val nextPos  = IntArray(gridSize) { NO_POS }
    private val prevPos  = IntArray(gridSize) { NO_POS }

    private val idHead   = IntArray(maxEntities) { NO_POS }  // id → head idx
    private val idCount  = IntArray(maxEntities)             // id → list size

    /* ─────────── Component API ─────────── */

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V) = when (type) {
        POS_TO_ID -> {
            val packed  = key as Int
            val id      = value as Int
            if (id == EMPTY_ID) removePosition(packed) else addPosition(id, packed)
        }
        ID_TO_POS_LIST -> replacePositions(id = key as Int, newList = value as IntArray)
        else           -> error("Unsupported AttrKey")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V = when (type) {
        ID_TO_POS_LIST -> positionsOf(key as Int)   as V
        POS_TO_ID      -> getIdAtPacked(key as Int) as V
        else           -> error("Unsupported AttrKey")
    }

    /* ─────────── core ops ─────────── */

    /** Привязать позицию [packed] к сущности [id] (добавить в её список). */
    private fun addPosition(id: Int, packed: Int) {
        validateId(id); validatePackedForWrite(packed)
        val idx = PositionUtils.idx(packed, width)
        val old = posToId[idx]
        if (old == id) return                   // already ours
        if (old != EMPTY_ID) unlinkPosition(old, idx)
        linkPosition(id, idx)
    }

    /** Освободить позицию [packed] (удалить из списка владельца). */
    private fun removePosition(packed: Int) {
        validatePackedForWrite(packed)
        val idx = PositionUtils.idx(packed, width)
        val owner = posToId[idx]
        if (owner != EMPTY_ID) unlinkPosition(owner, idx)
    }

    /** Полностью заменить список позиций сущности [id] новым списком [newList]. */
    private fun replacePositions(id: Int, newList: IntArray) {
        validateId(id)
        // validate new cells & duplicates
        if (newList.isNotEmpty()) {
            val seen = HashSet<Int>(newList.size * 2)
            for (p in newList) {
                validatePackedForWrite(p)
                require(seen.add(p)) { "duplicate packed position: $p" }
            }
        }

        // clear old
        var p = idHead[id]
        while (p != NO_POS) {
            val nxt = nextPos[p]
            posToId[p] = EMPTY_ID
            nextPos[p] = NO_POS
            prevPos[p] = NO_POS
            p = nxt
        }
        idHead[id]  = NO_POS
        idCount[id] = 0

        // add new
        for (p in newList) addPosition(id, p)
    }

    /** Вернуть массив всех упакованных позиций сущности [id] (новый массив каждый раз). */
    private fun positionsOf(id: Int): IntArray {
        validateId(id)
        val out = IntArray(idCount[id])
        var i = 0
        var p = idHead[id]
        while (p != NO_POS) {
            out[i++] = PositionUtils.pack(p % width, p / width)
            p = nextPos[p]
        }
        return out
    }

    /** Вернуть ID сущности в клетке [packed], или [EMPTY_ID] если клетка вне поля или пуста. */
    private fun getIdAtPacked(packed: Int): Int {
        val x = PositionUtils.unpackX(packed)
        val y = PositionUtils.unpackY(packed)
        if (x !in 0 until width || y !in 0 until height) return EMPTY_ID
        return posToId[PositionUtils.idx(packed, width)]
    }

    /* ─────────── list helpers ─────────── */

    /** Исключить позицию [idx] из двусвязного списка владельца [ownerId]. */
    private fun unlinkPosition(ownerId: Int, idx: Int) {
        val prev = prevPos[idx]
        val next = nextPos[idx]

        if (prev != NO_POS) nextPos[prev] = next else idHead[ownerId] = next
        if (next != NO_POS) prevPos[next] = prev

        posToId[idx] = EMPTY_ID
        nextPos[idx] = NO_POS
        prevPos[idx] = NO_POS
        idCount[ownerId]--
    }

    /** Включить позицию [idx] в начало двусвязного списка сущности [id]. */
    private fun linkPosition(id: Int, idx: Int) {
        val head = idHead[id]
        posToId[idx] = id
        prevPos[idx] = NO_POS
        nextPos[idx] = head
        if (head != NO_POS) prevPos[head] = idx
        idHead[id] = idx
        idCount[id]++
    }

    /* ─────────── validation ─────────── */

    /** Проверить корректность entity ID. */
    private fun validateId(id: Int) =
        require(id in 0 until maxEntities) { "id $id out of range [0, ${maxEntities - 1}]" }

    /** Проверить, что упакованная позиция находится в пределах сетки. */
    private fun validatePackedForWrite(packed: Int) {
        val x = PositionUtils.unpackX(packed)
        val y = PositionUtils.unpackY(packed)
        require(x in 0 until width && y in 0 until height) { "packed position out of board: x=$x, y=$y" }
    }
}
