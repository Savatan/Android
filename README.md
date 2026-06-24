# Countries Explorer

Android-приложение на открытом API **REST Countries**. Сдача **ДЗ №4**: поверх базового приложения (открытый API + список/детали + ViewModel/Repository/coroutines) добавлены **Hilt** и **Room**.

## ФИО и группа

- **ФИО:** Беседа Семен Денисович
- **Группа:** Б9123-09.03.03пикд(1)


## Какой API

[REST Countries](https://restcountries.com/) — открытый REST API без ключа.

| Эндпоинт | Назначение |
|---|---|
| `GET /v3.1/all?fields=...` | список стран (List) |
| `GET /v3.1/name/{name}?fields=...` | поиск страны по имени (Search) |
| `GET /v3.1/alpha/{code}?fields=...` | детали страны по коду cca3 (Detail/{id}) |

## Что храню в Room

- **Таблица:** `favourite_countries`
- **Сценарий:** Favourites (избранное).
- **Поля:** `code` (PK, cca3), `commonName`, `flagUrl`, `region`, `capital`, `population`.

Звёздочка на элементе списка и сердце на экране деталей добавляют/убирают страну в избранное. Экран Favourites — отдельный route, читает данные из Room через `Flow` и обновляется автоматически. Избранное переживает перезапуск приложения (Room).

## Как запустить

1. Открыть папку `CountriesExplorer` в Android Studio (Ladybug или новее).
2. Дождаться Gradle Sync (JDK 17, compileSdk 35, minSdk 24).
3. Запустить конфигурацию `app` на эмуляторе/устройстве с интернетом. Ключи API не нужны.

## Как проверить Room (сделал → перезапустил → осталось)

1. В списке нажать сердечко у нескольких стран.
2. Открыть экран **Favourites** (звезда в тулбаре) — выбранные страны там.
3. Полностью закрыть и перезапустить приложение (`adb shell am force-stop com.example.countries`, затем открыть снова).
4. Снова открыть **Favourites** — список сохранился. Значит данные лежат в Room.

## Скриншоты

Добавь сюда 4–6 скриншотов после запуска:

- `screenshots/loading.png` — состояние Loading
- `screenshots/error.png` — состояние Error + Retry (можно отключить интернет)
- `screenshots/list.png` — список стран
- `screenshots/detail.png` — экран деталей
- `screenshots/favourites.png` — состояние Room (избранное)

```
![Loading](screenshots/loading.png)
![Error](screenshots/error.png)
![List](screenshots/list.png)
![Detail](screenshots/detail.png)
![Favourites](screenshots/favourites.png)
```

## Архитектура

```
ui (Compose, Material3, Navigation)
  list / detail / favourites  -> stateless экраны + ViewModel (state + callbacks)
        |
        v
domain (model, repository interface)
        |
        v
data
  remote  -> Retrofit CountryApi + DTO (suspend, GsonConverter)
  local   -> Room (AppDatabase, FavouriteCountryDao, FavouriteCountryEntity)
  repository -> CountryRepositoryImpl (api + dao)

di (Hilt) -> NetworkModule, DatabaseModule, RepositoryModule (SingletonComponent)
```

Все сетевые запросы — `suspend`-функции Retrofit, запуск из `viewModelScope`. Зависимости (Retrofit, Repository, Room/DAO) предоставляются через Hilt и нигде не создаются вручную через `new`/конструкторы в UI или ViewModel.

## Чеклист ДЗ №4

- [x] **Hilt**: DI подключён и используется; зависимости не создаются вручную в UI/ViewModel
- [x] **Room**: таблица `favourite_countries`, реально используется в работе приложения
- [x] **Сценарий Favourites**: избранное переживает перезапуск
- [x] Приложение остаётся рабочим: 2+ экрана (List/Search и Detail/{code}), Navigation Compose
- [x] Retrofit + coroutines + viewModelScope
- [x] UI-состояния: Loading / Error + Retry / Empty / Success
