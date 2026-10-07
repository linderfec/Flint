<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Бесплатный загрузчик модов для Minecraft с открытым исходным кодом и поддержкой плагинов
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

## О Flint

Flint — это не традиционное решение «замени свой лаунчер»: он подключается к
процессу ванильной Minecraft как **Java-агент** (`-javaagent`) и как можно раньше
перехватывает загрузку классов, поэтому модули и моды можно загружать, продолжая
использовать оригинальный лаунчер.

> ⚠️ **Известная проблема**
> На данный момент для обнаружения загрузчика Flint необходимо использовать
> **ванильный лаунчер версии 26.3**. (Точная причина пока неизвестна.)

### Возможности

- 🧩 **Архитектура на основе плагинов** — моды и системные модули представляют
  собой отдельные jar-файлы; положите их в папку, и они будут подхвачены, без
  каких-либо изменений самого Flint
- 🪝 **Поддержка миксинов** — в комплект поставки входит SpongePowered Mixin,
  который инициализируется при запуске, так что вы можете внедрять код и
  перезаписывать байт-код любого класса игры
- ⚙️ **На основе Java-агента** — построен на `Instrumentation`, поддерживает
  переопределение и повторную трансформацию классов
- 🚀 **Автономная среда выполнения** — задачи Gradle сами скачивают клиентский
  jar, библиотеки и ресурсы; HMCL или другой лаунчер не требуется
- 📦 **Две системы модулей** — `Modsrt` (моды) и `PubSystem` (системные модули)
  разделены и никогда не мешают друг другу

### Поддерживаемые версии игры

| Поддержка | Версия |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Требования

| Параметр | Требование |
|-----|------|
| JDK | **Java 25** (строгое требование для 26.3; Gradle и игра используют одну и ту же JVM) |
| Сборка | Gradle Wrapper (`./gradlew`, отдельная установка Gradle не нужна) |
| Сеть | При первой сборке скачиваются клиент, библиотеки и ресурсы (~550 МБ+) |
| Диск | Среда выполнения находится в `.flint/minecraft`, оставьте около 1 ГБ свободного места |

## Быстрый старт

```bash
# 1. Собрать агент Flint и запустить клиент (отсутствующие библиотеки и ресурсы скачиваются автоматически)
./gradlew runClient
```

`runClient` выполнит следующее: соберёт `Flint-<version>.jar` → скачает
библиотеки и ресурсы → запустит игру с офлайн-аккаунтом `Dev` и подключит Flint
в качестве Java-агента.

Другие полезные задачи:

```bash
# Скачать только манифест версий и client.jar
./gradlew downloadMinecraft

# Скачать только библиотеки, необходимые для текущей ОС (включая нативные)
./gradlew downloadLibraries

# Скачать только индекс ресурсов и игровые ресурсы (при первом запуске ~484 МБ)
./gradlew downloadAssets

# Собрать только jar агента, не запуская игру
./gradlew jar

# Вывести полную команду запуска без старта игры — удобно для отладки
./gradlew printRunArgs
```

Также можно переопределить любые значения из `gradle.properties` из командной строки:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Структура каталогов

В игровом каталоге (`--gamedir`, по умолчанию — текущий рабочий каталог)
Flint создаёт для вас следующее:

```
<game dir>/
├── mods/            # Папка модов Modsrt, поместите сюда *.jar, чтобы загрузить их
├── pubSystem/       # Папка модулей PubSystem, поместите сюда *.jar, чтобы загрузить их
├── logs/            # Журналы игры
└── packager.txt     # Файл управления пакетами
```

Среда выполнения (клиентский jar, библиотеки, ресурсы) хранится в
`.flint/minecraft` в корне проекта — намеренно за пределами `build/`, иначе
один `gradle clean` стёр бы ~550 МБ скачанных данных. Чтобы вернуть её обратно
в `build/minecraft`, отредактируйте `minecraftDir` в `gradle.properties`.

## Создание модов

### Вариант 1: моды Modsrt (моды внутри игры)

Реализуйте интерфейс `Modsrt` и пометьте класс аннотацией `@MODS`. Когда Flint
сканирует папку `mods/`, он создаёт экземпляр класса и вызывает `onLoad()`:

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

### Вариант 2: модули PubSystem (расширения системного уровня)

Реализуйте интерфейс `PubModule`, отметьте класс аннотацией `@PUBCOM`
и положите jar-файл в `pubSystem/`:

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
        // С помощью экземпляра Instrumentation можно зарегистрировать собственный
        // ClassFileTransformer и перезаписать любой класс игры, не трогая
        // код самого Flint.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` передаёт вам экземпляр `Instrumentation`, поэтому модуль может
зарегистрировать собственный `ClassFileTransformer` — именно это и делает Flint
плагино-ориентированным.

### Вариант 3: миксины

Flint инициализирует Mixin из коробки, поэтому моды могут использовать
аннотации Mixin как обычно. Собственный конфигурационный файл Flint —
`flint.mixins.json` (`compatibilityLevel: JAVA_25`), а пример внедрения находится
в `org.flint.mixin.TitleScreenTransformer`.

## Конфигурация

Основные ключи в `gradle.properties` (значения из командной строки всегда
имеют приоритет):

| Ключ | По умолчанию | Описание |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Целевая версия Minecraft |
| `version` | `26.2-1.22.2-0.1.0` | Версия jar-файла агента Flint |
| `minecraftDir` | `.flint/minecraft` | Каталог среды выполнения (client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | Имя офлайн-аккаунта, используемого `runClient` |
| `windowWidth` / `windowHeight` | (закомментировано) | Раскомментируйте, чтобы задать размер окна |
| `assetDownloadThreads` | `8` | Количество потоков при первой загрузке ресурсов |
| `downloadRetries` | `4` | Число повторных попыток при неудачной загрузке |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Таймауты HTTP-соединения / чтения |

## Структура проекта

```
Flint/
├── src/main/java/org/flint/        # Ядро Flint
│   ├── AgentMain.java              # Точка входа Java-агента (premain)
│   ├── modsrt/                     # Загрузчик модов Modsrt
│   ├── pubsystem/                  # Загрузчик модулей PubSystem
│   ├── dirpath/                    # Инициализация структуры каталогов
│   ├── mixin/                      # Встроенный пример миксина
│   └── mixinservice/               # Адаптеры служб Mixin
├── src/main/resources/             # Ресурсы, манифест агента, конфигурация миксинов
├── Mixin/                          # Исходный код SpongePowered Mixin
├── buildSrc/                        # Скрипты сборки
├── build.gradle                     # Определения задач сборки и запуска
└── gradle.properties                # Конфигурация версий и среды выполнения
```

## Устранение неполадок

- **Лаунчер не обнаруживает Flint**: см. раздел «Известная проблема» выше —
  на данный момент требуется ванильный лаунчер 26.3.
- **`client.jar is missing`**: сначала выполните `./gradlew downloadMinecraft`.
- **`missing ... libraries`**: сначала выполните `./gradlew downloadLibraries`.
- **Ошибка версии Java**: направьте JVM Gradle на Java 25.
- **Хотите увидеть, что реально запускается**: `./gradlew printRunArgs` выводит
  полную команду без запуска игры.

## Благодарности

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — фреймворк
  внедрения байт-кода, на котором построена поддержка миксинов в Flint
- [ASM](https://asm.ow2.io/) — низкоуровневая библиотека для работы с байт-кодом

---

💡 Issues и PR приветствуются — они помогают сделать Flint лучше.
> ⚠️ Этот README является переводом и может быть неточным. При расхождениях обращайтесь к англоязычной версии: [English](../README.md)
