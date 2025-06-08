package com.bargystvelp

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.bargystvelp.logic.biome.common.Biome
import com.bargystvelp.logic.biome.ecosystem.Ecosystem
import com.bargystvelp.logic.cell.common.Size

class Main : ApplicationAdapter() {
    private lateinit var shapeRenderer: ShapeRenderer

    private lateinit var biome: Biome

    override fun create() {
        Logger.info("")

        // Инициализация биома с размерами экрана
        biome = Ecosystem(size = Size(width = Gdx.graphics.width, height = Gdx.graphics.height))

        // Создание рендерера для отрисовки клеток
        shapeRenderer = ShapeRenderer()

        // Инициализация камеры с размерами мира
        CameraHandler.init(width = biome.size.width.toFloat(), height = biome.size.height.toFloat())
    }

    override fun render() {
        // Обработка ввода и обновление камеры
        CameraHandler.instance.handle()

        // Установка матрицы проекции камеры для рендерера
        shapeRenderer.projectionMatrix = CameraHandler.instance.combined

        // Очистка экрана
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Отрисовка клеток биома
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)

        val cells = biome.render()
        for (x in 0 until biome.size.width) {
            for (y in 0 until biome.size.height) {
                val cell = cells[x][y]

                shapeRenderer.color = cell.entity.getColor(cell.position)
                shapeRenderer.rect(x.toFloat(), y.toFloat(), 1f, 1f)
            }
        }

        shapeRenderer.end()
    }

    // Очистка ресурсов
    override fun dispose() {
        shapeRenderer.dispose()

        Logger.info("")
    }
}
