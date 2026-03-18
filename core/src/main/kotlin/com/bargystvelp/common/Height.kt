package com.bargystvelp.common

/**
 * Перечисление уровней высоты в биоме (0–9).
 * Используется для расчёта коэффициента фотосинтеза.
 */
enum class Height(val value: Int) {
    HEIGHT_0(0),
    HEIGHT_1(1),
    HEIGHT_2(2),
    HEIGHT_3(3),
    HEIGHT_4(4),
    HEIGHT_5(5),
    HEIGHT_6(6),
    HEIGHT_7(7),
    HEIGHT_8(8),
    HEIGHT_9(9);

    companion object {
        /** Минимальная высота (HEIGHT_0). */
        fun minHeight() = HEIGHT_0
        /** Высота по умолчанию (HEIGHT_5). */
        fun defaultHeight() = HEIGHT_5
        /** Максимальная высота (HEIGHT_9). */
        fun maxHeight() = HEIGHT_9

        /** Найти Height по числовому значению; возвращает HEIGHT_5, если не найдено. */
        fun fromInt(value: Int): Height {
            return entries.firstOrNull { it.value == value } ?: HEIGHT_5 // Значение по умолчанию, если не найдено
        }
    }
}
