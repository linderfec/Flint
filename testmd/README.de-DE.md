<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Ein freier, quelloffener, pluginbasierter Mod-Loader für Minecraft
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

## Über Flint

Flint ist keine klassische „ersetze deine Launcher"-Lösung: Es hängt sich als
**Java Agent** (`-javaagent`) an einen Vanilla-Minecraft-Prozess an und übernimmt
das Klassenladen zum frühestmöglichen Moment, damit Mods und Module geladen
werden können, während Sie weiterhin Ihren ursprünglichen Launcher verwenden.

> ⚠️ **Bekanntes Problem**
> Vorläufig müssen Sie den **Vanilla-Launcher 26.3** verwenden, damit der
> Flint-Loader erkannt wird. (Der genaue Grund ist noch nicht bekannt.)

### Funktionen

- 🧩 **Plugin-Architektur** — Mods und Systemmodule eigenständige Jars; in einen
  Ordner legen und sie werden automatisch aufgenommen, ohne Änderungen an Flint selbst
- 🪝 **Mixin-Unterstützung** — bundled und bootstrapped SpongePowered Mixin, damit
  Sie in Bytecode beliebiger Spielklassen injizieren und diesen umschreiben können
- ⚙️ **Basiert auf Java Agent** — aufgebaut auf `Instrumentation`, mit Unterstützung
  für Klassen-Redefinition und -Retransformation
- 🚀 **Eigenständige Laufzeit** — Gradle-Tasks laden die Client-Jar, Bibliotheken
  und Assets für Sie herunter; kein HMCL oder anderer Launcher erforderlich
- 📦 **Zwei Modulsysteme** — `Modsrt` (Mods) und `PubSystem` (Systemmodule) sind
  getrennt und behindern sich gegenseitig nie

### Unterstützte Spielversionen

| Support | Version |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Voraussetzungen

| Punkt | Anforderung |
|-----|------|
| JDK | **Java 25** (zwingende Voraussetzung für 26.3; Gradle und das Spiel teilen sich dieselbe JVM) |
| Build | Gradle Wrapper (`./gradlew`, keine separate Gradle-Installation nötig) |
| Netzwerk | Der erste Build lädt den Client, die Bibliotheken und die Assets herunter (~550MB+) |
| Datenträger | Die Laufzeit liegt in `.flint/minecraft`, halten Sie etwa 1GB frei |

## Schnellstart

```bash
# 1. Flint-Agenten bauen und den Client starten (fehlende Bibliotheken/Assets werden automatisch heruntergeladen)
./gradlew runClient
```

`runClient` wird Folgendes tun: `Flint-<version>.jar` bauen → Bibliotheken und
Assets herunterladen → das Spiel mit dem Offline-Konto `Dev` starten und Flint
als Java Agent einbinden.

Weitere nützliche Tasks:

```bash
# Nur das Versionsmanifest und die client.jar herunterladen
./gradlew downloadMinecraft

# Nur die für das aktuelle Betriebssystem benötigten Laufzeitbibliotheken herunterladen (inklusive Natives)
./gradlew downloadLibraries

# Nur den Asset-Index und die Spielressourcen herunterladen (erster Lauf ~484MB)
./gradlew downloadAssets

# Nur die Agent-Jar bauen, das Spiel nicht starten
./gradlew jar

# Den vollständigen Startbefehl ausgeben, ohne das Spiel zu starten – nützlich beim Debuggen
./gradlew printRunArgs
```

Sie können außerdem alles in `gradle.properties` auf der Kommandozeile überschreiben:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Verzeichnisstruktur

Im Spielverzeichnis (`--gamedir`, standardmäßig das aktuelle Arbeitsverzeichnis)
legt Flint Folgendes für Sie an:

```
<game dir>/
├── mods/            # Modsrt-Mod-Ordner, *.jar hier ablegen, um sie zu laden
├── pubSystem/       # PubSystem-Modul-Ordner, *.jar hier ablegen, um sie zu laden
├── logs/            # Spielprotokolle
└── packager.txt     # Paketverwaltungsdatei
```

Die Laufzeit (Client-Jar, Bibliotheken, Assets) wird in `.flint/minecraft` im
Projektstamm gespeichert – bewusst außerhalb von `build/`, da sonst ein einzelnes
`gradle clean` etwa 550MB an Downloads löschen würde. Zurück zu `build/minecraft`
wechseln Sie, indem Sie `minecraftDir` in `gradle.properties` bearbeiten.

## Mods schreiben

### Option 1: Modsrt-Mods (Mods im Spiel)

Implementieren Sie das `Modsrt`-Interface und markieren Sie die Klasse mit
`@MODS`. Wenn Flint den `mods/`-Ordner durchsucht, instanziiert es die Klasse
und ruft `onLoad()` auf:

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

### Option 2: PubSystem-Module (Erweiterungen auf Systemebene)

Implementieren Sie das `PubModule`-Interface, markieren Sie die Klasse mit der
`@PUBCOM`-Annotation und legen Sie die Jar in `pubSystem/` ab:

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
        // Mit der Instrumentation-Instanz können Sie Ihren eigenen
        // ClassFileTransformer registrieren und jede Spielklasse
        // umschreiben, ohne Flints eigenen Code anzutasten.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` übergibt Ihnen eine `Instrumentation`-Instanz, sodass ein Modul seinen
eigenen `ClassFileTransformer` registrieren kann – genau das macht Flint
pluginbasiert.

### Option 3: Mixin

Flint bootstrapped Mixin standardmäßig, sodass Mods Mixin-Annotationen wie
gewohnt verwenden können. Flints eigene Konfigurationsdatei ist
`flint.mixins.json` (`compatibilityLevel: JAVA_25`), und die Beispielinjektion
befindet sich in `org.flint.mixin.TitleScreenTransformer`.

## Konfiguration

Häufig verwendete Schlüssel in `gradle.properties` (Werte auf der Kommandozeile
haben immer Vorrang):

| Schlüssel | Standard | Beschreibung |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Ziel-Minecraft-Version |
| `version` | `26.2-1.22.2-0.1.0` | Version der Flint-Agent-Jar |
| `minecraftDir` | `.flint/minecraft` | Laufzeitverzeichnis (client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | Von `runClient` verwendeter Offline-Kontoname |
| `windowWidth` / `windowHeight` | (auskommentiert) | Auskommentieren, um die Fenstergröße zu erzwingen |
| `assetDownloadThreads` | `8` | Threads für den ersten Download-Durchlauf der Assets |
| `downloadRetries` | `4` | Anzahl der Wiederholungen für fehlgeschlagene Downloads |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP-Verbindungs-/Lese-Timeout |

## Projektstruktur

```
Flint/
├── src/main/java/org/flint/        # Flint-Kern
│   ├── AgentMain.java              # Java-Agent-Einstiegspunkt (premain)
│   ├── modsrt/                     # Modsrt-Mod-Loader
│   ├── pubsystem/                  # PubSystem-Modul-Loader
│   ├── dirpath/                    # Bootstrap der Verzeichnisstruktur
│   ├── mixin/                      # Eingebauter Beispiel-Mixin
│   └── mixinservice/               # Mixin-Service-Adapter
├── src/main/resources/             # Ressourcen, Agent-Manifest, Mixin-Konfiguration
├── Mixin/                          # SpongePowered-Mixin-Quellen
├── buildSrc/                        # Build-Skripte
├── build.gradle                     # Definitionen der Build- und Lauf-Tasks
└── gradle.properties                # Versions- und Laufzeitkonfiguration
```

## Fehlerbehebung

- **Der Launcher erkennt Flint nicht**: Siehe „Bekanntes Problem" oben –
  vorläufig wird der Vanilla-Launcher 26.3 benötigt.
- **`client.jar is missing`**: Führen Sie zuerst `./gradlew downloadMinecraft` aus.
- **`missing ... libraries`**: Führen Sie zuerst `./gradlew downloadLibraries` aus.
- **Java-Version-Fehler**: Richten Sie die JVM von Gradle auf Java 25 aus.
- **Möchten Sie sehen, was tatsächlich läuft**: `./gradlew printRunArgs` gibt den
  vollständigen Befehl aus, ohne das Spiel zu starten.

## Danksagung

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — das
  Bytecode-Injektions-Framework, auf dem Flints Mixin-Unterstützung aufbaut
- [ASM](https://asm.ow2.io/) — die Bibliothek für Bytecode-Manipulation auf niedriger Ebene

---

💡 Issues und PRs sind willkommen, sie helfen Flint, besser zu werden.
> ⚠️ Dieses README ist eine Übersetzung und ist möglicherweise nicht vollständig korrekt. Bei Abweichungen beachten Sie bitte die englische Fassung: [English](../README.md)
