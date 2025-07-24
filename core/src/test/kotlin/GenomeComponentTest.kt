/*
 * Юнит‑тесты для GenomeComponent
 *
 * Используется JUnit 5 + kotlin.test.*
 * Каждый тест выводит пояснение в лог, чтобы сразу видеть,
 * какая проверка выполняется и что именно сравнивается.
 */

@file:Suppress("RemoveRedundantBackticks")

import com.badlogic.gdx.graphics.Color
import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.component.COMMAND_EMPTY
import com.bargystvelp.world.tree.component.COMMAND_SIZE
import com.bargystvelp.world.tree.component.DIRECTIONS_SIZE
import com.bargystvelp.world.tree.component.EMPTY_COMMANDS
import com.bargystvelp.world.tree.component.GenomeComponent
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import kotlin.test.assertFailsWith

private const val W   = 8
private const val H   = 6
private const val MAX = 16

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("GenomeComponent — unit tests")
class GenomeComponentTest {

    private lateinit var g: GenomeComponent

    @BeforeEach
    fun `fresh component before each test`() {
        g = GenomeComponent(MAX, W, H)
    }

    /* ─────────── дефолты ─────────── */

    @Test
    fun `defaults are correct`() {
        println("▶ Проверяем, что по умолчанию в клетках COMMAND_EMPTY и BLACK, а у id — EMPTY_COMMANDS")
        for (y in 0 until H) for (x in 0 until W) {
            val p = PositionUtils.pack(x, y)
            assertEquals(COMMAND_EMPTY, g.get(GenomeComponent.SEED_COMMAND_AT_POS, p))
            assertEquals(Color.BLACK,   g.get(GenomeComponent.COLOR_AT_POS, p))
        }
        for (id in 0 until MAX) {
            assertSame(EMPTY_COMMANDS, g.get(GenomeComponent.COMMANDS, id))
        }
    }

    /* ─────────── seeds ─────────── */

    @Test
    fun `set-get seed at position`() {
        println("▶ Однократная установка / чтение seed‑команды")
        val p = PositionUtils.pack(3, 4)
        g.set(GenomeComponent.SEED_COMMAND_AT_POS, p, 7.toByte())
        assertEquals(7.toByte(), g.get(GenomeComponent.SEED_COMMAND_AT_POS, p))
    }

    @Test
    fun `seed overwrite`() {
        println("▶ Перезапись seed‑команды в той же клетке")
        val p = PositionUtils.pack(1, 2)
        g.set(GenomeComponent.SEED_COMMAND_AT_POS, p, 5.toByte())
        g.set(GenomeComponent.SEED_COMMAND_AT_POS, p, 9.toByte())
        assertEquals(9.toByte(), g.get(GenomeComponent.SEED_COMMAND_AT_POS, p))
    }

    /* ─────────── color ─────────── */

    @Test
    fun `set-get color at position`() {
        println("▶ Установка / чтение цвета клетки")
        val p = PositionUtils.pack(0, 0)
        val c = Color(0.2f, 0.6f, 0.9f, 1f)
        g.set(GenomeComponent.COLOR_AT_POS, p, c)
        assertEquals(c, g.get(GenomeComponent.COLOR_AT_POS, p))
    }

    /* ─────────── commands ─────────── */

    @Test
    fun `set-get commands table`() {
        println("▶ Присваиваем собственную таблицу команд id и убеждаемся, что ссылка та же")
        val id = MAX - 1           // крайний допустимый
        val tbl = Array(COMMAND_SIZE) { ByteArray(DIRECTIONS_SIZE) { it.toByte() } }
        g.set(GenomeComponent.COMMANDS, id, tbl)
        assertSame(tbl, g.get(GenomeComponent.COMMANDS, id))
    }

    @Test
    fun `commands mutability is kept`() {
        println("▶ Мутируем выданную таблицу команд — изменения должны отражаться внутри компонента")
        val id = 5
        val tbl = Array(COMMAND_SIZE) { ByteArray(DIRECTIONS_SIZE) }
        g.set(GenomeComponent.COMMANDS, id, tbl)
        tbl[0][0] = 42
        val stored = g.get(GenomeComponent.COMMANDS, id)
        assertEquals(42, stored[0][0])
    }

    /* ─────────── граничные позиции ─────────── */

    @Test
    fun `invalid position throws`() {
        println("▶ Попытка записать за пределы поля → IllegalArgumentException")
        val badPacked = PositionUtils.pack(W, 0)   // x == width  → за гранью
        assertFailsWith<IllegalArgumentException> {
            g.set(GenomeComponent.SEED_COMMAND_AT_POS, badPacked, 1.toByte())
        }
    }
}
