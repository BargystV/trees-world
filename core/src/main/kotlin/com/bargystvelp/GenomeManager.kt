package com.bargystvelp

import com.bargystvelp.constant.Color
import com.bargystvelp.util.Logger
import kotlin.random.Random

// ==== COMMANDS ====
const val CMD_MOVE_UP: Byte = 0
const val CMD_MOVE_DOWN: Byte = 1
const val CMD_MOVE_LEFT: Byte = 2
const val CMD_MOVE_RIGHT: Byte = 3
const val CMD_WAIT: Byte = 4

val ALL_COMMANDS = byteArrayOf(CMD_MOVE_UP, CMD_MOVE_DOWN, CMD_MOVE_LEFT, CMD_MOVE_RIGHT, CMD_WAIT)

fun ByteArray.shuffledCopy(random: Random = Random): ByteArray {
    val copy = this.copyOf()
    for (i in copy.lastIndex downTo 1) {
        val j = random.nextInt(i + 1)
        val tmp = copy[i]
        copy[i] = copy[j]
        copy[j] = tmp
    }

    Logger.info(copy.joinToString(", "))

    return copy
}


class GenomeManager(capacity: Int) {
    private val commands = Array(capacity) {
        byteArrayOf()
    }
    private val colors = Array(capacity) {
        Color.PHOTOSYNTHESIS
    }

    fun set(id: Int, commands: ByteArray) {
        this.commands[id] = commands
    }

    fun getCommands(id: Int): ByteArray {
        return commands[id]
    }

    fun getColor(id: Int): com.badlogic.gdx.graphics.Color {
        return colors[id]
    }
}
