package com.bargystvelp.common

/**
 * Базовый класс игрового движка ECS.
 * Каждый движок — stateless-синглтон, реализующий один аспект симуляции.
 * Движки выполняются в строгом порядке за каждый тик мира.
 */
abstract class Engine {
    /** Выполнить один тик логики движка. [delta] — время кадра в секундах. */
    abstract fun tick(world: World, delta: Float)
}
