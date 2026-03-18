package com.bargystvelp.common

/**
 * Фабрика сущностей ECS.
 * Управляет пулом ID через linked list + stack свободных слотов — O(1) для всех операций.
 */
interface EntityFactory {
    /** Создать новую сущность и вернуть её ID. Бросает ошибку, если мир заполнен. */
    fun create(): Int

    /** Освободить ID сущности и вернуть его в пул. */
    fun destroy(id: Int)

    /** Обойти все живые сущности. Безопасно к удалению внутри колбэка. */
    fun forEachExist(block: (Int) -> Unit)
}
