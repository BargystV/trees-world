package com.bargystvelp.world.tree.engine

import com.bargystvelp.common.Engine
import com.bargystvelp.common.World
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.command.PhotosynthesisCommand
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.EMPTY_ID
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.PositionComponent

object PhotosynthesisEngine : Engine() {

    /** 0 → ×3, 1 → ×2, 2 → ×1, ≥3 → ×0 */
    private const val MAX_SHADOW = 3

    override fun tick(world: World, delta: Float) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY]    ?: return
        val genomeComponent   = world.components[GENOME_COMPONENT_KEY]      ?: return

        world.entityFactory.forEachExist { id ->
            var energyGain = 0

            /* ——— все клетки данного организма ——— */
            val cells = positionComponent[PositionComponent.ID_TO_POS_LIST, id]
            cells.forEach { packedPos ->
                /* 1. Семечко само энергию не копит, но даёт тень */
                if (genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPos] != COMMAND_EMPTY) return@forEach

                /* 2. Проверяем тень над клеткой */
                var cover = 0
                val x = PositionUtils.unpackX(packedPos)
                val y = PositionUtils.unpackY(packedPos)

                for (aboveY in (y + 1) until world.biomeSize.height) {
                    val packedAbove = PositionUtils.pack(x, aboveY)

                    if (positionComponent[PositionComponent.POS_TO_ID, packedAbove] != EMPTY_ID) {
                        if (++cover >= MAX_SHADOW) return@forEach
                    }
                }

                /* 3. Высота (0‑based -> 1‑based коэффициент) */
                val heightFactor = y + 1

                /* 4. Итоговая прибавка энергии: height * multiplier */
                val multiplier = MAX_SHADOW - cover          // 3,2,1 или 0
                energyGain += heightFactor * multiplier
            }

            /* 5. Сохраняем результат */
            if (energyGain != 0) {
                PhotosynthesisCommand.execute(world, id, energyGain)
            }
        }
    }
}


