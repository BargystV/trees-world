package com.bargystvelp.common

/**
 * Базовый интерфейс компонента ECS.
 * Компонент хранит данные сущностей в плоских массивах;
 * доступ осуществляется через типобезопасный AttrKey.
 */
interface Component {
    /** Записать значение [value] для ключа [key] с типом [type]. */
    operator fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V)
    /** Прочитать значение для ключа [key] с типом [type]. */
    operator fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V
}

