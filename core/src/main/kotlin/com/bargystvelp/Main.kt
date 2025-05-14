package com.bargystvelp

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.bargystvelp.logic.biome.Biome
import com.bargystvelp.logic.biome.Forest

class Main : ApplicationAdapter() {
    private lateinit var shapeRenderer: ShapeRenderer

    private lateinit var biome: Biome

    override fun create() {
        // Инициализация биома с размерами экрана
        biome = Forest(width = Gdx.graphics.width, height = Gdx.graphics.height)
//        biome = Forest(width = 100, height = 100)

        // Создание рендерера для отрисовки клеток
        shapeRenderer = ShapeRenderer()

        // Инициализация камеры с размерами мира
        CameraHandler.init(width = biome.width.toFloat(), height = biome.height.toFloat())
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

        biome.render { cell ->
            shapeRenderer.color = cell.getColor()
            shapeRenderer.rect(cell.x.toFloat(), cell.y.toFloat(), 1f, 1f)
        }

        shapeRenderer.end()
    }

    // Очистка ресурсов
    override fun dispose() {
        shapeRenderer.dispose()
    }
}
