<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Безкоштовний завантажувач модів для Minecraft із відкритим вихідним кодом, побудований на плагінах
</p>

___
| [English](../README.md)
| [简体中文](README.zh-CN.md)
| [繁體中文](README.zh-TW.md)
| [日本語](README.ja-JP.md)
| [한국어](README.ko-KR.md)
| [Deutsch](README.de-DE.md)
| [Русский](README.ru-RU.md)
| [Français](README.fr-FR.md)
| [العربية](README.ar-SA.md)
| [Español](README.es-ES.md)
| [Português](README.pt-BR.md)
| [Italiano](README.it-IT.md)
| [Nederlands](README.nl-NL.md)
| [Polski](README.pl-PL.md)
| [Čeština](README.cs-CZ.md)
| [Magyar](README.hu-HU.md)
| [Română](README.ro-RO.md)
| [Türkçe](README.tr-TR.md)
| [Tiếng Việt](README.vi-VN.md)
| [ไทย](README.th-TH.md)
| [Bahasa Indonesia](README.id-ID.md)
| [Українська](README.uk-UA.md)
___

## Про Flint

Flint — це не традиційне рішення «заміни свій лаунчер»: він під'єднується до
ванільного процесу Minecraft як **Java-агент** (`-javaagent`) і якомога раніше
перебирає завантаження класів, тож моди та модулі можна завантажувати, продовжуючи
користуватися оригінальним лаунчером.

> ⚠️ **Відома проблема**
> Наразі для виявлення завантажувача Flint потрібно використовувати **ванільний
> лаунчер 26.3**. (Точну причину поки що не відомо.)

### Можливості

- 🧩 **Архітектура плагінів** — моди та системні модулі є окремими JAR-файлами;
  покладіть їх у папку, і їх буде підхоплено, без жодних змін у самому Flint
- 🪝 **Підтримка Mixin** — пакує та ініціалізує SpongePowered Mixin, тож ви можете
  впроваджуватися в будь-який клас гри та переписувати його байт-код
- ⚙️ **На основі Java-агента** — побудовано на `Instrumentation`, з підтримкою
  перевизначення та трансформації класів
- 🚀 **Автономне середовище виконання** — завдання Gradle завантажують JAR клієнта,
  бібліотеки та ресурси за вас; HMCL чи інший лаунчер не потрібні
- 📦 **Дві системи модулів** — `Modsrt` (моди) та `PubSystem` (системні модулі)
  є окремими і жодним чином не заважають одна одній

### Підтримувані версії гри

| Підтримка | Версія |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Вимоги

| Параметр | Вимога |
|-----|------|
| JDK | **Java 25** (обов'язкова умова для 26.3; Gradle та гра використовують ту саму JVM) |
| Збірка | Gradle Wrapper (`./gradlew`, окрема інсталяція Gradle не потрібна) |
| Мережа | Перша збірка завантажує клієнт, бібліотеки та ресурси (~550 МБ+) |
| Диск | Середовище виконання зберігається в `.flint/minecraft`, залиште близько 1 ГБ вільного місця |

## Швидкий старт

```bash
# 1. Зібрати агента Flint і запустити клієнт (відсутні бібліотеки/ресурси завантажуються автоматично)
./gradlew runClient
```

`runClient` зробить таке: збере `Flint-<version>.jar` → завантажить бібліотеки та ресурси →
запустить гру з офлайн-обліковим записом `Dev` і підключить Flint як Java-агент.

Інші корисні завдання:

```bash
# Лише завантажити маніфест версії та client.jar
./gradlew downloadMinecraft

# Лише завантажити бібліотеки рантайму, потрібні поточній ОС (зокрема нативні)
./gradlew downloadLibraries

# Лише завантажити індекс ресурсів та ігрові ресурси (перший запуск ~484 МБ)
./gradlew downloadAssets

# Лише зібрати JAR агента, не запускати гру
./gradlew jar

# Надрукувати повну команду запуску, не запускаючи гру — зручно для налагодження
./gradlew printRunArgs
```

Ви також можете перевизначити будь-який параметр у `gradle.properties` з командного рядка:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Структура каталогів

В ігровому каталозі (`--gamedir`, типово — поточний робочий каталог)
Flint створює для вас таке:

```
<game dir>/
├── mods/            # Папка модів Modsrt, покладіть сюди *.jar, щоб завантажити їх
├── pubSystem/       # Папка модулів PubSystem, покладіть сюди *.jar, щоб завантажити їх
├── logs/            # Журнали гри
└── packager.txt     # Файл керування пакетами
```

Середовище виконання (JAR клієнта, бібліотеки, ресурси) зберігається в
`.flint/minecraft` у корені проєкту — свідомо поза `build/`, інакше один-єдиний
`gradle clean` стер би ~550 МБ завантажень. Поверніть його в `build/minecraft`,
відредагувавши `minecraftDir` у `gradle.properties`.

## Створення модів

### Варіант 1: Моди Modsrt (моди в грі)

Реалізуйте інтерфейс `Modsrt` і додайте анотацію до класу `@MODS`. Коли Flint
сканує папку `mods/`, він створює екземпляр класу та викликає `onLoad()`:

```java
import org.flint.modsrt.MODS;
import org.flint.modsrt.Modsrt;

@MODS("My Mod")
public class MyMod implements Modsrt {
    @Override
    public void onLoad() {
        System.out.println("My mod loaded!");
    }

    @Override
    public void onUnload() {
        System.out.println("My mod unloaded");
    }
}
```

### Варіант 2: Модулі PubSystem (розширення системного рівня)

Реалізуйте інтерфейс `PubModule`, позначте клас анотацією `@PUBCOM` і покладіть
JAR-файл у `pubSystem/`:

```java
import org.flint.pubsystem.PUBCOM;
import org.flint.pubsystem.PubModule;
import java.lang.instrument.Instrumentation;

@PUBCOM("logger")
public class LoggerModule implements PubModule {
    @Override
    public String getName()    { return "logger"; }

    @Override
    public String getVersion() { return "1.0.0"; }

    @Override
    public void onLoad(Instrumentation inst) {
        // За допомогою екземпляра Instrumentation ви можете зареєструвати власний
        // ClassFileTransformer і переписати будь-який клас гри, не торкаючись
        // власного коду Flint.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` передає вам екземпляр `Instrumentation`, тож модуль може зареєструвати
власний `ClassFileTransformer` — саме це робить Flint плагін-орієнтованим.

### Варіант 3: Mixin

Flint ініціалізує Mixin з коробки, тож моди можуть звично використовувати
анотації Mixin. Власний файл конфігурації Flint — це `flint.mixins.json`
(`compatibilityLevel: JAVA_25`), а приклад впровадження міститься в
`org.flint.mixin.TitleScreenTransformer`.

## Налаштування

Поширені ключі в `gradle.properties` (значення з командного рядка мають пріоритет):

| Ключ | Типове значення | Опис |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Цільова версія Minecraft |
| `version` | `26.2-1.22.2-0.1.0` | Версія JAR-файла агента Flint |
| `minecraftDir` | `.flint/minecraft` | Каталог середовища виконання (client.jar / бібліотеки / ресурси) |
| `offlinePlayerName` | `Dev` | Назва офлайн-облікового запису, яку використовує `runClient` |
| `windowWidth` / `windowHeight` | (закоментовано) | Розкоментуйте, щоб задати розмір вікна |
| `assetDownloadThreads` | `8` | Потоки для першого проходу завантаження ресурсів |
| `downloadRetries` | `4` | Кількість повторів для невдалих завантажень |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Тайм-аут з'єднання / читання HTTP |

## Структура проєкту

```
Flint/
├── src/main/java/org/flint/        # Ядро Flint
│   ├── AgentMain.java              # Точка входу Java-агента (premain)
│   ├── modsrt/                     # Завантажувач модів Modsrt
│   ├── pubsystem/                  # Завантажувач модулів PubSystem
│   ├── dirpath/                    # Ініціалізація структури каталогів
│   ├── mixin/                      # Вбудований приклад mixin
│   └── mixinservice/               # Адаптери сервісів Mixin
├── src/main/resources/             # Ресурси, маніфест агента, конфігурація Mixin
├── Mixin/                          # Вихідний код SpongePowered Mixin
├── buildSrc/                        # Скрипти збирання
├── build.gradle                     # Визначення завдань збирання та запуску
└── gradle.properties                # Конфігурація версій та середовища виконання
```

## Усунення несправностей

- **Лаунчер не виявляє Flint**: дивіться «Відома проблема» вище — наразі потрібен
  ванільний лаунчер 26.3.
- **`client.jar is missing`**: спочатку виконайте `./gradlew downloadMinecraft`.
- **`missing ... libraries`**: спочатку виконайте `./gradlew downloadLibraries`.
- **Помилка версії Java**: націльте JVM Gradle на Java 25.
- **Хочете побачити, що насправді запускається**: `./gradlew printRunArgs` друкує
  повну команду, не запускаючи гру.

## Подяки

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — фреймворк
  впровадження в байт-код, на якому побудована підтримка Mixin у Flint
- [ASM](https://asm.ow2.io/) — бібліотека низькорівневої маніпуляції байт-кодом

---

💡 Проблеми та PR вітаються, вони допомагають Flint ставати кращим.
> ⚠️ Цей README є перекладом і може бути неточним. У разі розбіжностей звертайтеся до англомовної версії: [English](../README.md)
