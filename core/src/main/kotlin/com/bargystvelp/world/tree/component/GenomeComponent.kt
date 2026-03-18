package com.bargystvelp.world.tree.component

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.common.AttrKey
import com.bargystvelp.common.Component
import com.bargystvelp.util.PositionUtils

/* ─────────────── константы ─────────────── */
const val DIRECTIONS_SIZE = 4
const val COMMAND_SIZE    = 16
const val START_COMMAND: Byte = 0

const val COMMAND_WOOD: Byte = 28
const val COMMAND_FALL: Byte = 29
const val COMMAND_EMPTY: Byte = 30

const val LEFT: Int = 0
const val UP: Int = 1
const val RIGHT: Int = 2
const val DOWN: Int = 3

val EMPTY_DIRECTIONS = ByteArray(DIRECTIONS_SIZE) { COMMAND_EMPTY }
val EMPTY_COMMANDS   = Array(COMMAND_SIZE) { EMPTY_DIRECTIONS }

/* ─────────────── компонент ─────────────── */
/**
 * Компонент генома сущностей.
 * Хранит:
 *  - [seeds]    — команда-тип каждой клетки на сетке (по packed-позиции)
 *  - [colors]   — цвет каждой клетки на сетке (по packed-позиции)
 *  - [commands] — таблица команд генома для каждой сущности (по entity ID):
 *                 commands[id][commandIndex][directionIndex] → следующая команда
 */
class GenomeComponent(
    private val maxEntities: Int,
    private val width: Int,
    private val height: Int,
) : Component {

    companion object {
        /** packedPos → seed‑команда */
        val SEED_COMMAND_AT_POS = AttrKey<Int, Byte>(0)
        /** id → таблица команд (как прежде) */
        val COMMANDS            = AttrKey<Int, Array<ByteArray>>(1)
        /** packedPos → цвет клетки (Color) */
        val COLOR_AT_POS        = AttrKey<Int, Color>(2)
        /** id → базовый цвет сущности (генетический, наследуется с мутацией) */
        val BASE_COLOR          = AttrKey<Int, Color>(3)
    }

    /* ─────────── внутреннее хранение ─────────── */
    private val seeds        = ByteArray(width * height) { COMMAND_EMPTY }
    private val colors       = Array(width * height) { Color.BLACK }
    private val commands     = Array(maxEntities) { EMPTY_COMMANDS }
    /** Генетический цвет каждой сущности — наследуется потомками с мутацией. */
    private val entityColors = Array(maxEntities) { Color(0.2f, 0.8f, 0.2f, 1f) }


    /* ───────────── Component API ───────────── */
    /** Записать значение атрибута. Поддерживает [SEED_COMMAND_AT_POS], [COMMANDS], [COLOR_AT_POS], [BASE_COLOR]. */
    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V) {
        when (type) {
            SEED_COMMAND_AT_POS -> seeds[PositionUtils.idx(key as Int, width)] = value as Byte
            COMMANDS            -> commands[key as Int]   = value as Array<ByteArray>
            COLOR_AT_POS        -> colors[PositionUtils.idx(key as Int, width)] = value as Color
            BASE_COLOR          -> entityColors[key as Int] = value as Color
            else                -> error("bad AttrKey for GenomeComponent")
        }
    }

    /** Прочитать значение атрибута. Поддерживает [SEED_COMMAND_AT_POS], [COMMANDS], [COLOR_AT_POS], [BASE_COLOR]. */
    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V =
        when (type) {
            SEED_COMMAND_AT_POS -> seeds[PositionUtils.idx(key as Int, width)]  as V
            COMMANDS            -> commands[key as Int]                          as V
            COLOR_AT_POS        -> colors[PositionUtils.idx(key as Int, width)] as V
            BASE_COLOR          -> entityColors[key as Int]                      as V
            else                -> error("bad AttrKey for GenomeComponent")
        }
}
