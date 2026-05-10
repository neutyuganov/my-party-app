# 🎉 Моя вечеринка — Дневник разработки

> Последнее обновление: 10 мая 2026
> Команда: Сережа + Саша

---

## ✅ Что сделано

### 🐙 GitHub
- [x] Создан репозиторий
- [x] Друг добавлен в проект
- [x] Ветки `main` и `develop` настроены
- [x] Первый коммит с инициализацией
- [x] Настроено правило: в `main` только через Pull Request
- [x] Разобраться как правильно делать коммиты

### 🤖 Android Studio
- [x] Создан проект (Empty Compose Activity)
- [x] Добавлены зависимости в `build.gradle`
- [x] Создана структура пакетов (core, feature/auth, feature/events, feature/home, feature/profile)
- [x] Подключён Supabase клиент
- [x] Приложение запускается на устройстве Samsung S10e
- [x] Вход и регистрация — полная реализация с ViewModel + Repository
- [x] Регистрация сохраняет username в таблицу profiles
- [x] Архитектура MVVM настроена
- [x] Event.kt — data class со всеми полями таблицы events (включая latitude/longitude)
- [x] EventRepository.kt — getFeed() через RPC-функцию get_feed
- [x] FeedViewModel.kt — состояние ленты (events, isLoading, errorMessage)
- [x] FeedScreen.kt — лента с карточками ивентов, pull-to-refresh, обработка состояний
- [x] ProfileScreen.kt — заглушка профиля с кнопкой выхода
- [x] MainScreen.kt — Scaffold с нижней навигацией (Главная / Профиль)
- [x] Навигация: AppNavigation → MainScreen → FeedScreen / ProfileScreen

### 🗄️ Supabase
- [x] Создан проект на supabase.com
- [x] supabaseClient.kt — подключение и модули
- [x] Auth — вход, регистрация, выход, проверка сессии
- [x] Таблицы созданы: profiles, events, participants, tags, event_tags, user_tag_weights, payments
- [x] RLS политики настроены для всех таблиц
- [x] Индексы на часто используемые поля
- [x] Мягкое удаление (deleted_at) для profiles и events
- [x] Таблица tag_categories — 9 категорий тегов
- [x] Seed-данные: 50 тегов по категориям залиты в БД
- [x] SQL-функция increment_tag_weights — обновляет веса тегов по поведению
- [x] SQL-функция get_feed — лента ивентов по интересам пользователя

### 🎨 Figma
- [ ] Мудборд с референсами (Playace, Partiful, Timepad, Meetup)
- [ ] Цветовая палитра выбрана
- [ ] Компоненты: кнопки, поля, карточки
- [ ] Онбординг (3 экрана)
- [ ] Регистрация и вход
- [ ] Главная лента
- [ ] Карточка ивента
- [ ] Создание ивента
- [ ] Профиль

---

## 📅 Лог — что делали по датам

### 29 марта 2026
- Создан GitHub репозиторий, добавлен друг
- Первый коммит с инициализацией проекта
- Добавлены зависимости в `build.gradle`

### 8 апреля 2026
- Изучен [[Data class]]
- Изучены [[Collections]] и [[Функции коллекций]]
- Изучен [[Формат коммитов]]

### 9 апреля 2026
- Изучен [[ViewModel]] (базовые концепции)

### 19 апреля 2026
- Сделана функция входа и регистрации (скелет)

### 20 апреля 2026 ⭐
- Изучен [[MyPartyApp-Notes/Concepts/ViewModel|ViewModel]] — глубоко: ViewModelStore, viewModelScope, private set
- Изучен [[MyPartyApp-Notes/Concepts/Repository-Pattern|Repository Pattern]] — зачем нужен, suspend-функции
- Изучены [[MyPartyApp-Notes/Concepts/Coroutines|Coroutines]] — suspend, viewModelScope, try-catch-finally
- Изучен [[MyPartyApp-Notes/Concepts/Compose-State|Compose State]] — mutableStateOf, by, remember, rememberSaveable
- Изучен [[MyPartyApp-Notes/Concepts/LaunchedEffect|LaunchedEffect]] — side effects в Compose
- Изучено [[MyPartyApp-Notes/Concepts/Remember-vs-ViewModel|Remember vs ViewModel]] — когда что использовать
- Разобраны файлы проекта:
    - [[MyPartyApp-Notes/Code-Reference/Supabase-Setup|supabaseClient.kt]] — подключение, модули, BuildConfig
    - [[MyPartyApp-Notes/Code-Reference/AuthRepository-Explained|AuthRepository.kt]] — все функции
    - [[MyPartyApp-Notes/Code-Reference/AuthViewModel-Explained|AuthViewModel.kt]] — вся логика
    - [[MyPartyApp-Notes/Code-Reference/LoginScreen-Explained|LoginScreen.kt]] — UI компоненты
- Изучена архитектура [[MyPartyApp-Notes/Architecture/MVVM-Pattern|MVVM]]
- Изучена [[MyPartyApp-Notes/Architecture/Clean-Architecture|Clean Architecture]]

### 10 мая 2026 ⭐
- Спроектирована и создана полная схема БД для МВП (7 таблиц)
- Разобраны типы данных: float8 для координат, int4 для денег, numeric(3,2) для рейтинга
- Продумана система рекомендаций: теги + веса (user_tag_weights), обновляются по поведению
- Продумана механика постоплаты: цена фиксируется за 30 мин до начала, ЮKassa замораживает budget/min_guests
- Продумана система QR-билетов: participant.id кодируется в QR, сканирование меняет статус на attended
- Три статуса участника: viewed → going → attended
- Мягкое удаление через deleted_at вместо физического удаления записей
- Настроены RLS политики и индексы для всех таблиц
- Создана таблица tag_categories (9 категорий)
- Залиты seed-данные: 50 тегов по категориям
- Написана SQL-функция increment_tag_weights (UPSERT весов по действию пользователя)
- Написана SQL-функция get_feed (лента отсортированная по SUM весов тегов)
- **Реализован первый рабочий экран ленты ивентов:**
    - RegisterScreen — добавлено поле username, при регистрации создаётся запись в profiles
    - Event.kt — data class с 21 полем (включая latitude/longitude для Яндекс Карт)
    - EventRepository.kt — getFeed() через postgrest RPC
    - FeedViewModel.kt — состояние ленты, загрузка при init
    - FeedScreen.kt — LazyColumn с EventCard, PullToRefreshBox, три состояния (загрузка / ошибка / пусто)
    - ProfileScreen.kt — заглушка с кнопкой выхода
    - MainScreen.kt — нижняя навигация Главная / Профиль через NavigationBar
    - Исправлен двойной Scaffold (MainActivity + MainScreen) — убран внешний, insets теперь правильные

---

## 🗺️ Следующие шаги

### Ближайшее
- [ ] Экран создания ивента (CreateEventScreen) — форма с названием, адресом, датой, тегами
- [ ] Карточка ивента — полный экран с деталями и кнопкой "Записаться"
- [ ] ProfileScreen — загрузка реальных данных из таблицы profiles (username, рейтинг, уровень)
- [ ] Подключить increment_tag_weights при открытии карточки ивента (+0.05) и записи (+0.1)

### Среднесрочное
- [ ] Figma дизайн — хотя бы Auth + Home экраны
- [ ] Лента ивентов с фильтрами по тегам
- [ ] Push-уведомления
- [ ] Загрузка аватара (Supabase Storage bucket `avatars`)

### Архитектурные задачи
- [ ] Dependency Injection (Hilt или Koin)
- [ ] Navigation Graph (полноценный граф вместо строк)
- [ ] Room для оффлайн кэша
- [ ] Обработка ошибок (sealed class Result)

---

## 💡 Наши фишки (что нас отличает)

- [ ] Закрытые ивенты по ссылке — только по репосту в сторис
- [ ] Горящий статус — ивент через 3 часа, свободные места
- [ ] Рейтинг хостов и гостей
- [ ] Вайб-теги — PlayStation, еда, терраса, алкоголь
- [ ] Настройка интересов для рекомендаций
- [ ] Карта ивентов
- [ ] Уровневая система
- [ ] Вечеринки с фикс. платой за вход или бюджет общий (плата делится среди всех)
- [ ] Постоплата: цена фиксируется за 30 мин до начала (budget / зарегистрировано), ЮKassa замораживает budget/min_guests — участник никогда не платит больше замороженного
- [ ] Регистрация через гугл
- [ ] Ссылки на мероприятия. QR-код мероприятия
- [ ] Подключение календаря
- [ ] Генерация чата в телеграм после создания вечеринки
- [ ] Мероприятия по близости (сделать позже)

---

## 🤔 Открытые вопросы

- [ ] Название приложения (Моя вечеринка, сонм)
- [ ] Первый город для запуска (Омск)
- [ ] Юридическое оформление (ИП, оферта)
- [ ] Как сделать регистрацию пользователя, чтобы она была быстрая и удобная (не мучила пользователя)
- [ ] Подтверждение возраста (госуслуги, чекбокс, на вере — написать что при входе могут потребовать паспорт)

---

## 🐛 Проблемы которые решили

| Проблема | Решение | Дата |
|---|---|---|
| Supabase ключи нельзя хранить в коде | BuildConfig + local.properties | 20 апр |
| UI зависает при сетевом запросе | viewModelScope.launch { } для фона | 20 апр |
| Состояние сбрасывается при повороте | ViewModel переживает поворот экрана | 20 апр |
| Регистрация не создавала запись в profiles | signUp() теперь делает INSERT в profiles после auth.signUpWith() | 10 мая |
| Мигание "Пока нет мероприятий" при старте | isLoading инициализируется true, а не false | 10 мая |
| Список исчезал за спиннером при обновлении | Спиннер показывается только при пустом списке (events.isEmpty()) | 10 мая |
| Огромный отступ у нижней навигации | Убран двойной Scaffold — был в MainActivity и в MainScreen | 10 мая |
| Скачок списка при pull-to-refresh | Следствие двойного Scaffold, исправлено вместе с ним | 10 мая |
| DateTimeFormatter создавался на каждой перерисовке | Вынесен в top-level константу DATE_FORMATTER | 10 мая |

---

## 📝 Важные решения

| Решение | Почему | Дата |
| --- | --- | --- |
| Supabase вместо Firebase | PostgreSQL для сложных фильтров ленты | 28 марта |
| Jetpack Compose вместо XML | Современный стандарт, быстрее UI | 28 марта |
| MVVM + Repository | Разделение ответственности, тестируемость | 20 апр |
| BuildConfig для ключей | Безопасность — ключи не попадают в git | 20 апр |
| float8 для координат | float4 даёт погрешность до 1.7м, для карты критично | 10 мая |
| int4 для денег в рублях | float даёт погрешность при округлении | 10 мая |
| Цена фиксируется по зарегистрированным, не по пришедшим | Участник не доплачивает из-за чужих неявок | 10 мая |
| ON DELETE RESTRICT для events.host_id | Мягкое удаление — профиль не удаляется физически | 10 мая |
| Логика рекомендаций в PostgreSQL функциях | Один вызов вместо трёх, атомарность, не дублируем логику | 10 мая |
| Supabase Storage для фото, путь в БД | Файлы не хранятся в БД, только ссылка | 10 мая |
| Один Scaffold на весь MainScreen | Два Scaffold дублируют системные insets, нижнее меню раздувается | 10 мая |
| rememberSaveable для выбранного таба | Сохраняет активный таб при повороте экрана | 10 мая |

---

## 📚 База знаний

→ [[MyPartyApp-Notes/INDEX|📋 INDEX — карта всех заметок]]

---

*Создан: 29 марта 2026 | Обновлён: 10 мая 2026*
