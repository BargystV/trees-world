# Simulation of Life — Документация проекта

## Обзор

LibGDX + Kotlin симуляция жизни деревьев с использованием **ECS (Entity Component System)** архитектуры. Деревья растут, фотосинтезируют, стареют, умирают и оставляют семена с унаследованным геномом.

**Стек:** Kotlin 2.1.0, LibGDX 1.13.1, JDK 17, Gradle (Kotlin DSL)

**Модули:**
- `core/` — вся логика симуляции
- `lwjgl3/` — десктопный лаунчер (LWJGL3 backend)
- `buildSrc/` — централизованные версии зависимостей (`Versions.kt`)

---

## Правила разработки

### Комментирование кода

- **Каждый новый класс, интерфейс, объект и enum** должны иметь KDoc-комментарий на русском языке (`/** ... */`) с описанием назначения.
- **Каждый новый метод** (публичный и приватный) должен иметь KDoc-комментарий, описывающий что делает метод, параметры и возвращаемое значение (если неочевидно).
- Стиль: `/** Описание на русском. */` — для однострочных, `/** \n * ... \n */` — для многострочных.
- Существующие неочевидные блоки логики внутри методов допускается дополнять inline-комментариями `//`.

---

## Архитектура ECS

### Принципы

- **Data-Oriented Design**: данные отделены от логики, хранятся в плоских массивах
- **Allocation-free hot paths**: никаких new-объектов в основном цикле
- **Stateless engines**: движки — синглтон-объекты без состояния
- **Command pattern**: мутации состояния через stateless command-объекты

### Ключевые абстракции

```
World → manages → [Engines, Components, EntityFactory, Renderer]
Engine.tick(world, delta) → reads/writes Components via Commands
Component → flat arrays indexed by entity ID or packed position
EntityFactory → linked list + free stack, O(1) create/destroy
```

---

## Структура файлов

```
core/src/main/kotlin/com/bargystvelp/
├── Main.kt                          # ApplicationAdapter, точка входа
├── CameraHandler.kt                 # Камера (WASD + Q/E зум)
├── common/
│   ├── AttrKey.kt                   # @JvmInline value class — ключ атрибута
│   ├── Component.kt                 # interface Component { get/set by AttrKey }
│   ├── Engine.kt                    # abstract class Engine { tick(world, delta) }
│   ├── EntityFactory.kt             # interface: create/destroy/forEachExist
│   ├── World.kt                     # abstract World(windowSize, cellSize, biomeSize)
│   ├── Size.kt                      # data class + div/times extension functions
│   ├── Height.kt                    # enum Height (HEIGHT_0..HEIGHT_9)
│   └── Color.kt                     # object с палитрой (PHOTOSYNTHESIS, BLACK, WHITE...)
├── util/
│   ├── PositionUtils.kt             # pack/unpackX/unpackY/idx
│   └── Randomizer.kt                # object Randomizer { init(seed); get: Random }
└── world/tree/
    ├── TreeWorld.kt                 # Основная реализация World
    ├── TreeRenderer.kt              # Pixmap → Texture рендер
    ├── component/
    │   ├── PositionComponent.kt     # Позиции сущностей (doubly-linked list per entity)
    │   ├── GenomeComponent.kt       # Геном + seed-команды + цвета
    │   ├── EnergyComponent.kt       # Энергия (IntArray)
    │   └── AgeComponent.kt          # Возраст (IntArray)
    ├── engine/
    │   ├── PhotosynthesisEngine.kt  # Энергия от высоты и тени
    │   ├── GrowEngine.kt            # Рост по командам генома
    │   ├── FallEngine.kt            # Гравитация для семян
    │   └── MortalEngine.kt          # Старение, расход энергии, смерть
    ├── entity/
    │   └── TreeEntityFactory.kt     # ID pool: linked list + free stack
    └── command/
        ├── CreateCommand.kt         # Создать сущность в точке
        ├── GrowCommand.kt           # Вырастить новую клетку
        ├── SeedToWoodCommand.kt     # Превратить семечко в древесину
        ├── FallCommand.kt           # Переместить семечко вниз
        ├── SeedOnGroundCommand.kt   # Семечко достигло земли → START_COMMAND
        ├── DestroySeedCommand.kt    # Семечко столкнулось → уничтожить
        ├── DieCommand.kt            # Смерть дерева, раздача семян
        ├── PhotosynthesisCommand.kt # Добавить энергию
        ├── EnergySpendCommand.kt    # Вычесть энергию
        └── AgeUpCommand.kt          # Увеличить возраст
```

---

## Компоненты (Component)

Каждый компонент — плоский массив(ы), индексируемый entity ID или packed-позицией.
Доступ через `component[AttrKey, key]`.

### PositionComponent

**Структура:**
- `IntArray posToId[width*height]` — packed pos → entity ID
- `IntArray nextPos, prevPos[width*height]` — двусвязный список позиций на сущность
- `IntArray idHead, idCount[maxEntities]` — голова списка и кол-во позиций

**Атрибуты:**
- `ID_TO_POS_LIST` — все позиции сущности (IntArray)
- `POS_TO_ID` — ID сущности в клетке (или EMPTY_ID = -1)

**Pack-формат:** `(y shl 16) or (x and 0xFFFF)` — 32-bit int

### GenomeComponent

**Константы:**
```
COMMAND_SIZE = 16        // строк в таблице команд
DIRECTIONS_SIZE = 4      // LEFT=0, UP=1, RIGHT=2, DOWN=3
START_COMMAND = 0        // начальная команда семени
COMMAND_WOOD = 28        // маркер клетки древесины
COMMAND_FALL = 29        // маркер падающего семени
COMMAND_EMPTY = 30       // пустая/неактивная клетка
```

**Структура:**
- `ByteArray seeds[width*height]` — команда-семени в каждой клетке
- `Array<Color> colors[width*height]` — цвет рендера в каждой клетке
- `Array<Array<ByteArray>> commands[maxEntities]` — таблица команд генома:
  - `commands[entityId][commandIndex][directionIndex]` → следующая команда

**Таблица команд:** 16 строк × 4 направления. Значение 0 = не расти в этом направлении.

### EnergyComponent

- `IntArray energies[maxEntities]`
- `DEFAULT_ENERGY = 300`, `ENERGY_TO_GROW = 18` (стоимость роста одной клетки)

### AgeComponent

- `IntArray ages[maxEntities]`
- `MAX_AGE = 100` — смерть от старости

---

## EntityFactory: TreeEntityFactory

**Паттерн:** два связных списка + стек свободных ID

**Состояния сущности:**
```
STATE_FREE (0)  — свободен (в стеке free)
STATE_CURR (1)  — активен в текущем тике
STATE_NEW  (2)  — создан в этом тике, станет CURR на следующем
```

**Двусвязные списки:**
- `head..tail` — основной список (STATE_CURR)
- `nbHead..nbTail` — список новорожденных (STATE_NEW)

**forEachExist():**
1. Слить nbList в основной список
2. Итерировать, захватив `next` ДО callback (безопасное удаление внутри итерации)

---

## Движки (Engine)

Порядок выполнения за тик: `Photosynthesis → Mortal → Fall → Grow`

### PhotosynthesisEngine

```
Для каждой сущности:
  Для каждой клетки-древесины:
    shade = кол-во занятых клеток выше (проверяет до 3 уровней вверх)
    energy += (y + 1) * (3 - shade)
  seeds НЕ фотосинтезируют, НО создают тень
```

### MortalEngine

```
Для каждой сущности:
  age++ → если >= MAX_AGE → DieCommand
  energy -= woodCount * WOOD_COST(4) → если <= 0 → DieCommand
```

### FallEngine

```
Для каждой клетки с COMMAND_FALL:
  если Y == 0 → SeedOnGroundCommand (→ START_COMMAND)
  иначе если клетка ниже занята → DestroySeedCommand
  иначе → FallCommand (сдвинуть вниз)
```

### GrowEngine

```
Для каждой клетки-семени (не WOOD, не FALL, не EMPTY):
  получить commands[entityId][seedCommand]  (4 direction bytes)
  если нет энергии → пропустить
  для каждого направления (4):
    если команда != 0 и клетка свободна:
      GrowCommand (добавить клетку с этой командой)
      отметить текущую клетку → будет превращена в WOOD
  SeedToWoodCommand для обработанных клеток
```

**Граница сетки:**
- X: торoidальное обёртывание (левый край → правый)
- Y: clamped к [0, height-1]

---

## Команды (Command)

Все команды — stateless объекты. Принимают `World` и параметры, мутируют компоненты напрямую.

| Команда | Действие |
|---------|----------|
| `CreateCommand` | Создать сущность: ID + позиция + геном + энергия + возраст |
| `GrowCommand` | Добавить клетку к сущности, списать ENERGY_TO_GROW |
| `SeedToWoodCommand` | SEED_COMMAND → COMMAND_WOOD, цвет → PHOTOSYNTHESIS (зелёный) |
| `FallCommand` | Сдвинуть позицию вниз, COMMAND_FALL, очистить старую клетку |
| `SeedOnGroundCommand` | COMMAND_FALL → START_COMMAND (готово к росту) |
| `DestroySeedCommand` | Уничтожить семя при столкновении, освободить ID |
| `DieCommand` | Древесина → пусто; семена → новые сущности с унаследованным геномом |
| `PhotosynthesisCommand` | `energy[id] += amount` |
| `EnergySpendCommand` | `energy[id] -= amount` → возвращает новое значение |
| `AgeUpCommand` | `age[id]++` → возвращает новое значение |

### DieCommand (подробно)

```
Для каждой клетки умирающей сущности:
  если COMMAND_WOOD → очистить (EMPTY_ID, COMMAND_EMPTY, BLACK)
  если seed → создать новую сущность:
    - унаследует таблицу команд родителя
    - если Y == 0 → START_COMMAND (готова расти)
    - если Y > 0 → COMMAND_FALL (падает)
Освободить родительский ID
```

---

## Рендеринг

### TreeRenderer

- `Pixmap` → `Texture` пайплайн через SpriteBatch
- Сетка (grid) — кэшированная текстура, рисуется поверх
- Каждый кадр: итерация по всем занятым клеткам → `draw(x, y, color)` из GenomeComponent

### CameraHandler (singleton)

- OrthographicCamera
- WASD: панорамирование (50 px/frame)
- Q/E: зум (±0.1, диапазон [0.1, 1.0])
- Ограничение по границам мира

---

## Главный цикл (Main.kt)

```
create():
  Randomizer.init(seed)   // можно задать конкретный seed для воспроизводимости
  world = TreeWorld(Size(screenWidth, screenHeight))

render(delta):
  world.render(delta)   // draw
  world.tick(delta)     // update
```

**TreeWorld.tick():**
1. `PhotosynthesisEngine.tick()`
2. `MortalEngine.tick()`
3. `FallEngine.tick()`
4. `GrowEngine.tick()`

**Инициализация:** один "Адам" в центре нижнего края с рандомной таблицей команд.

---

## Утилиты

### PositionUtils

```kotlin
pack(x, y)     = (y shl 16) or (x and 0xFFFF)
unpackX(p)     = p and 0xFFFF
unpackY(p)     = (p ushr 16) and 0xFFFF
idx(packed, w) = unpackY(packed) * w + unpackX(packed)
idx(x, y, w)   = y * w + x
```

### Randomizer

```kotlin
object Randomizer {
    fun init(seed: Long = System.currentTimeMillis())
    val get: Random
}
```

### Color (палитра)

```
PHOTOSYNTHESIS  (0.2, 0.8, 0.2) — ярко-зелёный (древесина)
SAPROPHYTE      (0.4, 0.3, 0.1) — коричневый
PREDATOR        (0.8, 0.1, 0.1) — красный
ORGANIC         (0.36, 0.26, 0.13) — тёмно-коричневый
BLACK           (0, 0, 0)
WHITE           (255, 255, 255)  — семена (до превращения в дерево)
```

---

## Производительность

| Операция | Сложность |
|----------|-----------|
| Создание сущности | O(1) (pop из стека) |
| Уничтожение сущности | O(1) (unlink + push) |
| Поиск по позиции | O(1) (прямой индекс в массив) |
| Итерация по сущностям | O(n) |
| Итерация по клеткам | O(cells per entity) |
| Фотосинтез (тень) | O(cells × 3) |
| Рост | O(cells × 4 directions) |

---

## Текущая ветка: test_ecs

Незакоммиченные изменения (на момент начала работы):
- `Main.kt` — изменения в инициализации/конфигурации
- `GrowCommand.kt` — логика роста
- `SeedToWoodCommand.kt` — логика превращения семечка в дерево

---

## Что добавлено из последних коммитов

1. **COMMAND_FALL** — маркер для падающих семян (не прижаты к земле, не растут)
2. **COMMAND_WOOD** — маркер для клеток древесины (отличает от семян)
3. **FallEngine** — гравитация: семена падают вниз, коллизии → смерть, земля → START_COMMAND
4. **DieCommand** — при смерти дерева семена становятся новыми сущностями с геномом родителя
5. Скорректирован подсчёт энергии при прорастании семян
