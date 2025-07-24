package com.bargystvelp.world.tree.component

import com.bargystvelp.common.AttrKey
import com.bargystvelp.common.Component
import com.bargystvelp.util.PositionUtils

const val EMPTY_ID  = -1
private const val NO_POS = -1                // sentinel для конца списка

/**
 * Хранит взаимо‑отображение:
 *
 * * **id → список позиций** (одно дерево ↔︎ несколько клеток)
 * * **позиция → id** (моментальный ответ «кто стоит в клетке»)
 *
 * Реализация на чистых примитивах: `IntArray` + односвязные списки.
 * Все операции, вызываемые внутри горячего цикла симуляции, работают за O(1)
 * и без дополнительных аллокаций.
 */
class PositionComponent(
    private val maxEntities: Int,
    private val width: Int,
    private val height: Int,
) : Component {

    companion object {
        /** id → IntArray(packedPos) */
        val ID_TO_POS_LIST = AttrKey<Int, IntArray>(0)
        /** packedPos → id */
        val POS_TO_ID      = AttrKey<Int, Int>(1)
    }

    /* ─────────── внутреннее хранилище ─────────── */

    private val gridSize = width * height           // кол‑во клеток на доске

    private val posToId  = IntArray(gridSize) { EMPTY_ID } // позиция → id

    // внутренний связный список позиций дерева
    private val nextPos  = IntArray(gridSize) { NO_POS }
    private val prevPos  = IntArray(gridSize) { NO_POS }

    private val idHead   = IntArray(maxEntities) { NO_POS } // id → head‑idx
    private val idCount  = IntArray(maxEntities)            // id → размер списка

    /* ───────────── Component API ───────────── */

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V) = when (type) {
        POS_TO_ID      -> addPosition(id = value as Int, packed = key as Int)
        ID_TO_POS_LIST -> replacePositions(id = key as Int, newList = value as IntArray)
        else           -> error("Unsupported AttrKey")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V = when (type) {
        ID_TO_POS_LIST -> positionsOf(key as Int)   as V
        POS_TO_ID      -> getIdAtPacked(key as Int) as V
        else           -> error("Unsupported AttrKey")
    }

    /* ────────── базовые операции ────────── */

    /** Добавляет (или переназначает) клетку `packed` дереву `id`. */
    private fun addPosition(id: Int, packed: Int) {
        validateId(id)
        validatePackedForWrite(packed)

        val idx      = PositionUtils.idx(packed, width)
        val occupant = posToId[idx]

        // если клетка уже принадлежит нужному id — ничего делать не надо
        if (occupant == id) return

        // если занята – отцепляем от старого списка
        if (occupant != EMPTY_ID) unlinkPosition(occupant, idx)

        linkPosition(id, idx)
    }

    /** Заменяет всё тело дерева `id` новым списком позиций. */
    private fun replacePositions(id: Int, newList: IntArray) {
        validateId(id)

        // проверка новых позиций (границы + дубликаты)
        if (newList.isNotEmpty()) {
            val seen = HashSet<Int>(newList.size * 2)
            for (packed in newList) {
                validatePackedForWrite(packed)
                if (!seen.add(packed))
                    throw IllegalArgumentException("duplicate packed position: $packed")
            }
        }

        // удалить старые
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

        // добавить новые
        for (packed in newList) addPosition(id, packed)
    }

    /** Возвращает packed‑позиции дерева `id` (новый массив). */
    private fun positionsOf(id: Int): IntArray {
        validateId(id)
        val out = IntArray(idCount[id])
        var i   = 0
        var p   = idHead[id]
        while (p != NO_POS) {
            out[i++] = PositionUtils.pack(p % width, p / width)
            p = nextPos[p]
        }
        return out
    }

    /**
     * id в клетке `packed` или EMPTY_ID, если клетка свободна **или лежит
     * за пределами доски**.  Чтение «за борт» безопасно для удобства тестов
     * и визуализации — это операция только на чтение, не влияющая на данные.
     */
    private fun getIdAtPacked(packed: Int): Int {
        val x = PositionUtils.unpackX(packed)
        val y = PositionUtils.unpackY(packed)
        if (x !in 0 until width || y !in 0 until height) return EMPTY_ID

        val idx = PositionUtils.idx(packed, width)
        return posToId[idx]
    }

    /* ──────── утилиты linked‑list ─────── */

    private fun unlinkPosition(ownerId: Int, idx: Int) {
        val prev = prevPos[idx]
        val next = nextPos[idx]

        if (prev != NO_POS) nextPos[prev] = next else idHead[ownerId] = next
        if (next != NO_POS) prevPos[next] = prev

        posToId[idx]  = EMPTY_ID
        nextPos[idx]  = NO_POS
        prevPos[idx]  = NO_POS
        idCount[ownerId]--
    }

    private fun linkPosition(id: Int, idx: Int) {
        val head      = idHead[id]

        posToId[idx]  = id
        prevPos[idx]  = NO_POS
        nextPos[idx]  = head
        if (head != NO_POS) prevPos[head] = idx

        idHead[id]    = idx
        idCount[id]++
    }

    /* ──────────── валидация данных ──────────── */

    private fun validateId(id: Int) {
        require(id in 0 until maxEntities) { "id $id out of range [0, ${maxEntities - 1}]" }
    }

    /**
     * Строгая проверка для **записывающих** операций (`set`, `replace`).
     * Для чтения мы разрешаем выходить за границы, просто возвращая EMPTY_ID.
     */
    private fun validatePackedForWrite(packed: Int) {
        val x = PositionUtils.unpackX(packed)
        val y = PositionUtils.unpackY(packed)
        require(x in 0 until width && y in 0 until height) {
            "packed position out of board: x=$x, y=$y"
        }
    }
}
