@file:Suppress("RemoveRedundantQualifierName")

import com.bargystvelp.util.PositionUtils
import com.bargystvelp.world.tree.component.EMPTY_ID
import com.bargystvelp.world.tree.component.PositionComponent
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/** PositionComponent – exhaustive spec for multi‑pos logic */
@DisplayName("PositionComponent – exhaustive spec (multi‑pos)")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PositionComponentTest {

    /* ─────────── конфигурация ─────────── */

    private val W = 6; private val H = 5
    private val CAP = W * H
    private lateinit var pc: PositionComponent

    /* ─────────── helpers ─────────── */
    /* snapshot каждой клетки, но ключ всегда pack(x,y) */
    private fun snapshot() = IntArray(W * H) { idx ->
        val x = idx % W
        val y = idx / W
        pc[PositionComponent.POS_TO_ID, PositionUtils.pack(x, y)]
    }

    /* печать — то же вычисление pack  */
    private fun printBoard(b: IntArray) {
        val w = (CAP - 1).toString().length
        for (y in H - 1 downTo 0) {
            println((0 until W).joinToString(" ") { x ->
                b[y * W + x].let { if (it == EMPTY_ID) "." else it.toString() }.padStart(w, ' ')
            })
        }
    }


    private inline fun withLog(step: String, act: () -> Unit) {
        val before = snapshot(); act(); val after = snapshot()
        println("\n—— $step ——\nbefore:"); printBoard(before)
        println("after:"); printBoard(after)
    }

    private fun coordsOf(id: Int): Pair<Int, Int>? =
        pc[PositionComponent.ID_TO_POS_LIST, id].firstOrNull()?.let {
            PositionUtils.unpackX(it) to PositionUtils.unpackY(it)
        }

    /* ─────────── lifecycle ─────────── */

    @BeforeEach fun setUp() { pc = PositionComponent(CAP, W, H) }

    /* ====================================================================== */
    /* 1. новая доска пуста                                                   */
    /* ====================================================================== */

    @Test
    fun boardStartsEmpty() {
        repeat(H) { y ->
            repeat(W) { x ->
                assertEquals(
                    EMPTY_ID,
                    pc[PositionComponent.POS_TO_ID, PositionUtils.pack(x, y)]
                )
            }
        }
        repeat(CAP) { id ->
            assertTrue(pc[PositionComponent.ID_TO_POS_LIST, id].isEmpty())
        }
    }

    /* ====================================================================== */
    /* 2. базовое размещение                                                  */
    /* ====================================================================== */

    @Test
    fun placeViaPosToId() {
        val id = 0; val p = PositionUtils.pack(W / 2, 0)
        withLog("place id=$id") { pc[PositionComponent.POS_TO_ID, p] = id }
        assertEquals(id, pc[PositionComponent.POS_TO_ID, p])
        assertArrayEquals(intArrayOf(p), pc[PositionComponent.ID_TO_POS_LIST, id])
    }

    /* ====================================================================== */
    /* 3. добавление второй позиции                                           */
    /* ====================================================================== */

    @Test
    @DisplayName("Добавление второй позиции сохраняет первую клетку")
    fun addSecondPosKeepsFirst() {
        val id = 0
        val p1 = PositionUtils.pack(1, 1)
        val p2 = PositionUtils.pack(2, 2)
        pc[PositionComponent.POS_TO_ID, p1] = id

        withLog("add second pos id=$id") {
            pc[PositionComponent.POS_TO_ID, p2] = id
        }
        assertEquals(id, pc[PositionComponent.POS_TO_ID, p1])
        assertEquals(id, pc[PositionComponent.POS_TO_ID, p2])
        assertArrayEquals(intArrayOf(p1, p2).sortedArray(),
            pc[PositionComponent.ID_TO_POS_LIST, id].sortedArray())
    }

    /* ====================================================================== */
    /* 4. вытеснение прежнего жителя                                          */
    /* ====================================================================== */

    @Test
    fun overwriteCellEvictsVictim() {
        val victim = 1; val offender = 2; val target = PositionUtils.pack(0, 0)
        pc[PositionComponent.POS_TO_ID, target] = victim

        withLog("overwrite victim=$victim by offender=$offender") {
            pc[PositionComponent.POS_TO_ID, target] = offender
        }
        assertEquals(offender, pc[PositionComponent.POS_TO_ID, target])
        assertTrue(pc[PositionComponent.ID_TO_POS_LIST, victim].isEmpty())
    }

    /* ====================================================================== */
    /* 5. заполнение всей доски                                               */
    /* ====================================================================== */

    @Test
    fun fillWholeBoard() {
        var nextId = 0
        (0 until H).forEach { y ->
            (0 until W).forEach { x ->
                if (nextId < CAP) pc[PositionComponent.POS_TO_ID, PositionUtils.pack(x, y)] = nextId++
            }
        }
        repeat(CAP) { id ->
            val coords = coordsOf(id)!!
            assertEquals(id, pc[PositionComponent.POS_TO_ID, PositionUtils.pack(coords.first, coords.second)])
            assertEquals(1, pc[PositionComponent.ID_TO_POS_LIST, id].size)
        }
    }

    /* ====================================================================== */
    /* 6. зеркальное перемещение                                              */
    /* ====================================================================== */

    @Test
    fun mirrorMoveKeepsConsistency() {
        /* заполнить */
        repeat(CAP) { id -> pc[PositionComponent.POS_TO_ID,
            PositionUtils.pack(id % W, id / W)] = id }

        /* целевые позиции */
        val tgt = IntArray(CAP) { id ->
            val (x, y) = coordsOf(id)!!
            PositionUtils.pack(W - 1 - x, H - 1 - y)
        }

        /* перенос */
        withLog("mirror‑move all") {
            repeat(CAP) { id -> pc[PositionComponent.POS_TO_ID, tgt[id]] = id }
        }

        /* верификация */
        val seen = HashSet<Pair<Int, Int>>()
        repeat(CAP) { id ->
            val (x, y) = coordsOf(id)!!
            assertTrue(seen.add(x to y))
        }
        assertEquals(CAP, seen.size)
    }

    /* ====================================================================== */
    /* 7. замена списка позиций                                               */
    /* ====================================================================== */

    @Test
    fun replacePositionsViaIdToPosList() {
        val id = 0
        val first = intArrayOf(PositionUtils.pack(0, 0), PositionUtils.pack(1, 0))
        pc[PositionComponent.ID_TO_POS_LIST, id] = first
        assertArrayEquals(first.sortedArray(), pc[PositionComponent.ID_TO_POS_LIST, id].sortedArray())

        val second = intArrayOf(PositionUtils.pack(2, 2))
        withLog("replace positions id=$id") {
            pc[PositionComponent.ID_TO_POS_LIST, id] = second
        }
        assertArrayEquals(second, pc[PositionComponent.ID_TO_POS_LIST, id])
        /* убедиться, что старые клетки освободились */
        first.forEach { p -> assertEquals(EMPTY_ID, pc[PositionComponent.POS_TO_ID, p]) }
    }

    /* ====================================================================== */
    /* 8. невалидные операции                                                 */
    /* ====================================================================== */

    @Test
    fun invalidOperationsThrow() {
        assertAll(
            /* 1. координата –1 */
            { assertThrows(IllegalArgumentException::class.java) {
                pc[PositionComponent.POS_TO_ID, PositionUtils.pack(-1, 0)] = 0
            } },
            /* 2. координата y == H */
            { assertThrows(IllegalArgumentException::class.java) {
                pc[PositionComponent.POS_TO_ID, PositionUtils.pack(0, H)]  = 0
            } },
            /* 3. id >= maxEntities */
            { assertThrows(IllegalArgumentException::class.java) {
                pc[PositionComponent.POS_TO_ID, PositionUtils.pack(0, 0)]  = CAP
            } }
            /* передача EMPTY_ID (-1) — валидное удаление ⇒ исключения нет */
        )
    }


    /* ====================================================================== */
    /* 9. идемпотентность                                                     */
    /* ====================================================================== */

    @Test
    fun idempotentSet() {
        val id = 0; val p = PositionUtils.pack(1, 1)
        pc[PositionComponent.POS_TO_ID, p] = id

        withLog("idempotent set id=$id") {
            pc[PositionComponent.POS_TO_ID, p] = id
        }
        assertEquals(id, pc[PositionComponent.POS_TO_ID, p])
        assertArrayEquals(intArrayOf(p), pc[PositionComponent.ID_TO_POS_LIST, id])
    }

    /* ====================================================================== */
    /* 10. удаление клетки                                                    */
    /* ====================================================================== */

    @Test
    fun removePositionViaPosToId() {
        val id = 0
        val p  = PositionUtils.pack(2, 2)

        pc[PositionComponent.POS_TO_ID, p] = id     // разместили
        withLog("remove pos id=$id") {
            pc[PositionComponent.POS_TO_ID, p] = EMPTY_ID   // очистили
        }

        assertEquals(EMPTY_ID, pc[PositionComponent.POS_TO_ID, p])
        assertTrue(pc[PositionComponent.ID_TO_POS_LIST, id].isEmpty())
    }
}
