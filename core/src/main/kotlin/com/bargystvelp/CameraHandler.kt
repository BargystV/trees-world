package com.bargystvelp

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera

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

        fun init(width: Float, height: Float) {
            _instance = CameraHandler(width, height)
        }
    }

    init {
        // Установка камеры в центр области
        position.set(width / 2f, height / 2f, 0f)
        update()
    }


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
