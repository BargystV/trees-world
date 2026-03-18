package com.bargystvelp.util

import com.bargystvelp.logger.Logger
import java.util.Random

/**
 * Синглтон-обёртка над [java.util.Random].
 * Инициализируется один раз при старте симуляции; один и тот же seed даёт воспроизводимый результат.
 */
object Randomizer {
    private var random = Random()

    /** Инициализировать генератор случайных чисел. По умолчанию seed = текущее время. */
    fun init(seed: Long = System.currentTimeMillis()) {
        Logger.info("seed: $seed")

        this.random = Random(seed)
    }

    /** Получить текущий экземпляр генератора. */
    val get: Random
        get() = random
}
