package com.bargystvelp.world.tree

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.bargystvelp.CameraHandler
import com.bargystvelp.common.Renderer
import com.bargystvelp.common.Size
import com.bargystvelp.common.times
import com.bargystvelp.logger.Logger
import kotlin.math.min

/**
 * Рендерер симуляции деревьев на основе LibGDX SpriteBatch + Pixmap.
 * Использует две текстуры:
 *  - [cellsTexture] — однопиксельная белая текстура, масштабируется до размера клетки
 *  - [gridTexture]  — кэшированная текстура сетки, рисуется поверх клеток
 */
class TreeRenderer(
    windowSize: Size,
    biomeSize: Size,
    val cellSize: Size,
) : Renderer {

    companion object {
        private const val X_OFFSET_FACTOR = 1.001f
        private const val Y_OFFSET_FACTOR = 0.99f
        private const val FONT_SIZE_SCALE = 0.4f
    }

    private val spriteBatch = SpriteBatch()
//    private val font = createBitmapFont()

    private var gridTexture: Texture = createGridTexture(biomeSize, cellSize)
    private var cellsTexture: Texture = createCellsTexture()

    init {
        Logger.info("windowSize: $windowSize")
        Logger.info("biomeSize: $biomeSize")
        Logger.info("cellSize: $cellSize")

        CameraHandler.init(windowSize)
    }

    /** Подготовить кадр: обработать ввод камеры, очистить экран, начать SpriteBatch. */
    override fun begin() {
        CameraHandler.instance.handle()
        spriteBatch.projectionMatrix = CameraHandler.instance.combined

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        spriteBatch.begin()
    }

    /** Нарисовать одну клетку заданным цветом в клеточных координатах (x, y). */
    override fun draw(x: Int, y: Int, color: Color) {
        spriteBatch.color = color
        spriteBatch.draw(
            cellsTexture,
            x * cellSize.width.toFloat(),
            y * cellSize.height.toFloat(),
            cellSize.width.toFloat(),
            cellSize.height.toFloat()
        )

//        font.draw(
//            spriteBatch,
//            "x:$x \ny:$y",
//            x * (cellSize.width.toFloat() * X_OFFSET_FACTOR),
//            y * (cellSize.height.toFloat() * Y_OFFSET_FACTOR) + cellSize.height
//        )
    }

    /** Завершить кадр: поверх клеток нарисовать сетку, закончить SpriteBatch. */
    override fun end() {
        // Отрисовка сетки
        spriteBatch.color = Color.DARK_GRAY
        spriteBatch.draw(gridTexture, 0f, 0f)

        spriteBatch.end()
    }

    /** Освободить GPU-ресурсы: текстуры и SpriteBatch. */
    override fun dispose() {
        gridTexture.dispose()
        cellsTexture.dispose()
//        font.dispose()
        spriteBatch.dispose()
    }

    /** Создать текстуру сетки (линии через каждую клетку) для заданного размера биома. */
    private fun createGridTexture(biomeSize: Size, cellSize: Size): Texture {
        val windowSize = biomeSize.times(cellSize)

        val pixmap = Pixmap(windowSize.width, windowSize.height, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.DARK_GRAY)

        // Вертикальные линии
        for (x in 0..biomeSize.width) {
            val px = x * cellSize.width
            pixmap.drawLine(px, 0, px, windowSize.height)
        }

        // Горизонтальные линии
        for (y in 0..biomeSize.height) {
            val py = y * cellSize.height
            pixmap.drawLine(0, py, windowSize.width, py)
        }

        val texture = Texture(pixmap)

        pixmap.dispose()

        return texture
    }

    /** Создать однопиксельную белую текстуру для отрисовки клеток любого цвета. */
    private fun createCellsTexture(): Texture {
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color.WHITE)
        pixmap.fill()

        val texture = Texture(pixmap)

        pixmap.dispose()

        return texture
    }

    /** Создать растровый шрифт для отладочного вывода координат (работает только на крупных клетках). */
    private fun createBitmapFont(): BitmapFont {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("RobotoMono-Regular.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter().apply {
            size = (min(cellSize.width, cellSize.height) * FONT_SIZE_SCALE).toInt()
            color = Color.RED

            // 👇 Ключевые настройки для чёткости
            magFilter = Texture.TextureFilter.Nearest
            minFilter = Texture.TextureFilter.Nearest
            genMipMaps = false
            borderWidth = 0f
            kerning = false
            hinting = FreeTypeFontGenerator.Hinting.None

            characters = "0123456789:xy-," // если нужен только числовой текст
        }

        val font = generator.generateFont(parameter)
        generator.dispose()

        return font
    }
}
