package com.bargystvelp

class EntityManager(private val capacity: Int) {
    private val freeIds  = IntArray(capacity) {
        capacity - 1 - it // оптимизация сдвига
    }
    private var freeTop = capacity
    private val alive = BooleanArray(capacity)

    /** Новый ID или ошибка, если мир заполнен. */
    fun create(): Int {
        check(freeTop > 0) { "World is full" }

        val id = freeIds[--freeTop]

        alive[id] = true

        return id
    }

    /** Освободить ID. */
    fun destroy(id: Int) {
        if (!alive[id]) return

        alive[id] = false
        freeIds[freeTop++] = id
    }

    /** Быстрый проход по живым сущностям. */
    fun forEachAlive(block: (Int) -> Unit) {
        for (id in 0 until capacity) if (alive[id]) block(id)
    }
}

