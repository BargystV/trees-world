package com.bargystvelp.common

/**
 * Типобезопасный ключ атрибута для доступа к данным компонента.
 *
 * [K] — тип ключа (обычно Int — entity ID или packed-позиция).
 * [V] — тип значения, хранимого по этому ключу.
 *
 * @JvmInline гарантирует, что в байткоде это просто Int — без лишних аллокаций на горячем пути.
 */
@JvmInline
value class AttrKey<K, V : Any>(val id: Int)        // инлайн ⇒ 1 Int в байткоде
