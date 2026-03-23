package com.bargystvelp.world.tree.command

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.common.Component
import com.bargystvelp.common.World
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.util.Randomizer
import com.bargystvelp.world.tree.AGE_COMPONENT_KEY
import com.bargystvelp.world.tree.ENERGY_COMPONENT_KEY
import com.bargystvelp.world.tree.GENOME_COMPONENT_KEY
import com.bargystvelp.world.tree.POSITION_COMPONENT_KEY
import com.bargystvelp.world.tree.component.AgeComponent
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.COMMAND_FALL
import com.bargystvelp.world.tree.component.COMMAND_SIZE
import com.bargystvelp.world.tree.component.COMMAND_WOOD
import com.bargystvelp.world.tree.component.DEFAULT_ENERGY
import com.bargystvelp.world.tree.component.DIRECTIONS_SIZE
import com.bargystvelp.world.tree.component.EMPTY_COMMANDS
import com.bargystvelp.world.tree.component.EMPTY_ID
import com.bargystvelp.world.tree.component.EnergyComponent
import com.bargystvelp.world.tree.component.GenomeComponent
import com.bargystvelp.world.tree.component.MIN_AGE
import com.bargystvelp.world.tree.component.PositionComponent
import com.bargystvelp.world.tree.component.START_COMMAND

/** Вероятность мутации одного байта в таблице команд генома (3%). */
private const val MUTATION_RATE = 0.03f

/** Максимальное отклонение каждого RGB-канала цвета при мутации (±3%). */
private const val COLOR_MUTATION_RANGE = 0.06f

/**
 * Команда смерти дерева.
 * Древесные клетки удаляются, семенные — становятся новыми сущностями с мутированным геномом родителя
 * только если [propagateSeeds] == true (смерть от старости). При смерти от нехватки энергии семена тоже удаляются.
 * ID родителя освобождается.
 */
object DieCommand {
    /**
     * Уничтожить сущность [id]: очистить древесину, при [propagateSeeds]=true — создать дочерние сущности из семян,
     * освободить ID родителя.
     * @param propagateSeeds true — семена становятся потомками (смерть от старости); false — все клетки удаляются.
     */
    fun execute(world: World, id: Int, propagateSeeds: Boolean = true) {
        val positionComponent = world.components[POSITION_COMPONENT_KEY]    ?: return
        val genomeComponent   = world.components[GENOME_COMPONENT_KEY]      ?: return
        val energyComponent   = world.components[ENERGY_COMPONENT_KEY]      ?: return
        val ageComponent      = world.components[AGE_COMPONENT_KEY]         ?: return

        // Снимок всех клеток дерева до модификаций
        val cells = positionComponent[PositionComponent.ID_TO_POS_LIST, id]
        val parentGenome = genomeComponent[GenomeComponent.COMMANDS, id]
        val parentColor  = genomeComponent[GenomeComponent.BASE_COLOR, id]

        cells.forEach { packedPos ->
            if (genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPos] == COMMAND_WOOD || !propagateSeeds) {
                clearWood(positionComponent, genomeComponent, packedPos)
            } else {
                createSeed(world, positionComponent, genomeComponent, energyComponent, ageComponent, packedPos, parentGenome, parentColor)
            }
        }

        /* ─── очищаем слоты родителя и высвобождаем его id ─── */
        genomeComponent[GenomeComponent.COMMANDS, id]     = EMPTY_COMMANDS
        energyComponent[EnergyComponent.ENERGY, id]       = DEFAULT_ENERGY
        ageComponent[AgeComponent.AGE, id]                = MIN_AGE

        world.entityFactory.destroy(id)
    }


    /**
     * Создать новую сущность из семени родителя в позиции [packedPos],
     * унаследовав мутированную таблицу команд [parentGenome] и мутированный цвет [parentColor].
     */
    private fun createSeed(
        world: World,
        positionComponent: Component,
        genomeComponent: Component,
        energyComponent: Component,
        ageComponent: Component,
        packedPos: Int,
        parentGenome: Array<ByteArray>,
        parentColor: Color
    ) {
        /* ───── семя остаётся и становится новым деревом ───── */
        val newId = world.entityFactory.create()

        // привязываем клетку к новому id
        positionComponent[PositionComponent.POS_TO_ID, packedPos] = newId

        // наследуем мутированный геном и цвет
        genomeComponent[GenomeComponent.COMMANDS, newId]          = mutateCommands(parentGenome)
        genomeComponent[GenomeComponent.BASE_COLOR, newId]        = mutateColor(parentColor)
        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPos] = getSeedCommand(packedPos)

        // стартовые параметры нового дерева
        energyComponent[EnergyComponent.ENERGY, newId] = DEFAULT_ENERGY
        ageComponent[AgeComponent.AGE, newId]          = MIN_AGE
    }

    /**
     * Создать мутированную копию таблицы команд генома.
     * Каждый байт с вероятностью [MUTATION_RATE] заменяется случайным допустимым значением.
     */
    private fun mutateCommands(original: Array<ByteArray>): Array<ByteArray> =
        Array(COMMAND_SIZE) { cmd ->
            ByteArray(DIRECTIONS_SIZE) { dir ->
                val gene = original[cmd][dir]
                if (Randomizer.get.nextFloat() < MUTATION_RATE) {
                    val newCmd = Randomizer.get.nextInt(COMMAND_EMPTY.toInt()).toByte()
                    if (newCmd >= COMMAND_SIZE) COMMAND_EMPTY else newCmd
                } else {
                    gene
                }
            }
        }

    /**
     * Создать мутированный цвет на основе [original].
     * Каждый RGB-канал смещается на случайную величину в диапазоне [-COLOR_MUTATION_RANGE/2, +COLOR_MUTATION_RANGE/2]
     * и зажимается в [0, 1].
     */
    private fun mutateColor(original: Color): Color {
        val r = (original.r + Randomizer.get.nextFloat() * COLOR_MUTATION_RANGE - COLOR_MUTATION_RANGE / 2).coerceIn(0f, 1f)
        val g = (original.g + Randomizer.get.nextFloat() * COLOR_MUTATION_RANGE - COLOR_MUTATION_RANGE / 2).coerceIn(0f, 1f)
        val b = (original.b + Randomizer.get.nextFloat() * COLOR_MUTATION_RANGE - COLOR_MUTATION_RANGE / 2).coerceIn(0f, 1f)
        return Color(r, g, b, 1f)
    }

    /** Очистить древесную клетку в позиции [packedPos]: убрать из позиций, сброс цвета. */
    private fun clearWood(positionComponent: Component, genomeComponent: Component, packedPos: Int) {
        /* ───── древесина: полностью очищаем ───── */
        positionComponent[PositionComponent.POS_TO_ID, packedPos]       = EMPTY_ID
        genomeComponent[GenomeComponent.SEED_COMMAND_AT_POS, packedPos] = COMMAND_EMPTY
        genomeComponent[GenomeComponent.COLOR_AT_POS, packedPos]        = Color.BLACK
    }


    /** Определить начальную команду семени: START_COMMAND если Y==0, иначе COMMAND_FALL. */
    private fun getSeedCommand(packedPos: Int): Byte {
        return if (PositionUtils.unpackY(packedPos) == 0) {
            START_COMMAND
        } else {
            COMMAND_FALL
        }
    }
}

