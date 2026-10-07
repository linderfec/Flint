<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Darmowy, open-source'owy loader modów dla Minecrafta oparty na wtyczkach
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

## O Flincie

Flint to nie typowe rozwiązanie „zamień swój launcher": podpina się do
procesu oryginalnego launchera Minecrafta jako **agent Java** (`-javaagent`)
i przejmuje ładowanie klas w możliwie najwcześniejszym momencie, dzięki czemu
moddy i moduły można ładować, nadal korzystając z oryginalnego launchera.

> ⚠️ **Znany problem**
> Na razie wykrycie loadera Flinta wymaga użycia **oryginalnego launchera
> 26.3**. (Dokładna przyczyna nie jest jeszcze znana.)

### Funkcje

- 🧩 **Architektura oparta na wtyczkach** — moddy i moduły systemowe to samodzielne
  pliki JAR; wrzuć je do folderu — zostaną wykryte, bez zmian w samym Flincie
- 🪝 **Obsługa Mixin** — dołącza i uruchamia SpongePowered Mixin, dzięki czemu
  można wstrzykiwać się w bajtkod i go przerabiać w dowolnej klasie gry
- ⚙️ **Oparty na agencie Java** — zbudowany na `Instrumentation`, z obsługą
  redefinecji i retransformacji klas
- 🚀 **Samodzielne środowisko uruchomieniowe** — zadania Gradle pobierają plik
  JAR klienta, biblioteki i zasoby za Ciebie; nie wymaga HMCL ani innego launchera
- 📦 **Dwa systemy modułów** — `Modsrt` (moddy) i `PubSystem` (moduły
  systemowe) są rozdzielone i nigdy sobie nie przeszkadzają

### Wspierane wersje gry

| Wsparcie | Wersja |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Wymagania

| Element | Wymaganie |
|-----|------|
| JDK | **Java 25** (twarde wymaganie dla 26.3; Gradle i gra używają tej samej maszyny wirtualnej) |
| Kompilacja | Gradle Wrapper (`./gradlew`, nie jest potrzebna osobna instalacja Gradle) |
| Sieć | Pierwsza kompilacja pobiera klienta, biblioteki i zasoby (~550MB+) |
| Dysk | Środowisko uruchomieniowe znajduje się w `.flint/minecraft`, zostaw około 1GB wolnego miejsca |

## Szybki start

```bash
# 1. Zbuduj agenta Flinta i uruchom klienta (brakujące biblioteki/zasoby są pobierane automatycznie)
./gradlew runClient
```

`runClient` wykona: budowę `Flint-<version>.jar` → pobranie bibliotek i zasobów →
uruchomienie gry na koncie offline `Dev` i zamontowanie Flinta jako agenta Java.

Pozostałe przydatne zadania:

```bash
# Pobierz tylko manifest wersji i client.jar
./gradlew downloadMinecraft

# Pobierz tylko biblioteki środowiska uruchomieniowego wymagane przez bieżący system operacyjny (w tym pliki natywne)
./gradlew downloadLibraries

# Pobierz tylko indeks zasobów i zasoby gry (pierwsze uruchomienie ~484MB)
./gradlew downloadAssets

# Zbuduj tylko plik JAR agenta, bez uruchamiania gry
./gradlew jar

# Wyświetl pełną komendę uruchamiania bez startowania gry, przydatne przy debugowaniu
./gradlew printRunArgs
```

Wszystko w pliku `gradle.properties` można też nadpisać z wiersza poleceń:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Układ katalogów

W katalogu gry (`--gamedir`, domyślnie bieżący katalog roboczy) Flint tworzy
następujące elementy:

```
<game dir>/
├── mods/            # Folder modów Modsrt, wrzuć tutaj pliki *.jar, aby je załadować
├── pubSystem/       # Folder modułów PubSystem, wrzuć tutaj pliki *.jar, aby je załadować
├── logs/            # Logi gry
└── packager.txt     # Plik zarządzania pakietami
```

Środowisko uruchomieniowe (plik JAR klienta, biblioteki, zasoby) jest
przechowywane w `.flint/minecraft` w katalogu głównym projektu — celowo poza
`build/`, ponieważ pojedyncze `gradle clean` skasowałoby ~550MB pobranych
plików. Przywróć `build/minecraft`, edytując `minecraftDir` w `gradle.properties`.

## Pisanie modów

### Wariant 1: Moddy Modsrt (moddy w grze)

Zaimplementuj interfejs `Modsrt` i oznacz klasę adnotacją `@MODS`. Gdy Flint
skanuje folder `mods/`, tworzy instancję klasy i wywołuje `onLoad()`:

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

### Wariant 2: Moduły PubSystem (rozszerzenia na poziomie systemu)

Zaimplementuj interfejs `PubModule`, oznacz klasę adnotacją `@PUBCOM` i
wrzuć plik JAR do folderu `pubSystem/`:

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
        // Mając do dyspozycji instancję Instrumentation, możesz zarejestrować
        // własny ClassFileTransformer i przerobić dowolną klasę gry, bez
        // ingerencji w kod samego Flinta.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` przekazuje instancję `Instrumentation`, więc moduł może zarejestrować
własny `ClassFileTransformer` — właśnie to sprawia, że Flint jest oparty na wtyczkach.

### Wariant 3: Mixin

Flint uruchamia Mixin od razu, więc moddy mogą używać adnotacji Mixin jak
zwykle. Plik konfiguracyjny samego Flinta to `flint.mixins.json`
(`compatibilityLevel: JAVA_25`), a przykładowe wstrzyknięcie znajduje się w
`org.flint.mixin.TitleScreenTransformer`.

## Konfiguracja

Najczęstsze klucze w pliku `gradle.properties` (wartości z wiersza poleceń zawsze wygrywają):

| Klucz | Domyślna wartość | Opis |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Docelowa wersja Minecrafta |
| `version` | `26.2-1.22.2-0.1.0` | Wersja pliku JAR agenta Flinta |
| `minecraftDir` | `.flint/minecraft` | Katalog środowiska uruchomieniowego (client.jar / biblioteki / zasoby) |
| `offlinePlayerName` | `Dev` | Nazwa konta offline używana przez `runClient` |
| `windowWidth` / `windowHeight` | (zakomentowane) | Odkomentuj, aby wymusić rozmiar okna |
| `assetDownloadThreads` | `8` | Liczba wątków dla pierwszego pobierania zasobów |
| `downloadRetries` | `4` | Liczba ponowień nieudanych pobierań |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Limit czasu HTTP: połączenie / odczyt |

## Struktura projektu

```
Flint/
├── src/main/java/org/flint/        # Rdzeń Flinta
│   ├── AgentMain.java              # Punkt wejścia agenta Java (premain)
│   ├── modsrt/                     # Loader modów Modsrt
│   ├── pubsystem/                  # Loader modułów PubSystem
│   ├── dirpath/                    # Inicjalizacja układu katalogów
│   ├── mixin/                      # Wbudowany przykład Mixina
│   └── mixinservice/               # Adaptery usługi Mixin
├── src/main/resources/             # Zasoby, manifest agenta, konfiguracja Mixina
├── Mixin/                          # Źródła SpongePowered Mixin
├── buildSrc/                        # Skrypty budujące
├── build.gradle                     # Definicje zadań kompilacji i uruchamiania
└── gradle.properties                # Konfiguracja wersji i środowiska uruchomieniowego
```

## Rozwiązywanie problemów

- **Launcher nie wykrywa Flinta**: patrz „Znany problem" powyżej — na razie
  wymagany jest oryginalny launcher 26.3.
- **`client.jar is missing`**: najpierw uruchom `./gradlew downloadMinecraft`.
- **`missing ... libraries`**: najpierw uruchom `./gradlew downloadLibraries`.
- **Błąd wersji Java**: skieruj maszynę wirtualną Gradle na Javę 25.
- **Chcesz zobaczyć, co faktycznie działa**: `./gradlew printRunArgs`
  wyświetla pełną komendę bez uruchamiania gry.

## Podziękowania

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — framework
  wstrzykiwania bajtkodu, na którym opiera się obsługa Mixina w Flincie
- [ASM](https://asm.ow2.io/) — niskopoziomowa biblioteka do manipulacji bajtkodem

---

💡 Zgłoszenia issue i PR są mile widziane — pomagają Flincie stawać się lepszym.
> ⚠️ Ten README jest tłumaczeniem i może nie być w pełni dokładny. W razie rozbieżności zapoznaj się z wersją angielską: [English](../README.md)
