package com.bargystvelp

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.bargystvelp.world.tree.TreeWorld
import com.bargystvelp.common.World
import com.bargystvelp.common.Size
import com.bargystvelp.logger.Logger
import com.bargystvelp.logger.MeasureUtil
import com.bargystvelp.util.Randomizer

/**
 * Точка входа LibGDX-приложения.
 * Инициализирует мир симуляции и запускает основной цикл render/tick.
 */
class Main : ApplicationAdapter() {
    private lateinit var world: World

    private var renderCount: Int = 0

    /** Создать мир и инициализировать рандомайзер. Вызывается LibGDX один раз при старте. */
    override fun create() {
        Randomizer.init()

        world = TreeWorld(Size(width = Gdx.graphics.width, height = Gdx.graphics.height))
    }

    /** Основной цикл: рендер + тик симуляции каждый кадр. */
    override fun render() {
//        Logger.info("${renderCount++}")

//        MeasureUtil.time("Render") {
            world.render(Gdx.graphics.deltaTime)
//        }

        // ► Вычисляем ТОЛЬКО при «одном» нажатии пробела
//        if (!Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) return
//        MeasureUtil.time("Tick") {
            world.tick(Gdx.graphics.deltaTime)
//        }
    }

    /** Освободить все ресурсы при закрытии приложения. */
    override fun dispose() {
        Logger.info("")

        world.renderer.dispose()
    }
}
