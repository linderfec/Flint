<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Bezplatný open-source mod loader založený na pluginy pro Minecraft
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

## O projektu Flint

Flint není tradiční řešení „nahraďte svůj launcher“: připojuje se k
procesu originálního Minecraftu jako **Java agent** (`-javaagent`) a přebírá
načítání tříd v nejdřívějším možném okamžiku, takže módy a moduly lze načítat
aniž byste přestali používat originální launcher.

> ⚠️ **Známý problém**
> Prozatím musíte použít **originální launcher 26.3**, aby byl loader Flintu
> detekován. (Přesný důvod zatím není znám.)

### Funkce

- 🧩 **Architektura pluginů** — módy a systémové moduly jsou samostatné soubory
  jar; vložte je do složky a budou načteny, bez jakýchkoli změn samotného Flintu
- 🪝 **Podpora Mixin** — dodává a inicializuje SpongePowered Mixin, takže
  můžete vkládat kód a přepisovat bajtkód libovolné herní třídy
- ⚙️ **Založeno na Java agentu** — postaveno na `Instrumentation`, s podporou
  redefinice a retransformace tříd
- 🚀 **Samostatný běhový prostředek** — úlohy Gradle stáhnou klientský soubor
  jar, knihovny a assety za vás; není potřeba HMCL ani žádný jiný launcher
- 📦 **Dva modulové systémy** — `Modsrt` (módy) a `PubSystem` (systémové
  moduly) jsou oddělené a nikdy si nezavadí navzájem

### Podporované verze hry

| Podpora | Verze |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Požadavky

| Položka | Požadavek |
|-----|------|
| JDK | **Java 25** (povinné pro 26.3; Gradle a hra sdílejí stejné JVM) |
| Sestavení | Gradle Wrapper (`./gradlew`, není potřeba samostatná instalace Gradle) |
| Síť | První sestavení stáhne klienta, knihovny a assety (~550MB+) |
| Disk | Běhový prostředek se nachází v `.flint/minecraft`, ponechte asi 1GB volného místa |

## Rychlý start

```bash
# 1. Sestavte agenta Flintu a spusťte klienta (chybějící knihovny/assety se stáhnou automaticky)
./gradlew runClient
```

`runClient` provede: sestaví `Flint-<version>.jar` → stáhne knihovny a assety →
spustí hru s offline účtem `Dev` a připojí Flint jako Java agenta.

Další užitečné úlohy:

```bash
# Stáhnout pouze manifest verzí a client.jar
./gradlew downloadMinecraft

# Stáhnout pouze běhové knihovny vyžadované aktuálním operačním systémem (včetně nativních knihoven)
./gradlew downloadLibraries

# Stáhnout pouze index assetů a herní zdroje (první spuštění ~484MB)
./gradlew downloadAssets

# Sestavit pouze jar agenta, nespouštět hru
./gradlew jar

# Vypsat celý příkaz pro spuštění bez startu hry, užitečné pro ladění
./gradlew printRunArgs
```

Cokoli v `gradle.properties` můžete také přepsat z příkazové řádky:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Rozložení adresářů

Ve složce hry (`--gamedir`, ve výchozím nastavení aktuální pracovní adresář)
Flint pro vás vytvoří následující:

```
<game dir>/
├── mods/            # Složka módů Modsrt, vložte sem *.jar pro jejich načtení
├── pubSystem/       # Složka modulů PubSystem, vložte sem *.jar pro jejich načtení
├── logs/            # Herní logy
└── packager.txt     # Soubor pro správu balíčků
```

Běhový prostředek (klientský soubor jar, knihovny, assety) je uložen
v `.flint/minecraft` v kořeni projektu — záměrně mimo `build/`, jinak by
jediný příkaz `gradle clean` smazal ~550MB stažených souborů. Zpět jej
přepnete na `build/minecraft` úpravou `minecraftDir` v `gradle.properties`.

## Psaní módů

### Možnost 1: Módy Modsrt (módy ve hře)

Implementujte rozhraní `Modsrt` a označte třídu anotací `@MODS`. Když Flint
prochází složku `mods/`, vytvoří instanci třídy a zavolá `onLoad()`:

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

### Možnost 2: Moduly PubSystem (rozšíření na systémové úrovni)

Implementujte rozhraní `PubModule`, označte třídu anotací `@PUBCOM` a vložte
soubor jar do složky `pubSystem/`:

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
        // Pomocí instance Instrumentation můžete zaregistrovat vlastní
        // ClassFileTransformer a přepsat libovolnou herní třídu, aniž by
        // se zasáhlo do kódu samotného Flintu.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` vám předá instanci `Instrumentation`, takže modul může zaregistrovat
vlastní `ClassFileTransformer` — právě to dělá z Flintu pluginovou platformu.

### Možnost 3: Mixin

Flint inicializuje Mixin přímo z výroby, takže módy mohou používat Mixin
anotace jako obvykle. Vlastní konfigurační soubor Flintu je `flint.mixins.json`
(`compatibilityLevel: JAVA_25`) a příklad injekce se nachází v
`org.flint.mixin.TitleScreenTransformer`.

## Konfigurace

Běžné klíče v `gradle.properties` (hodnoty z příkazové řádky mají vždy přednost):

| Klíč | Výchozí hodnota | Popis |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Cílová verze Minecraftu |
| `version` | `26.2-1.22.2-0.1.0` | Verze souboru jar agenta Flintu |
| `minecraftDir` | `.flint/minecraft` | Adresář běhového prostředku (client.jar / knihovny / assety) |
| `offlinePlayerName` | `Dev` | Název offline účtu používaný úlohou `runClient` |
| `windowWidth` / `windowHeight` | (zakomentováno) | Odkomentujte pro vynucení velikosti okna |
| `assetDownloadThreads` | `8` | Vlákna pro první průchod stahování assetů |
| `downloadRetries` | `4` | Počet opakování u neúspěšných stažení |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Časový limit HTTP připojení / čtení |

## Struktura projektu

```
Flint/
├── src/main/java/org/flint/        # Jádro Flintu
│   ├── AgentMain.java              # Vstupní bod Java agenta (premain)
│   ├── modsrt/                     # Loader módů Modsrt
│   ├── pubsystem/                  # Loader modulů PubSystem
│   ├── dirpath/                    # Inicializace rozložení adresářů
│   ├── mixin/                      # Vstavěný příklad Mixinu
│   └── mixinservice/               # Adaptéry služeb Mixinu
├── src/main/resources/             # Zdroje, manifest agenta, konfigurace Mixinu
├── Mixin/                          # Zdrojové kódy SpongePowered Mixinu
├── buildSrc/                        # Skripty sestavení
├── build.gradle                     # Definice úloh pro sestavení a spuštění
└── gradle.properties                # Konfigurace verze a běhového prostředku
```

## Řešení problémů

- **Launcher nedetekuje Flint**: viz „Známý problém“ výše — prozatím je
  vyžadován originální launcher 26.3.
- **`client.jar is missing`**: nejprve spusťte `./gradlew downloadMinecraft`.
- **`missing ... libraries`**: nejprve spusťte `./gradlew downloadLibraries`.
- **Chyba verze Java**: nasměrujte JVM pro Gradle na Javu 25.
- **Chcete vidět, co se skutečně spouští**: `./gradlew printRunArgs` vypíše
  celý příkaz bez spuštění hry.

## Poděkování

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — rámec pro
  injekci bajtkódu, na kterém je postavena podpora Mixinu ve Flintu
- [ASM](https://asm.ow2.io/) — knihovna pro manipulaci s bajtkódem na nízké úrovni

---

💡 Issues a PR jsou vítány, pomáhají Flintu stávat se lepším.
> ⚠️ Tento README je překlad a nemusí být zcela přesný. V případě nesrovnalostí se řiďte anglickou verzí: [English](../README.md)
