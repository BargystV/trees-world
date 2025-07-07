package com.bargystvelp

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.bargystvelp.component.Genome
import com.bargystvelp.component.Position
import com.bargystvelp.util.Logger

class Main : ApplicationAdapter() {
    private lateinit var shapeRenderer: ShapeRenderer

    private lateinit var world: World

    private var renderCount: Int = 0

    override fun create() {
        world = World(Size(width = Gdx.graphics.width, height = Gdx.graphics.height))
        Logger.info(world.size.toString())

        // Создание рендерера для отрисовки клеток
        shapeRenderer = ShapeRenderer()

        // Инициализация камеры с размерами мира
        CameraHandler.init(width = world.size.width.toFloat(), height = world.size.height.toFloat())
    }

    override fun render() {
        renderCount++

        // Обработка ввода и обновление камеры
        CameraHandler.instance.handle()
        shapeRenderer.projectionMatrix = CameraHandler.instance.combined

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // ⏱ Замер начала тика
        val tickStart = System.nanoTime()
        world.tick(delta = Gdx.graphics.deltaTime)
        val tickEnd = System.nanoTime()

        // ⏱ Замер начала рендера
        val renderStart = System.nanoTime()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        var entityCounter = 0
        world.entityManager.forEachAlive { id ->
            entityCounter++
            shapeRenderer.color = world.genomeManager.getColor(id)

            val x = world.positionManager.getX(id)
            val y = world.positionManager.getY(id)

            shapeRenderer.rect(x.toFloat(), y.toFloat(), 1f, 1f)
        }

        shapeRenderer.end()
        val renderEnd = System.nanoTime()

        val tickMs = (tickEnd - tickStart) / 1_000_000.0
        val renderMs = (renderEnd - renderStart) / 1_000_000.0
        val totalMs = (renderEnd - tickStart) / 1_000_000.0

        Logger.info(
            "$renderCount Organisms: $entityCounter Tick: %.3f ms, Render: %.3f ms, Total: %.3f ms"
                .format(tickMs, renderMs, totalMs)
        )
    }


    // Очистка ресурсов
    override fun dispose() {
        shapeRenderer.dispose()

        Logger.info("")
    }
}
