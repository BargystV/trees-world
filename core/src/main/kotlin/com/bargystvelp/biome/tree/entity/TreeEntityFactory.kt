package com.bargystvelp.biome.tree.entity

import com.bargystvelp.common.EntityFactory

private const val FREE = -1
private const val STATE_FREE: Byte = 0     // ячейка свободна (в пуле)
private const val STATE_USED: Byte = 1     // активный (живой) id

/**
 * Минимальная, однопроходная фабрика энтити.
 *
 * •create— O(1) взятие id из стека free и добавление в хвост списка
 * •destroy— O(1) удаление узла из двусвязного списка и возврат id в стек
 * •forEachExist — однократный обход *только* тех id, которые существовали
 *   в момент старта обхода; всё созданное внутри колбэка увидит следующий тик
 *
 * Структуры данных ― только примитивные массивы:
 * next/prev — двусвязный список живых id, free — стек свободных id,
 * state — метка «жив/свободен».
 */
class TreeEntityFactory(private val capacity: Int) : EntityFactory {

    /* ───── связи живых id ───── */
    private val next = IntArray(capacity) { FREE }
    private val prev = IntArray(capacity) { FREE }

    /* ───── состояние id ───── */
    private val state = ByteArray(capacity) { STATE_FREE }

    /* ───── пул свободных id (LIFO) ───── */
    private val free = IntArray(capacity) { (capacity - 1) - it }
    private var freeTop = capacity           // вершина стека free

    /* ───── границы списка живых ───── */
    private var head = FREE                  // первый живой
    private var tail = FREE                  // последний живой

    /* ──────────────────────────── create ──────────────────────────── */
    override fun create(): Int {
        check(freeTop > 0) { "World is full" }

        val id = free[--freeTop]

        // вставляем в хвост
        if (tail == FREE) {
            head = id
            tail = id
            prev[id] = FREE
        } else {
            next[tail] = id
            prev[id] = tail
            tail = id
        }
        next[id] = FREE
        state[id] = STATE_USED
        return id
    }

    /* ─────────────────────────── destroy ──────────────────────────── */
    override fun destroy(id: Int) {
        if (state[id] == STATE_FREE) return

        // unlink из списка
        val p = prev[id]
        val n = next[id]

        if (p != FREE) next[p] = n else head = n
        if (n != FREE) prev[n] = p else tail = p

        // помечаем и возвращаем в пул
        state[id] = STATE_FREE
        free[freeTop++] = id
    }

    /* ──────────────────────── forEachExist ────────────────────────── */
    override fun forEachExist(block: (Int) -> Unit) {
        // фиксируем «старый» хвост ― всё, что появится позже, игнорируется
        var id = tail
        while (id != FREE) {
            val prevId = prev[id]        // запоминаем ДО вызова колбэка
            block(id)

            // пропускаем узлы, уничтоженные внутри колбэка
            var cursor = prevId
            while (cursor != FREE && state[cursor] == STATE_FREE) {
                cursor = prev[cursor]    // безопасно: prev[] старых узлов не трогаем
            }
            id = cursor
        }
    }
}
