@file:Suppress("RemoveRedundantQualifierName")

package com.bargystvelp.biome.tree.component

import com.bargystvelp.world.tree.component.EMPTY_ID
import com.bargystvelp.world.tree.component.PositionComponent
import com.bargystvelp.world.tree.component.PositionComponent.Companion.pack
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/**
 * Полный, изолированный и читаемый набор тестов для `PositionComponent`.
 *
 * Каждый сценарий выводит доску «до» и «после» действия, так что по логу
 * сразу видно, что именно поменялось.
 *
 * Запускайте `gradle test` — в консоли будет понятная картина.
 */
@DisplayName("PositionComponent – exhaustive spec")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PositionComponentTest {

    /* ---------- конфигурация доски ---------- */

    private val W   = 6              // ширина
    private val H   = 5              // высота
    private val CAP = W * H          // максимум id
    private lateinit var pc: PositionComponent

    /* ---------- утилиты снимков и печати ---------- */

    /** Снимок текущей доски (плоский массив размера W*H). */
    private fun snapshot(): IntArray =
        IntArray(W * H) { pos -> pc[PositionComponent.POS_TO_ID, pos] }

    /** Печатает массив доски построчно. */
    private fun printBoard(board: IntArray) {
        val cellW = (CAP - 1).toString().length          // динамическая ширина
        for (y in H - 1 downTo 0) {
            val row = buildString {
                for (x in 0 until W) {
                    val id = board[y * W + x]
                    val cell = if (id == EMPTY_ID) "." else id.toString()
                    append(cell.padStart(cellW, ' '))
                    if (x < W - 1) append(' ')
                }
            }
            println(row)
        }
    }

    /** Обёртка: выводит «до»-/«после»-доски для любого действия. */
    private inline fun withLog(step: String, action: () -> Unit) {
        val before = snapshot()
        action()
        val after = snapshot()

        println("\n—— $step ——")
        println("before:")
        printBoard(before)
        println("after:")
        printBoard(after)
    }

    /** Лог одного снимка (когда изменений нет). */
    private fun logBoard(step: String) {
        println("\n—— $step ——")
        printBoard(snapshot())
    }

    /* ---------- вспомогательная функция ---------- */

    private fun coordsOf(id: Int): Pair<Int, Int>? =
        pc[PositionComponent.ID_TO_POS, id].let { p ->
            if (p == -1) null else PositionComponent.unpackX(p) to PositionComponent.unpackY(p)
        }

    /* ---------- инфраструктура ---------- */

    @BeforeEach
    fun setUp() {
        pc = PositionComponent(width = W, height = H)
    }

    /* ====================================================================== */
    /*  1. базовое состояние                                                  */
    /* ====================================================================== */

    @Test
    @DisplayName("Новая доска пуста (все клетки -> EMPTY_ID, все id вне поля)")
    fun boardStartsEmpty() {
        logBoard("fresh board")
        repeat(H) { y ->
            repeat(W) { x ->
                assertEquals(
                    EMPTY_ID,
                    pc[PositionComponent.POS_TO_ID, pack(x, y)],
                    "($x,$y) должна быть пуста"
                )
            }
        }
        repeat(CAP) { id ->
            assertEquals(-1, pc[PositionComponent.ID_TO_POS, id], "id=$id не должен быть на доске")
        }
    }

    /* ====================================================================== */
    /*  2. размещение id через ID_TO_POS                                      */
    /* ====================================================================== */

    @Test
    @DisplayName("ID_TO_POS корректно занимает клетку и формирует двустороннюю связь")
    fun placeViaIdToPos() {
        val id = 0
        val x = W / 2
        val y = 0
        withLog("place id=$id") {
            pc[PositionComponent.ID_TO_POS, id] = pack(x, y)
        }
        assertAll(
            { assertEquals(id, pc[PositionComponent.POS_TO_ID, pack(x, y)]) },
            { assertEquals(pack(x, y), pc[PositionComponent.ID_TO_POS, id]) }
        )
    }

    /* ====================================================================== */
    /*  3. перемещение id                                                     */
    /* ====================================================================== */

    @Test
    @DisplayName("Перемещение id зачищает старую клетку и занимает новую")
    fun moveIdCleansOldCell() {
        val id = 0

        pc[PositionComponent.ID_TO_POS, id] = pack(1, 1)

        withLog("move id=$id") {
            pc[PositionComponent.ID_TO_POS, id] = pack(2, 2)    // ход
        }
        assertEquals(EMPTY_ID, pc[PositionComponent.POS_TO_ID, pack(1, 1)])
        assertEquals(id, pc[PositionComponent.POS_TO_ID, pack(2, 2)])
    }

    /* ====================================================================== */
    /*  4. размещение через POS_TO_ID                                         */
    /* ====================================================================== */

    @Test
    @DisplayName("POS_TO_ID корректно присваивает id клетке")
    fun placeViaPosToId() {
        val id = 3
        withLog("place id=$id via POS_TO_ID") {
            pc[PositionComponent.POS_TO_ID, pack(W - 1, H - 1)] = id
        }
        assertEquals(id, pc[PositionComponent.POS_TO_ID, pack(W - 1, H - 1)])
        assertEquals(pack(W - 1, H - 1), pc[PositionComponent.ID_TO_POS, id])
    }

    /* ====================================================================== */
    /*  5. вытеснение прежнего жителя                                         */
    /* ====================================================================== */

    @Test
    @DisplayName("Новый id вытесняет прежний и обнуляет его координаты")
    fun overwriteCellEvictsVictim() {
        val victim   = 1
        val offender = 2
        val target   = pack(0, 0)

        pc[PositionComponent.POS_TO_ID, target] = victim

        withLog("overwrite victim=$victim by offender=$offender") {
            pc[PositionComponent.POS_TO_ID, target] = offender   // overwrite
        }
        assertEquals(offender, pc[PositionComponent.POS_TO_ID, target])
        assertEquals(-1, pc[PositionComponent.ID_TO_POS, victim], "victim должен быть выгнан")
    }

    /* ====================================================================== */
    /*  6. заполнение всей доски                                              */
    /* ====================================================================== */

    @Test
    @DisplayName("Можно полностью заполнить доску уникальными id")
    fun fillWholeBoard() {
        withLog("filled board") {
            var nextId = 0
            outer@ for (y in 0 until H) {
                for (x in 0 until W) {
                    if (nextId >= CAP) break@outer
                    // чередуем способы
                    if (nextId % 2 == 0)
                        pc[PositionComponent.ID_TO_POS, nextId] = pack(x, y)
                    else
                        pc[PositionComponent.POS_TO_ID, pack(x, y)] = nextId
                    ++nextId
                }
            }
        }

        // верификация
        val seen = HashSet<Pair<Int, Int>>()
        repeat(CAP) { id ->
            val coords = coordsOf(id)
            assertNotNull(coords, "id=$id должен иметь клетку")
            assertTrue(seen.add(coords!!), "дубликат клетки $coords")
            assertEquals(id, pc[PositionComponent.POS_TO_ID, pack(coords.first, coords.second)])
        }
    }

    /* ====================================================================== */
    /*  7. зеркальное перемещение                                             */
    /* ====================================================================== */

    @Test
    @DisplayName("Масс-зеркальное перемещение сохраняет уникальность маппинга")
    fun mirrorMoveKeepsConsistency() {
        /* 1. полностью заполняем доску */
        repeat(CAP) { id ->
            val x = id % W
            val y = id / W
            pc[PositionComponent.ID_TO_POS, id] = pack(x, y)
        }

        /* 2. рассчитываем целевые позиции для каждого id */
        val target = IntArray(CAP)
        repeat(CAP) { id ->
            val (x, y) = coordsOf(id)!!
            target[id] = pack(W - 1 - x, H - 1 - y)
        }

        /* 3. переносим всех на новые клетки */
        withLog("mirror-move all") {
            repeat(CAP) { id ->
                pc[PositionComponent.ID_TO_POS, id] = target[id]
            }
        }

        /* 4. верификация: каждая клетка занята единственным id */
        val seen = HashSet<Pair<Int, Int>>()
        repeat(CAP) { id ->
            val (x, y) = coordsOf(id)!!
            assertTrue(seen.add(x to y))
            assertEquals(id, pc[PositionComponent.POS_TO_ID, pack(x, y)])
        }
        assertEquals(CAP, seen.size)
    }

    /* ====================================================================== */
    /*  8. невалидные операции                                                */
    /* ====================================================================== */

    @Test
    @DisplayName("Невалидные id и координаты выбрасывают IllegalArgumentException")
    fun invalidOperationsThrow() {
        assertAll(
            { assertThrows(IllegalArgumentException::class.java) { pc[PositionComponent.ID_TO_POS, 0] = pack(-1, 0) } }, // x < 0
            { assertThrows(IllegalArgumentException::class.java) { pc[PositionComponent.ID_TO_POS, 0] = pack(0, H) } },  // y >= height
            { assertThrows(IllegalArgumentException::class.java) { pc[PositionComponent.ID_TO_POS, -1] = pack(0, 0) } }, // id < 0
            { assertThrows(IllegalArgumentException::class.java) { pc[PositionComponent.ID_TO_POS, CAP] = pack(0, 0) } } // id >= capacity
        )
    }

    /* ====================================================================== */
    /*  9. повторное присвоение той же клетки                                 */
    /* ====================================================================== */

    @Test
    @DisplayName("Повторное присвоение той же клетки не изменяет состояние")
    fun idempotentSet() {
        val id = 0
        val p  = pack(1, 1)
        pc[PositionComponent.ID_TO_POS, id] = p

        withLog("idempotent set id=$id") {
            pc[PositionComponent.ID_TO_POS, id] = p   // ещё раз
        }
        assertEquals(p,  pc[PositionComponent.ID_TO_POS, id])
        assertEquals(id, pc[PositionComponent.POS_TO_ID, p])
    }
}
