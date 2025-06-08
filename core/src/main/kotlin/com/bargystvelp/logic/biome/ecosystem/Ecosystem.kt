package com.bargystvelp.logic.biome.ecosystem

import com.bargystvelp.logic.biome.common.Biome
import com.bargystvelp.logic.cell.common.Cell
import com.bargystvelp.logic.cell.common.Position
import com.bargystvelp.logic.cell.common.Size
import com.bargystvelp.logic.cell.ecosystem.plant.Plant
import com.bargystvelp.logic.cell.ecosystem.Void

data class Ecosystem(override val size: Size) : Biome(size = size) {
    override fun create(): Array<Array<Cell>> {
        return Array(size.width) { x ->
            Array(size.height) { y ->
                val position = Position(x = x, y = y)
                val entity = Plant.trySpawn(position) ?: Void(mutableListOf(position))
                Cell(position = position, entity = entity)

//                createOnePlant(position)
            }
        }
    }

//    var hasPlant = false
//    private fun createOnePlant(position: Position): Cell {
//        val entity = if (hasPlant)
//            Void(mutableListOf(position))
//        else
//            Plant.trySpawn(position) ?: Void(mutableListOf(position))
//
//        if (entity is Plant) hasPlant = true
//
//        return Cell(position = position, entity = entity)
//    }
}
