# 🗄️ База данных — MyPartyApp

> Supabase (PostgreSQL 17) Проект: `MyPartyApp` · Регион: `eu-west-3` Обновлено: 10 мая 2026

---

## Обзор таблиц

|Таблица|Назначение|Мягкое удаление|
|---|---|---|
|`profiles`|Профили пользователей|✓ `deleted_at`|
|`events`|Мероприятия|✓ `deleted_at`|
|`participants`|Участие в мероприятиях (просмотр, запись, посещение)|—|
|`tag_categories`|Категории тегов (Спорт, Игры, Музыка...)|—|
|`tags`|Теги внутри категорий (Волейбол, FIFA, DJ-сет...)|—|
|`event_tags`|Связь мероприятий с тегами|—|
|`user_tag_weights`|Веса тегов для рекомендаций|—|
|`payments`|Платежи (предоплата и постоплата)|—|

---

## Таблицы

### `profiles`

Расширение встроенной `auth.users`. Создаётся автоматически при регистрации.

|Колонка|Тип|Ограничения|По умолчанию|Описание|
|---|---|---|---|---|
|`id`|`uuid`|PK, FK → `auth.users.id`|—|ID пользователя|
|`username`|`text`|UNIQUE, NOT NULL|—|Имя пользователя|
|`avatar_url`|`text`|—|—|Путь к фото в Supabase Storage|
|`bio`|`text`|—|—|О себе|
|`rating`|`numeric(3,2)`|CHECK 0–5|`0`|Рейтинг (например 4.75)|
|`level`|`int4`|CHECK >= 1|`1`|Уровень пользователя|
|`created_at`|`timestamptz`|—|`now()`|Дата регистрации|
|`updated_at`|`timestamptz`|—|`now()`|Дата последнего изменения (триггер)|
|`deleted_at`|`timestamptz`|—|`null`|Мягкое удаление|

**Триггеры:**
- `profiles_updated_at` — автоматически обновляет `updated_at` при каждом UPDATE.
- `on_auth_user_created` (на `auth.users`) → `handle_new_user()` — создаёт строку профиля при регистрации, беря `username` из `raw_user_meta_data` (приложение передаёт его в `data` при `signUp`). Если имя не передано — подставляется `user_<8 символов id>`. Поэтому клиент НЕ делает ручной INSERT в `profiles`.

**RLS:**

- SELECT: все видят профили где `deleted_at IS NULL`
- UPDATE: только свой профиль (`auth.uid() = id`)
- INSERT: отдельной политики нет — профиль создаёт только серверный триггер (`SECURITY DEFINER`, обходит RLS).

---

### `events`

Мероприятия. Организатор — всегда пользователь из `profiles`.

|Колонка|Тип|Ограничения|По умолчанию|Описание|
|---|---|---|---|---|
|`id`|`uuid`|PK|`gen_random_uuid()`|ID мероприятия|
|`host_id`|`uuid`|FK → `profiles.id` ON DELETE RESTRICT|—|Организатор|
|`title`|`text`|NOT NULL|—|Название|
|`description`|`text`|—|—|Описание|
|`latitude`|`float8`|—|—|Широта (для Яндекс Карт)|
|`longitude`|`float8`|—|—|Долгота (для Яндекс Карт)|
|`address`|`text`|—|—|Адрес строкой (из геокодера)|
|`starts_at`|`timestamptz`|NOT NULL|—|Начало|
|`ends_at`|`timestamptz`|CHECK > `starts_at`|—|Конец|
|`max_guests`|`int4`|CHECK > 0|—|Максимум гостей|
|`min_guests`|`int4`|CHECK > 0, CHECK <= `max_guests`|`1`|Минимум для расчёта заморозки постоплаты|
|`is_private`|`bool`|—|`false`|Закрытое по ссылке|
|`invite_code`|`text`|UNIQUE, NOT NULL|`substr(md5(random()), 1, 8)`|Код для ссылки и QR|
|`status`|`text`|CHECK IN ('active','cancelled','finished')|`'active'`|Статус|
|`cover_image_url`|`text`|—|—|Обложка (Supabase Storage)|
|`telegram_chat_url`|`text`|—|—|Ссылка на чат в Telegram|
|`payment_type`|`text`|CHECK IN ('free','prepaid','postpaid')|`'free'`|Тип оплаты|
|`price`|`int4`|CHECK >= 0|`0`|Рублей: при prepaid — цена с человека, при postpaid — общий бюджет|
|`commission_pct`|`int4`|CHECK 0–100|`10`|Комиссия сервиса в %|
|`created_at`|`timestamptz`|—|`now()`|Дата создания|
|`deleted_at`|`timestamptz`|—|`null`|Мягкое удаление|

**Важно про `invite_code`:** используется как публичная ссылка (`myparty.app/e/{invite_code}`) и для генерации QR-кода на стороне клиента. Не хранить сам QR — генерировать на лету (библиотека `zxing`).

**Важно про постоплату:** регистрация закрывается за 30 минут до `starts_at` (`starts_at - interval '30 minutes'`). Цена фиксируется в этот момент: `price / count(participants where status='going')`. Заморозка через ЮKassa = `price / min_guests`.

**ON DELETE RESTRICT** на `host_id` — профиль нельзя удалить пока есть активные мероприятия. Используем мягкое удаление.

**RLS:**

- SELECT: публичные мероприятия (`is_private = false AND deleted_at IS NULL`)
- ALL: организатор (`auth.uid() = host_id`)

---

### `participants`

Фиксирует любое взаимодействие пользователя с мероприятием. Один пользователь — одна запись на мероприятие (UNIQUE).

|Колонка|Тип|Ограничения|По умолчанию|Описание|
|---|---|---|---|---|
|`id`|`uuid`|PK|`gen_random_uuid()`|ID записи (кодируется в QR-билет)|
|`event_id`|`uuid`|FK → `events.id` ON DELETE CASCADE|—|Мероприятие|
|`user_id`|`uuid`|FK → `profiles.id` ON DELETE CASCADE|—|Пользователь|
|`status`|`text`|CHECK IN ('viewed','going','attended')|`'viewed'`|Статус участия|
|`created_at`|`timestamptz`|—|`now()`|Когда появилась запись|

**Статусы:**

- `viewed` — открыл карточку мероприятия (дедупликация просмотров)
- `going` — записался
- `attended` — пришёл, QR отсканирован на входе

**Жизненный цикл:** `viewed` → `going` → `attended`. При отмене: `going` → `viewed` (строка не удаляется).

**QR-билет:** `participants.id` кодируется в QR. При сканировании организатором — статус меняется на `attended`.

**Подсчёт постоплаты (SQL):**

```sql
SELECT price / COUNT(*) AS per_person
FROM events e
JOIN participants p ON p.event_id = e.id
WHERE e.id = $eventId AND p.status = 'attended';
```

**RLS:**

- SELECT: все
- INSERT: только от своего имени (`auth.uid() = user_id`)
- UPDATE: только свои записи

---

### `tag_categories`

Категории тегов. Seed-данные заливаются один раз при старте проекта.

|Колонка|Тип|Ограничения|По умолчанию|Описание|
|---|---|---|---|---|
|`id`|`uuid`|PK|`gen_random_uuid()`|ID категории|
|`name`|`text`|NOT NULL, UNIQUE|—|Название (Спорт, Игры, Музыка...)|
|`emoji`|`text`|NOT NULL|—|Эмодзи категории для UI|
|`sort_order`|`int4`|—|`0`|Порядок отображения|

**RLS:** SELECT — все.

**Текущие категории (9 шт.):**

|Категория|Эмодзи|Теги|
|---|---|---|
|Спорт|⚽|Волейбол, Футбол, Баскетбол, Пинг-понг, Бильярд, Настольный теннис|
|Игры|🎮|FIFA, CS2, Mortal Kombat, It Takes Two, Турнир, Покер|
|Настолки|🎲|Мафия, Уно, Крокодил, Активити, Диксит, Монополия|
|Музыка|🎵|Живая музыка, DJ-сет, Акустика, Танцы, Кавер-группа|
|Активности|🎤|Квиз, Karaoke, Кино, Мастер-класс, Фотосессия, Квест|
|Еда|🍕|Барбекю, Пицца, Роллы, Торт, Веганское меню|
|Напитки|🍹|Коктейли, Вино, Пиво, Настойки, Без алкоголя|
|Место|🌿|Терраса, Крыша, Дача, Бар, Бассейн, Пляж|
|Формат|👥|Нетворкинг, Для своих, Открытая, Костюмированная, Свидание вслепую|

---

### `tags`

Теги внутри категорий. Привязаны к `tag_categories`.

|Колонка|Тип|Ограничения|Описание|
|---|---|---|---|
|`id`|`uuid`|PK|ID тега|
|`category_id`|`uuid`|FK → `tag_categories.id` ON DELETE SET NULL|Категория|
|`name`|`text`|NOT NULL, UNIQUE|Название тега|
|`emoji`|`text`|NOT NULL|Эмодзи тега для UI|

**RLS:** SELECT — все.

**Как добавить новый тег:**

```sql
INSERT INTO tags (category_id, name, emoji)
SELECT id, 'Новый тег', '🎯' FROM tag_categories WHERE name = 'Активности';
```

---

### `event_tags`

Связь мероприятий с тегами. Составной первичный ключ — дублей быть не может.

|Колонка|Тип|Ограничения|
|---|---|---|
|`event_id`|`uuid`|PK, FK → `events.id` ON DELETE CASCADE|
|`tag_id`|`uuid`|PK, FK → `tags.id` ON DELETE CASCADE|

**RLS:**

- SELECT: все
- ALL: организатор мероприятия

---

### `user_tag_weights`

Персональные веса тегов для системы рекомендаций. Обновляются по поведению пользователя.

|Колонка|Тип|Ограничения|По умолчанию|Описание|
|---|---|---|---|---|
|`user_id`|`uuid`|PK, FK → `profiles.id` ON DELETE CASCADE|—|Пользователь|
|`tag_id`|`uuid`|PK, FK → `tags.id` ON DELETE CASCADE|—|Тег|
|`weight`|`float4`|CHECK 0.0–1.0|`0.5`|Вес интереса|
|`updated_at`|`timestamptz`|—|`now()`|Когда обновлено|

**Логика весов:**

- При регистрации (выбрал тег) → `0.5`
- Открыл мероприятие с тегом → `+0.05`
- Записался → `+0.1`
- Пришёл (`attended`) → `+0.2`
- Отменил запись → `-0.1`
- Границы: всегда в диапазоне `[0.0, 1.0]`

**Рекомендации:** мероприятия сортируются по сумме весов совпадающих тегов.

**RLS:** ALL — только свои веса.

---

### `payments`

Платёжные записи. Создаются при записи на платное мероприятие.

|Колонка|Тип|Ограничения|По умолчанию|Описание|
|---|---|---|---|---|
|`id`|`uuid`|PK|`gen_random_uuid()`|ID платежа|
|`participant_id`|`uuid`|FK → `participants.id` ON DELETE CASCADE|—|Участник|
|`event_id`|`uuid`|FK → `events.id` ON DELETE CASCADE|—|Мероприятие (денормализация для удобства)|
|`frozen_amount`|`int4`|NOT NULL, CHECK >= 0|—|Заморожено в ЮKassa (рубли)|
|`captured_amount`|`int4`|CHECK >= 0|`null`|Списано после ивента (рубли)|
|`status`|`text`|CHECK IN ('pending','frozen','captured','refunded')|`'pending'`|Статус платежа|
|`yukassa_payment_id`|`text`|UNIQUE|`null`|ID в ЮKassa для сверки и возвратов|
|`created_at`|`timestamptz`|—|`now()`|Дата создания|

**Жизненный цикл платежа:**

```
pending → frozen → captured
                 → refunded
```

**RLS:** SELECT — только свои платежи.

---

## Связи между таблицами

```
auth.users ──────── profiles (1:1)
profiles   ──────<  events           (организатор)
profiles   ──────<  participants      (участник)
profiles   ──────<  user_tag_weights
events     ──────<  participants
events     ──────<  event_tags
events     ──────<  payments
tag_categories ─<  tags
tags       ──────<  event_tags
tags       ──────<  user_tag_weights
participants ────<  payments
```

---

## Индексы

|Индекс|Таблица|Поле|Зачем|
|---|---|---|---|
|`tags_category_id_idx`|`tags`|`category_id`|Теги по категории|
|`events_host_id_idx`|`events`|`host_id`|Мероприятия пользователя|
|`events_starts_at_idx`|`events`|`starts_at`|Сортировка ленты|
|`events_status_idx`|`events`|`status`|Фильтрация активных|
|`events_deleted_at_idx`|`events`|`deleted_at` WHERE NULL|Быстрый поиск неудалённых|
|`participants_event_id_idx`|`participants`|`event_id`|Участники мероприятия|
|`participants_user_id_idx`|`participants`|`user_id`|Мероприятия пользователя|
|`participants_status_idx`|`participants`|`status`|Фильтрация по статусу|
|`payments_event_id_idx`|`payments`|`event_id`|Платежи мероприятия|
|`payments_participant_id_idx`|`payments`|`participant_id`|Платёж участника|
|`user_tag_weights_user_id_idx`|`user_tag_weights`|`user_id`|Веса пользователя|
|`event_tags_tag_id_idx`|`event_tags`|`tag_id`|Джойн тегов в `get_feed`|
|`user_tag_weights_tag_id_idx`|`user_tag_weights`|`tag_id`|Джойн весов в `get_feed`|

> **Производительность RLS:** все политики используют `(select auth.uid())` вместо `auth.uid()` — Postgres вычисляет пользователя один раз на запрос, а не на каждую строку.

---

## Хранилище файлов (Supabase Storage)

Файлы не хранятся в БД — только пути к ним.

|Bucket|Поле в БД|Пример пути|
|---|---|---|
|`avatars`|`profiles.avatar_url`|`avatars/uuid123.jpg`|
|`covers`|`events.cover_image_url`|`covers/event-uuid.jpg`|

---

## PostgreSQL функции

Логика рекомендаций вынесена в функции на стороне БД — приложение делает один вызов, база считает сама.

---

### `increment_tag_weights(p_event_id, p_delta)`

Обновляет веса тегов **текущего** пользователя при действии с ивентом.

**Безопасность:** `SECURITY INVOKER`, `search_path = ''`, доступна только роли `authenticated`. Пользователь определяется на сервере через `auth.uid()` — `p_user_id` параметром НЕ передаётся (иначе можно было бы менять чужие веса).

**Параметры:**

|Параметр|Тип|Описание|
|---|---|---|
|`p_event_id`|`uuid`|У какого ивента берём теги|
|`p_delta`|`float4`|На сколько меняем вес|

**Когда вызывать:**

|Действие|`p_delta`|
|---|---|
|Открыл карточку ивента|`+0.05`|
|Записался|`+0.1`|
|Пришёл (`attended`)|`+0.2`|
|Отменил запись|`-0.1`|

**Логика:** использует UPSERT — если вес тега уже есть, прибавляет дельту к текущему значению. Если тег встречается впервые, создаёт запись с весом `0.5 + delta`. Границы всегда `[0.0, 1.0]`.

**Вызов из Android:**

```kotlin
supabase.postgrest.rpc("increment_tag_weights", mapOf(
    "p_event_id" to eventId,
    "p_delta"    to 0.05f
))
```

---

### `get_feed()`

Возвращает ленту публичных будущих ивентов, отсортированных по интересам **текущего** пользователя.

**Безопасность:** `SECURITY INVOKER`, `search_path = ''`, доступна только роли `authenticated`. Пользователь определяется через `auth.uid()` — параметр не передаётся.

**Возвращает:** `SETOF events` — список строк из таблицы `events`.

**Логика сортировки:** для каждого ивента суммируются веса его тегов у пользователя (`SUM(weight)`). Ивент с тегами "Барбекю + Дача" у пользователя с весами 0.9 и 0.7 получает score = 1.6 и идёт выше ивента с score = 0.3.

**Фильтры:** только `deleted_at IS NULL`, `is_private = false`, `starts_at > now()`.

**Вызов из Android:**

```kotlin
val feed = supabase.postgrest.rpc("get_feed").decodeList<Event>()
```

---

## Хранилище файлов (Supabase Storage)

Файлы не хранятся в БД — только пути к ним.

|Bucket|Поле в БД|Пример пути|
|---|---|---|
|`avatars`|`profiles.avatar_url`|`avatars/uuid123.jpg`|
|`covers`|`events.cover_image_url`|`covers/event-uuid.jpg`|

---

## Соглашения

- **Деньги** — `int4` в рублях (никаких float)
- **Координаты** — `float8` (double precision, погрешность < 1 см)
- **Рейтинг** — `numeric(3,2)` (например `4.75`)
- **Мягкое удаление** — `deleted_at timestamptz`, при удалении ставим `now()`, не делаем DELETE
- **UUID** — все первичные ключи, генерируются через `gen_random_uuid()`
- **Время** — всегда `timestamptz` (с часовым поясом)