package com.bargystvelp.common

abstract class World(
    val windowSize: Size,
    val cellSize: Size,
    val biomeSize: Size
) {
    abstract val renderer: Renderer
    abstract val engines: List<Engine>
    abstract val entityFactory: EntityFactory
    abstract val components: Map<String, Component>

    abstract fun tick(delta: Float)
    abstract fun render(delta: Float)
}
