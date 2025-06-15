package com.bargystvelp.constant

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
        fun minHeight() = HEIGHT_0
        fun defaultHeight() = HEIGHT_5
        fun maxHeight() = HEIGHT_9

        fun fromInt(value: Int): Height {
            return entries.firstOrNull { it.value == value } ?: HEIGHT_5 // Значение по умолчанию, если не найдено
        }
    }
}
