<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Een gratis, open-source, plugin-gebaseerde modlader voor Minecraft
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

## Over Flint

Flint is geen traditionele "vervang je launcher"-oplossing: het koppelt zich als
**Java-agent** (`-javaagent`) aan een standaard Minecraft-proces en neemt zo
vroeg mogelijk het klassen laden over, waardoor mods en modules geladen kunnen
worden terwijl je gewoon je oorspronkelijke launcher blijft gebruiken.

> ⚠️ **Bekend probleem**
> Voorlopig moet je de **standaard launcher 26.3** gebruiken om de
> Flint-lader te laten detecteren. (De exacte reden is nog niet bekend.)

### Functies

- 🧩 **Plugin-architectuur** — mods en systemmodules zijn zelfstandige jars; stop
  ze in een map en ze worden automatisch geladen, zonder wijzigingen aan Flint zelf
- 🪝 **Mixin-ondersteuning** — bundelt en bootstrapt SpongePowered Mixin zodat je
  in bytecode van elke gameklasse kunt injecteren en deze kunt herschrijven
- ⚙️ **Gebaseerd op een Java-agent** — gebouwd op `Instrumentation`, met
  ondersteuning voor het herdefiniëren en opnieuw transformeren van klassen
- 🚀 **Zelfstandige runtime** — Gradle-taken downloaden de client jar,
  bibliotheken en assets voor je; geen HMCL of andere launcher nodig
- 📦 **Twee modulesystemen** — `Modsrt` (mods) en `PubSystem` (systemmodules)
  zijn gescheiden en komen elkaar nooit in de weg

### Ondersteunde spelversies

| Ondersteuning | Versie |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Vereisten

| Item | Vereiste |
|-----|------|
| JDK | **Java 25** (harde vereiste voor 26.3; Gradle en de game delen dezelfde JVM) |
| Build | Gradle Wrapper (`./gradlew`, geen aparte Gradle-installatie nodig) |
| Netwerk | De eerste build downloadt de client, bibliotheken en assets (~550MB+) |
| Schijf | De runtime staat in `.flint/minecraft`, houd ongeveer 1GB vrij |

## Aan de slag

```bash
# 1. Bouw de Flint-agent en start de client (ontbrekende libs/assets worden automatisch gedownload)
./gradlew runClient
```

`runClient` doet het volgende: bouwt `Flint-<version>.jar` → downloadt bibliotheken
en assets → start de game met het offline account `Dev` en koppelt Flint als Java-agent.

Andere handige taken:

```bash
# Download alleen de versiemanifest en client.jar
./gradlew downloadMinecraft

# Download alleen de runtime-bibliotheken die nodig zijn voor het huidige OS (inclusief natives)
./gradlew downloadLibraries

# Download alleen de asset-index en gameresources (eerste run ~484MB)
./gradlew downloadAssets

# Bouw alleen de agent jar, start de game niet
./gradlew jar

# Print het volledige startcommando zonder de game te starten, handig voor debuggen
./gradlew printRunArgs
```

Je kunt ook elke waarde in `gradle.properties` overschrijven vanaf de commandoregel:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Mappenstructuur

In de gamemap (`--gamedir`, standaard de huidige werkmap)
maakt Flint het volgende voor je aan:

```
<game dir>/
├── mods/            # Modsrt-modmap, stop hier *.jar om ze te laden
├── pubSystem/       # PubSystem-modulemap, stop hier *.jar om ze te laden
├── logs/            # Gamelogs
└── packager.txt     # Pakketbeheerbestand
```

De runtime (client jar, bibliotheken, assets) wordt opgeslagen in
`.flint/minecraft` op de projectroot — bewust buiten `build/` gehouden, want
anders zou één enkele `gradle clean` ~550MB aan downloads wissen. Zet het terug
naar `build/minecraft` door `minecraftDir` in `gradle.properties` aan te passen.

## Mods schrijven

### Optie 1: Modsrt-mods (mods in de game)

Implementeer de `Modsrt`-interface en markeer de klasse met `@MODS`. Wanneer Flint de
`mods/`-map scant, maakt het een instantie van de klasse aan en roept het `onLoad()` aan:

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

### Optie 2: PubSystem-modules (uitbreidingen op systeemniveau)

Implementeer de `PubModule`-interface, markeer de klasse met de `@PUBCOM`-annotatie
en stop de jar in `pubSystem/`:

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
        // Met de Instrumentation-instantie kun je je eigen
        // ClassFileTransformer registreren en elke gameklasse
        // herschrijven, zonder aan de code van Flint zelf te raken.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` geeft je een `Instrumentation`-instantie, zodat een module zijn eigen
`ClassFileTransformer` kan registreren — dit maakt Flint plugin-gebaseerd.

### Optie 3: Mixin

Flint bootstrapt Mixin standaard, zodat mods gewoon Mixin-annotaties kunnen
gebruiken. Het eigen configuratiebestand van Flint is `flint.mixins.json`
(`compatibilityLevel: JAVA_25`), en de voorbeeldinjectie staat in
`org.flint.mixin.TitleScreenTransformer`.

## Configuratie

Veelgebruikte sleutels in `gradle.properties` (waarden van de commandoregel hebben altijd voorrang):

| Sleutel | Standaard | Beschrijving |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Doel-Minecraft-versie |
| `version` | `26.2-1.22.2-0.1.0` | Versie van de Flint-agent jar |
| `minecraftDir` | `.flint/minecraft` | Runtime-map (client.jar / bibliotheken / assets) |
| `offlinePlayerName` | `Dev` | Naam van het offline account dat door `runClient` wordt gebruikt |
| `windowWidth` / `windowHeight` | (uitgecommentarieerd) | Haal de commentaar weg om de venstergrootte te forceren |
| `assetDownloadThreads` | `8` | Threads voor de eerste asset-downloadronde |
| `downloadRetries` | `4` | Aantal herhalingen voor mislukte downloads |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP-connectie-/leestimeout |

## Projectstructuur

```
Flint/
├── src/main/java/org/flint/        # Flint-core
│   ├── AgentMain.java              # Java-agent-invoerpunt (premain)
│   ├── modsrt/                     # Modsrt-modlader
│   ├── pubsystem/                  # PubSystem-modulelader
│   ├── dirpath/                    # Bootstrap van de mappenstructuur
│   ├── mixin/                      # Ingebouwd voorbeeld-Mixin
│   └── mixinservice/              # Mixin-service-adapters
├── src/main/resources/             # Resources, agent-manifest, Mixin-config
├── Mixin/                          # SpongePowered Mixin-bronbestanden
├── buildSrc/                        # Builds scripts
├── build.gradle                     # Definities van build- en run-taken
└── gradle.properties                # Versie- en runtimeconfiguratie
```

## Problemen oplossen

- **De launcher detecteert Flint niet**: zie "Bekend probleem" hierboven — de
  standaard launcher 26.3 is voorlopig vereist.
- **`client.jar is missing`**: voer eerst `./gradlew downloadMinecraft` uit.
- **`missing ... libraries`**: voer eerst `./gradlew downloadLibraries` uit.
- **Java-versiefout**: wijs de JVM van Gradle naar Java 25.
- **Wil je zien wat er echt draait**: `./gradlew printRunArgs` print het volledige
  commando zonder de game te starten.

## Met dank aan

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — het
  bytecode-injectieframework waarop de Mixin-ondersteuning van Flint is gebouwd
- [ASM](https://asm.ow2.io/) — de bibliotheek voor bytecode-manipulatie op laag niveau

---

💡 Issues en PR's zijn welkom, ze helpen Flint om beter te worden.
> ⚠️ Deze README is een vertaling en mogelijk niet volledig correct. Bij verschillen verwijzen we je naar de Engelse versie: [English](../README.md)
