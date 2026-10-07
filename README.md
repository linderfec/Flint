<p align="center">
  <img src="src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  A free, open-source, plugin-based mod loader for Minecraft
</p>

___
| [English](README.md)
| [简体中文](testmd/README.zh-CN.md)
| [繁體中文](testmd/README.zh-TW.md)
| [日本語](testmd/README.ja-JP.md)
| [한국어](testmd/README.ko-KR.md)
| [Deutsch](testmd/README.de-DE.md)
| [Русский](testmd/README.ru-RU.md)
| [Français](testmd/README.fr-FR.md)
| [العربية](testmd/README.ar-SA.md)
| [Español](testmd/README.es-ES.md)
| [Português](testmd/README.pt-BR.md)
| [Italiano](testmd/README.it-IT.md)
| [Nederlands](testmd/README.nl-NL.md)
| [Polski](testmd/README.pl-PL.md)
| [Čeština](testmd/README.cs-CZ.md)
| [Magyar](testmd/README.hu-HU.md)
| [Română](testmd/README.ro-RO.md)
| [Türkçe](testmd/README.tr-TR.md)
| [Tiếng Việt](testmd/README.vi-VN.md)
| [ไทย](testmd/README.th-TH.md)
| [Bahasa Indonesia](testmd/README.id-ID.md)
| [Українська](testmd/README.uk-UA.md)
___

## About Flint

Flint is not a traditional "replace your launcher" solution: it attaches to a
vanilla Minecraft process as a **Java Agent** (`-javaagent`) and takes over class
loading at the earliest possible moment, so mods and modules can be loaded while
you keep using the original launcher.

> ⚠️ **Known issue**
> For now you must use the **vanilla 26.3 launcher** for the Flint loader to be
> detected. (The exact reason is not known yet.)

### Features

- 🧩 **Plugin architecture** — mods and system modules are standalone jars; drop
  them into a folder and they get picked up, no changes to Flint itself
- 🪝 **Mixin support** — bundles and bootstraps SpongePowered Mixin so you can
  inject into and rewrite bytecode of any game class
- ⚙️ **Java Agent based** — built on `Instrumentation`, with support for class
  redefinition and retransformation
- 🚀 **Self-contained runtime** — Gradle tasks download the client jar,
  libraries and assets for you; no HMCL or other launcher required
- 📦 **Two module systems** — `Modsrt` (mods) and `PubSystem` (system modules)
  are separate and never get in each other's way

### Supported game versions

| Support | Version |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Requirements

| Item | Requirement |
|-----|------|
| JDK | **Java 25** (hard requirement for 26.3; Gradle and the game share the same JVM) |
| Build | Gradle Wrapper (`./gradlew`, no separate Gradle install needed) |
| Network | First build downloads the client, libraries and assets (~550MB+) |
| Disk | The runtime lives in `.flint/minecraft`, keep about 1GB free |

## Quick start

```bash
# 1. Build the Flint agent and launch the client (missing libs/assets are downloaded automatically)
./gradlew runClient
```

`runClient` will: build `Flint-<version>.jar` → download libraries and assets →
start the game with the offline account `Dev` and mount Flint as a Java Agent.

Other useful tasks:

```bash
# Only download the version manifest and client.jar
./gradlew downloadMinecraft

# Only download the runtime libraries required by the current OS (including natives)
./gradlew downloadLibraries

# Only download the asset index and game resources (first run ~484MB)
./gradlew downloadAssets

# Only build the agent jar, do not start the game
./gradlew jar

# Print the full launch command without starting the game, handy for debugging
./gradlew printRunArgs
```

You can also override anything in `gradle.properties` from the command line:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Directory layout

In the game directory (`--gamedir`, defaults to the current working directory)
Flint creates the following for you:

```
<game dir>/
├── mods/            # Modsrt mod folder, drop *.jar here to load them
├── pubSystem/       # PubSystem module folder, drop *.jar here to load them
├── logs/            # Game logs
└── packager.txt     # Package management file
```

The runtime (client jar, libraries, assets) is stored in `.flint/minecraft` at
the project root — deliberately kept outside `build/`, otherwise a single
`gradle clean` would wipe ~550MB of downloads. Switch it back to
`build/minecraft` by editing `minecraftDir` in `gradle.properties`.

## Writing mods

### Option 1: Modsrt mods (in-game mods)

Implement the `Modsrt` interface and annotate the class with `@MODS`. When Flint
scans the `mods/` folder it instantiates the class and calls `onLoad()`:

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

### Option 2: PubSystem modules (system-level extensions)

Implement the `PubModule` interface, mark the class with the `@PUBCOM` annotation
and drop the jar into `pubSystem/`:

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
        // With the Instrumentation instance you can register your own
        // ClassFileTransformer and rewrite any game class, without
        // touching Flint's own code.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` hands you an `Instrumentation` instance, so a module can register its
own `ClassFileTransformer` — this is what makes Flint plugin-based.

### Option 3: Mixin

Flint bootstraps Mixin out of the box, so mods can use Mixin annotations as
usual. Flint's own configuration file is `flint.mixins.json`
(`compatibilityLevel: JAVA_25`), and the example injection lives in
`org.flint.mixin.TitleScreenTransformer`.

## Configuration

Common keys in `gradle.properties` (command line values always win):

| Key | Default | Description |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Target Minecraft version |
| `version` | `26.2-1.22.2-0.1.0` | Version of the Flint agent jar |
| `minecraftDir` | `.flint/minecraft` | Runtime directory (client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | Offline account name used by `runClient` |
| `windowWidth` / `windowHeight` | (commented) | Uncomment to force the window size |
| `assetDownloadThreads` | `8` | Threads for the first asset download pass |
| `downloadRetries` | `4` | Retry count for failed downloads |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP connect / read timeout |

## Project structure

```
Flint/
├── src/main/java/org/flint/        # Flint core
│   ├── AgentMain.java              # Java agent entry point (premain)
│   ├── modsrt/                     # Modsrt mod loader
│   ├── pubsystem/                  # PubSystem module loader
│   ├── dirpath/                    # Directory layout bootstrap
│   ├── mixin/                      # Built-in example mixin
│   └── mixinservice/               # Mixin service adapters
├── src/main/resources/             # Resources, agent manifest, mixin config
├── Mixin/                          # SpongePowered Mixin sources
├── buildSrc/                        # Build scripts
├── build.gradle                     # Build and run task definitions
└── gradle.properties                # Version and runtime configuration
```

## Troubleshooting

- **The launcher does not detect Flint**: see "Known issue" above — the vanilla
  26.3 launcher is required for now.
- **`client.jar is missing`**: run `./gradlew downloadMinecraft` first.
- **`missing ... libraries`**: run `./gradlew downloadLibraries` first.
- **Java version error**: point Gradle's JVM at Java 25.
- **Want to see what actually runs**: `./gradlew printRunArgs` prints the full
  command without starting the game.

## Credits

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — the bytecode
  injection framework Flint's Mixin support is built on
- [ASM](https://asm.ow2.io/) — the low-level bytecode manipulation library

---

💡 Issues and PRs are welcome, they help Flint get better.
> ⚠️ The translated versions of this README may not be fully accurate. If you find a discrepancy in a translation, please refer to this English version or open an issue.
