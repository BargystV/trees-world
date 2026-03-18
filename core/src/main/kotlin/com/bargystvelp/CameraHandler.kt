package com.bargystvelp

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.bargystvelp.common.Size

/**
 * Ортографическая камера с управлением через клавиатуру.
 * Синглтон: создаётся через [CameraHandler.init], доступен через [CameraHandler.instance].
 *
 * Управление:
 *  - WASD — перемещение
 *  - Q/E  — изменение масштаба
 */
class CameraHandler private constructor(
    val width: Float,
    val height: Float
) : OrthographicCamera(width, height) {

    companion object {

        private const val MOVE_SPEED      = 50f
        private const val ZOOM_SPEED      = 0.1f
        private const val MAX_ZOOM        = 1f
        private const val MIN_ZOOM        = 0.1f

        private var _instance: CameraHandler? = null
        val instance: CameraHandler
            get() = _instance ?: error("CameraHandler is not initialized. Call init(width, height) first.")

        /** Создать и сохранить единственный экземпляр камеры для заданного размера окна. */
        fun init(windowSize: Size) {
            _instance = CameraHandler(windowSize.width.toFloat(), windowSize.height.toFloat())
        }
    }

    init {
        // Установка камеры в центр области
        position.set(width / 2f, height / 2f, 0f)
        update()
    }

    /**
     * Обработать ввод с клавиатуры и обновить матрицу камеры.
     * Должна вызываться каждый кадр до отрисовки.
     */
    fun handle() {
        // Управление перемещением
        if (Gdx.input.isKeyPressed(Input.Keys.W)) translate(0f, MOVE_SPEED)
        if (Gdx.input.isKeyPressed(Input.Keys.S)) translate(0f, -MOVE_SPEED)
        if (Gdx.input.isKeyPressed(Input.Keys.A)) translate(-MOVE_SPEED, 0f)
        if (Gdx.input.isKeyPressed(Input.Keys.D)) translate(MOVE_SPEED, 0f)

        // Управление масштабом
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) zoom += ZOOM_SPEED
        if (Gdx.input.isKeyPressed(Input.Keys.E)) zoom -= ZOOM_SPEED

        // Ограничение масштаба
        zoom = zoom.coerceIn(MIN_ZOOM, MAX_ZOOM)

        clampPosition()

        update()
    }

    // Не даёт камере выйти за границы
    private fun clampPosition() {
        val halfWidth = viewportWidth * zoom / 2f
        val halfHeight = viewportHeight * zoom / 2f

        position.x = position.x.coerceIn(halfWidth, width - halfWidth)
        position.y = position.y.coerceIn(halfHeight, height - halfHeight)
    }
}
