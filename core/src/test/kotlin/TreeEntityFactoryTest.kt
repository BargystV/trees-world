@file:Suppress("RemoveRedundantQualifierName", "SpellCheckingInspection")

import com.bargystvelp.biome.tree.entity.TreeEntityFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

/**
 *  ⚡ TreeEntityFactory‑test (v2, обход «с хвоста»)
 *
 *  Проверяем все публичные сценарии:
 *  1.  Порядок посещения «отнового к старому»;
 *  2.  «Новорождённые» видны только в следующем тике и идут первыми;
 *  3.  Удаление головы / хвоста / середины сохраняет порядок;
 *  4.  Удаление прямо во время обхода не ломает снимок;
 *  5.  Повторно выделенный id идёт первым в следующем тике;
 *  6.  Переполнение пула бросает исключение.
 *
 *  Лог печатается так, чтобы одним взглядом увидеть порядок обхода
 *  и текущее содержимое очереди.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TreeEntityFactoryTest {

    /* ---------- настройки ---------- */

    private val CAP = 10
    private lateinit var f: TreeEntityFactory

    /* ---------- утилиты ---------- */

    private fun log(header: String, bodies: List<String> = emptyList()) {
        println("\n── $header ──")
        bodies.forEach(::println)
    }

    private fun snapshot(): List<Int> {
        val list = mutableListOf<Int>()
        f.forEachExist { list += it }
        return list
    }

    private fun snapshotLog(tag: String) =
        "$tag: ${snapshot().joinToString(prefix = "[", postfix = "]")}"

    /* ---------- lifecycle ---------- */

    @BeforeEach fun setup() { f = TreeEntityFactory(CAP) }

    /* ───────────────────── 1. порядок «с хвоста» ───────────────────── */

    @Test fun `creation order is reversed in iteration`() {
        val created = (0..4).map { f.create() }            // 4,3,2,1,0
        log("after create", listOf(snapshotLog("curr")))
        assertEquals(created.reversed(), snapshot(),
            "ожидался обход от нового к старому")
    }

    /* ───────────── 2. новорождённые → только в следующем тике ───────── */

    @Test fun `newborn appear next tick and first`() {
        repeat(3) { f.create() }                           // 0,1,2

        // первый тик
        val visited1 = mutableListOf<Int>()
        f.forEachExist { id ->
            visited1 += id
            if (id == 1) f.create()                       // рождаем 3
        }
        log("tick#1 (iterate)",
            listOf("→ ${visited1.joinToString()}",
                snapshotLog("после тика")))
        assertEquals(listOf(2,1,0), visited1,
            "новорождённый не должен попасть в текущий тик")

        // второй тик
        val visited2 = mutableListOf<Int>()
        f.forEachExist { visited2 += it }
        log("tick#2 (iterate)",
            listOf("→ ${visited2.joinToString()}",
                snapshotLog("после тика")))
        assertEquals(listOf(3,2,1,0), visited2,
            "новорождённый должен идти первым в следующем тике")
    }

    /* ───────────── 3. удаление головы, хвоста, середины ─────────────── */

    @Test fun `destroy keeps relative order`() {
        val ids = (0..5).map { f.create() }                // 0..5
        f.destroy(ids[0])                                  // head  (0)
        f.destroy(ids[3])                                  // middle(3)
        f.destroy(ids[5])                                  // tail  (5)

        val order = snapshot()
        log("after destroy head,mid,tail", listOf(snapshotLog("curr")))
        assertEquals(listOf(4,2,1), order,
            "порядок оставшихся ids нарушен")
    }

    /* ───────────── 4. удаление прямо во время обхода ────────────────── */

    @Test fun `destroy during iteration does not break snapshot`() {
        repeat(4) { f.create() }                           // 0..3

        val visited = mutableListOf<Int>()
        f.forEachExist { id ->
            visited += id
            if (id == 1) f.destroy(1)
            if (id == 2) f.destroy(2)
        }
        log("tick (destroy in‑loop)",
            listOf("→ ${visited.joinToString()}",
                snapshotLog("после тика")))
        assertEquals(listOf(3,2,1,0), visited,
            "снимок должен быть фиксирован")
        assertEquals(listOf(3,0), snapshot(),
            "остались неверные id")
    }

    /* ───────────── 5. повторное выделение id ────────────────────────── */

    @Test fun `reused id treated as newborn and is first next tick`() {
        val first  = f.create()                            // 0
        val second = f.create()                            // 1
        f.forEachExist { /* прогрев */ }

        f.destroy(first)                                   // освободили 0
        val new0 = f.create()                              // вернулся 0

        val order = mutableListOf<Int>()
        f.forEachExist { order += it }
        log("reused id", listOf("→ ${order.joinToString()}"))
        assertEquals(listOf(new0, second), order,
            "`reused` id должен идти первым")
    }

    /* ───────────── 6. переполнение пула ─────────────────────────────── */

    @Test fun `factory throws when capacity exceeded`() {
        repeat(CAP) { f.create() }
        val ex = assertThrows<IllegalStateException> { f.create() }
        log("capacity overflow", listOf(ex.message ?: "no msg"))
    }
}
