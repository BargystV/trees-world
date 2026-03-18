package com.bargystvelp.common

/**
 * Базовый класс мира симуляции.
 * Хранит ссылки на все подсистемы ECS: рендерер, движки, фабрику сущностей и компоненты.
 *
 * @param windowSize размер окна в пикселях
 * @param cellSize   размер одной клетки в пикселях
 * @param biomeSize  размер биома в клетках (= windowSize / cellSize)
 */
abstract class World(
    val windowSize: Size,
    val cellSize: Size,
    val biomeSize: Size
) {
    abstract val renderer: Renderer
    abstract val engines: List<Engine>
    abstract val entityFactory: EntityFactory
    abstract val components: Map<String, Component>

    /** Выполнить один тик симуляции — запустить все движки. */
    abstract fun tick(delta: Float)
    /** Отрисовать текущее состояние мира на экране. */
    abstract fun render(delta: Float)
}
