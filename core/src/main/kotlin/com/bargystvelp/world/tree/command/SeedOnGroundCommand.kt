package com.bargystvelp.world.tree.command

import com.bargystvelp.common.World
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.START_COMMAND

/** Команда приземления семени: переводит его из COMMAND_FALL в START_COMMAND (готово к росту). */
object SeedOnGroundCommand {
    /** Сбросить команду клетки [packed] с COMMAND_FALL на START_COMMAND. */
    fun execute(world: World, packed: Int) {
        val genomeComponent = world.components[GENOME_COMPONENT_KEY] ?: return

        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packed] = START_COMMAND
    }
}
