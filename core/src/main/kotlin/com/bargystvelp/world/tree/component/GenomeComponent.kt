package com.bargystvelp.world.tree.component

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.common.AttrKey
import com.bargystvelp.common.Component


// ==== DIRECTIONS ====
const val DIRECTIONS_SIZE: Int      = 4

const val LEFT: Int                 = 0
const val UP: Int                   = 1
const val RIGHT: Int                = 2
const val DOWN: Int                 = 3

val EMPTY_DIRECTIONS: ByteArray = ByteArray(DIRECTIONS_SIZE) { COMMAND_EMPTY }

// ==== COMMANDS ====
const val COMMAND_SIZE: Int         = 64
const val START_COMMAND: Byte       = 0

const val COMMAND_EMPTY: Byte       = 100

val EMPTY_COMMANDS: Array<ByteArray> = Array(COMMAND_SIZE) { EMPTY_DIRECTIONS }


class GenomeComponent(capacity: Int): Component {
    companion object {
        val COMMAND_NUMBER          = AttrKey<Int, Byte>(0)
        val COMMANDS                = AttrKey<Int, Array<ByteArray>>(1)
        val COLOR                   = AttrKey<Int, Color>(2)
    }

    private val seeds: ByteArray = ByteArray(capacity) { START_COMMAND }
    private val commands: Array<Array<ByteArray>> = Array(capacity) { EMPTY_COMMANDS }
    private val colors = Array(capacity) { com.bargystvelp.common.Color.BLACK }


    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> set(type: AttrKey<K, V>, key: K, value: V) {
        when (type) {
            COMMAND_NUMBER      -> seeds[key as Int] = value    as Byte
            COMMANDS            -> commands[key as Int] = value as Array<ByteArray>
            COLOR               -> colors[key as Int] = value   as Color

            else                -> error("bad key")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <K, V : Any> get(type: AttrKey<K, V>, key: K): V =
        when (type) {
            COMMAND_NUMBER      -> seeds[key as Int]        as V
            COMMANDS            -> commands[key as Int]     as V
            COLOR               -> colors[key as Int]       as V

            else                -> error("bad key")
        }
}
