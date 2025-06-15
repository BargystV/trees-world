package com.bargystvelp

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.bargystvelp.logic.common.Biome
import com.bargystvelp.logic.ecosystem.EcosystemBiome
import com.bargystvelp.logic.common.Size

class Main : ApplicationAdapter() {
    private lateinit var shapeRenderer: ShapeRenderer

    private lateinit var biome: Biome

    override fun create() {
        Logger.info("")

        // Инициализация биома с размерами экрана
        biome = EcosystemBiome(size = Size(width = Gdx.graphics.width, height = Gdx.graphics.height))

        // Создание рендерера для отрисовки клеток
        shapeRenderer = ShapeRenderer()

        // Инициализация камеры с размерами мира
        CameraHandler.init(width = biome.size.width.toFloat(), height = biome.size.height.toFloat())
    }

    private var renderCount = 0

    override fun render() {
        renderCount++

        // Обработка ввода и обновление камеры
        CameraHandler.instance.handle()
        shapeRenderer.projectionMatrix = CameraHandler.instance.combined

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // ⏱ Замер начала тика
        val tickStart = System.nanoTime()
        val organisms = biome.tick()
        val tickEnd = System.nanoTime()

        // ⏱ Замер начала рендера
        val renderStart = System.nanoTime()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        organisms.values.forEach { organism ->
            shapeRenderer.color = organism.color
            shapeRenderer.rect(organism.position.x.toFloat(), organism.position.y.toFloat(), 1f, 1f)
        }
        shapeRenderer.end()
        val renderEnd = System.nanoTime()

        val tickMs = (tickEnd - tickStart) / 1_000_000.0
        val renderMs = (renderEnd - renderStart) / 1_000_000.0
        val totalMs = (renderEnd - tickStart) / 1_000_000.0

        Logger.info(
            "$renderCount Organisms: ${organisms.size} Tick: %.3f ms, Render: %.3f ms, Total: %.3f ms"
                .format(tickMs, renderMs, totalMs)
        )
    }


    // Очистка ресурсов
    override fun dispose() {
        shapeRenderer.dispose()

        Logger.info("")
    }
}
