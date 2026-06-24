# Countries Explorer

Android-приложение на открытом API **REST Countries**. Текущая сдача — **ДЗ №5** (автотесты поверх проекта ДЗ №4). База: открытый API + список/детали + ViewModel/Repository/coroutines + **Hilt** + **Room**.

## ФИО и группа

- **ФИО:** Беседа Семен Денисович
- **Группа:** Б9123-09.03.03пикд(1)


## Стек и база проекта (ДЗ №4 остаётся рабочим)

- Compose + Material3, Navigation Compose, 2 экрана: `list` (List/Search) и `detail/{code}` + экран Favourites.
- Состояние через `mutableStateOf` в ViewModel, корутины через `viewModelScope`.
- Retrofit (suspend) + Hilt (DI) + Room (таблица `favourite_countries`, сценарий «избранное»).
- UI-состояния: Loading / Error + Retry / Empty / Success.

API: [REST Countries](https://restcountries.com/) (без ключа). Эндпоинты: `/v3.1/all`, `/v3.1/name/{name}`, `/v3.1/alpha?codes={code}`.

## Как запустить

1. Открыть папку `CountriesExplorer` в Android Studio, дождаться Gradle Sync (JDK 17, compileSdk 35, minSdk 24).
2. Запустить конфигурацию `app` на устройстве/эмуляторе с интернетом.

## Как запустить тесты

- Юнит-тесты (JVM, без эмулятора): `./gradlew testDebugUnitTest`
- Интеграционные/UI-тесты (нужен эмулятор или устройство): `./gradlew connectedDebugAndroidTest`

## Сколько сделано (ДЗ №5)

- **Юнит-тестов: 18** (минимум 6) — `src/test`
- **Тестов на Flow (последовательности эмиссий): 3** (минимум 2, т.к. в проекте есть Flow) — `src/test`
- **Интеграционных тестов: 5** (минимум 3): 3 на data-слой (Repository + Room) + 2 на UI (Compose) — `src/androidTest`
- **Нетривиальных тестов: ≥ 8** (минимум 2) — проверяют контракт поведения, а не только финальное значение

## Какие сценарии покрыты

### Юнит-тесты (`src/test`)

`CountryListViewModelTest`:
- корректное начальное состояние (Loading до выполнения работы)
- успешная загрузка -> Success
- ошибка загрузки -> Error
- пустой результат -> именно Empty, а не Success(emptyList())
- retry() после ошибки инициирует новый запрос и переходит в Success
- debounce поиска: выполняется только последний запрос, устаревший отменяется и не попадает в UI
- повторное добавление в избранное не создаёт дубль
- избранное в state отражает эмиссии репозитория во времени (промежуточные состояния)

`CountryDetailViewModelTest`:
- загрузка деталей по коду из `SavedStateHandle`
- ошибка -> Error
- retry() после ошибки инициирует новый запрос и переходит в Success
- переключение избранного меняет флаг isFavourite

`CountryMappersTest`:
- корректное преобразование DTO -> summary / country
- возврат null при отсутствии обязательных полей (cca3 / name)
- round-trip summary <-> entity без потерь

### Тесты на Flow / последовательности эмиссий (`src/test`, `CountryRepositoryFlowTest`)

- **полная последовательность эмиссий** `observeFavouriteCodes`: `{}` -> `{FRA}` -> `{FRA, ITA}` -> `{ITA}`
- новый подписчик сразу получает текущее значение избранного
- повторное добавление идентичного элемента не порождает лишнюю эмиссию (`expectNoEvents`)

### Интеграционные тесты (`src/androidTest`)

`FavouritesRoomIntegrationTest` (Repository + реальный Room in-memory + Fake API):
- запись в Room и корректное повторное чтение
- повторная вставка той же страны не создаёт дубль строки (PK + REPLACE)
- удаление действительно удаляет запись из Room

`CountryListScreenTest` (Compose UI):
- ошибка -> нажатие Retry -> успешное состояние со списком
- успешное состояние: клик по элементу передаёт корректный код (переход на детали именно нужного id)

## Где смотреть тесты

```
app/src/test/java/com/example/countries/...      <- юнит-тесты + Flow
app/src/androidTest/java/com/example/countries/...<- интеграционные + UI
```

## Чеклист ДЗ №5

- [x] Проект из прошлой ДЗ остаётся рабочим (Compose, Hilt, Retrofit, Room, Navigation, mutableStateOf, viewModelScope, 2+ экрана, UI-состояния)
- [x] 6+ юнит-тестов (есть 18), покрыты успешная загрузка и ошибка + тесты на data-слой/логику
- [x] 3+ интеграционных теста (есть 5): ≥1 на data-слой (Repo + Room) и ≥1 на UI (Compose)
- [x] 2+ нетривиальных теста (есть ≥8)
- [x] Проект с Flow: 2+ теста на эмиссии (есть 3), включая тест на полную последовательность
- [x] Тесты детерминированные (TestDispatcher + StandardTestDispatcher, Turbine, in-memory Room)
