# Countries Explorer

Сдача **ДЗ №6** — Flow поверх проекта ДЗ №4/5. База (открытый API + список/детали + ViewModel/Repo/coroutines + Hilt + Room) сохранена и работает.

## ФИО и группа

- **ФИО:** Беседа Семен Денисович
- **Группа:** Б9123-09.03.03пикд(1)


## Где реактивная логика

Экран List/Search: состояние собирается из нескольких независимых потоков в один `StateFlow<CountryListUiState>`. Изменение любого источника само пересобирает UI.

## 1) Flow используется по назначению

Поиск по мере ввода + фильтр по региону + флаг «только избранное» + избранное из Room собираются вместе. Ввод проходит через `debounce`/`distinctUntilChanged`, новый запрос отменяет предыдущий через `flatMapLatest`, а избранное из Room и настройки из DataStore влияют на список сами.

## 2) Это не «Flow как простой State»

Состояние экрана — результат `combine` нескольких источников, а не `_state.value = _state.value.copy(...)`. `SharedFlow` — поток действий refresh/retry, а не one-shot.

## 3) Источники данных/событий (4, два из data layer)

| Источник | Тип | Откуда |
|---|---|---|
| строка поиска | `MutableStateFlow<String>` | ввод пользователя |
| refresh/retry | `MutableSharedFlow<Unit>` | поток действий пользователя |
| регион + «только избранное» | `Flow<UserPreferences>` | **DataStore (data layer)** |
| избранное | `Flow<Set<String>>` | **Room (data layer)** |

## 4) Операторы Flow

Объединение: `combine`. Преобразование/управление: `debounce`, `distinctUntilChanged`, `flatMapLatest`, `map`, `onStart`. Дополнительно `stateIn`, `catch`.

`flatMapLatest` отменяет устаревший запрос. Избранное из Room и `onlyFavourites` из DataStore пересобирают список без повторного сетевого запроса.

## 5) StateFlow и SharedFlow уместно

- **StateFlow** — длительное состояние с текущим значением: `query`, итоговый `uiState`.
- **SharedFlow** — поток событий refresh/retry, а не one-shot-сигнал.

## 6) Экран остаётся рабочим

2+ экрана (List/Search, Detail/{code}, Favourites), Navigation Compose, Hilt, Room, Retrofit + coroutines + viewModelScope, состояния Loading / Error + Retry / Empty / Success.

## Как запустить

Открыть в Android Studio, дождаться Gradle Sync (JDK 17, compileSdk 35, minSdk 24), запустить `app`. Тесты: `./gradlew testDebugUnitTest` и `./gradlew connectedDebugAndroidTest`.

## Минимальный чек-лист ДЗ №6

- [x] Реальное объединение потоков (`combine`)
- [x] Flow не сводится к `MutableStateFlow + copy`
- [x] `SharedFlow` не только one-shot
- [x] Flow влияет на реальное поведение
- [x] Экран из 3+ независимых источников (есть 4)
- [x] Источник из data layer (Room + DataStore)
- [x] Оператор объединения и оператор преобразования
- [x] Проект из ДЗ №4 остаётся рабочим
