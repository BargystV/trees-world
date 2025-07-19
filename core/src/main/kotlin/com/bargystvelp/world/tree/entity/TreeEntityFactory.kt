package com.bargystvelp.world.tree.entity

import com.bargystvelp.common.EntityFactory

private const val FREE = -1
private const val STATE_FREE: Byte = 0   // не используется
private const val STATE_CURR: Byte = 1   // в основном списке (обрабатывается сейчас)
private const val STATE_NEW:  Byte = 2   // создан в этом тике, будет первым в следующем

class TreeEntityFactory(private val capacity: Int) : EntityFactory {

    /* -------- внутренние массивы -------- */

    private val next = IntArray(capacity) { FREE }     // двусвязный список
    private val prev = IntArray(capacity) { FREE }

    private val state = ByteArray(capacity) { STATE_FREE }

    /* -------- стек свободных id -------- */

    private val free = IntArray(capacity) { (capacity - 1) - it }
    private var freeTop = capacity                     // вершина стека

    /* -------- текущий и «новорожденный» списки -------- */

    private var head = FREE                            // голова основного списка
    private var tail = FREE                            // хвост основного списка

    private var nbHead = FREE                          // голова списка «новорожденных»
    private var nbTail = FREE                          // хвост списка «новорожденных»

    /* =============================================== */

    override fun create(): Int {
        check(freeTop > 0) { "World is full" }

        val id = free[--freeTop]

        if (nbHead == FREE) {                 // первый в списке NewBorn
            nbHead = id
            nbTail = id
            prev[id] = FREE                  // ← остаётся FREE (голова)
        } else {                              // обычное добавление в хвост
            next[nbTail] = id
            prev[id] = nbTail                // ← ВАЖНО: сохраняем ссылку на хвост
            nbTail = id
        }

        next[id] = FREE
        state[id] = STATE_NEW

        return id
    }



    override fun destroy(id: Int) {
        when (state[id]) {
            STATE_FREE  -> return                                           // уже свободен
            STATE_CURR  -> unlink(id, false)                  // из основного списка
            STATE_NEW   -> unlink(id, true)                   // из списка «новорожденных»
        }

        state[id] = STATE_FREE
        next[id] = FREE
        prev[id] = FREE
        free[freeTop++] = id
    }

    // --- замените метод целиком в TreeEntityFactory ---
    override fun forEachExist(block: (Int) -> Unit) {
        /* 1. Переводим накопленных «новорожденных» в начало основного списка */
        if (nbHead != FREE) {
            if (tail != FREE) {                // основная очередь уже есть
                next[nbTail] = head            // сцепляем NB → head
                if (head != FREE) prev[head] = nbTail
                head = nbHead
            } else {                           // основной очереди ещё нет
                head = nbHead
                tail = nbTail
            }

            // помечаем их как текущих
            var cur = nbHead
            while (cur != FREE) {
                state[cur] = STATE_CURR
                cur = next[cur]
            }
            nbHead = FREE
            nbTail = FREE
        }

        /* 2. Снимок и обход */
        var id = head
        while (id != FREE) {
            val nxt = next[id]                 // берём next ДО колбэка
            block(id)
            id = nxt
        }
    }


    /* =============================================== */

    private fun unlink(id: Int, fromNewborn: Boolean) {
        val p = prev[id]
        val n = next[id]

        if (fromNewborn) {
            if (p != FREE) next[p] = n else nbHead = n
            if (n != FREE) prev[n] = p else nbTail = p
            if (nbHead == FREE) nbTail = FREE
        } else {
            if (p != FREE) next[p] = n else head = n
            if (n != FREE) prev[n] = p else tail = p
            if (head == FREE) tail = FREE
        }
    }
}
