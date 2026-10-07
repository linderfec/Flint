<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Un caricatore di mod per Minecraft libero, open-source e basato su plugin
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

## Informazioni su Flint

Flint non è una tradizionale soluzione che "sostituisce la tua launcher": si
attacca a un processo Minecraft vanilla come **agente Java** (`-javaagent`) e si
prende il controllo del caricamento delle classi il prima possibile, così mod e
moduli possono essere caricati mentre continui a usare la launcher originale.

> ⚠️ **Problema noto**
> Per ora devi usare la **launcher vanilla 26.3** affinché il caricatore Flint
> venga rilevato. (La ragione esatta non è ancora nota.)

### Funzionalità

- 🧩 **Architettura a plugin** — mod e moduli di sistema sono jar indipendenti;
  mettili in una cartella e vengono rilevati, senza modifiche a Flint stesso
- 🪝 **Supporto Mixin** — include e avvia SpongePowered Mixin così puoi
  iniettare e riscrivere il bytecode di qualsiasi classe di gioco
- ⚙️ **Basato su Java Agent** — costruito su `Instrumentation`, con supporto per
  ridefinizione e ritrasformazione delle classi
- 🚀 **Runtime autosufficiente** — i task Gradle scaricano per te il client jar,
  le librerie e le risorse; non serve HMCL o altre launcher
- 📦 **Due sistemi di moduli** — `Modsrt` (mod) e `PubSystem` (moduli di
  sistema) sono separati e non si interferiscono mai tra loro

### Versioni di gioco supportate

| Supporto | Versione |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Requisiti

| Elemento | Requisito |
|-----|------|
| JDK | **Java 25** (requisito obbligatorio per 26.3; Gradle e il gioco condividono la stessa JVM) |
| Build | Gradle Wrapper (`./gradlew`, non serve un'installazione separata di Gradle) |
| Rete | La prima build scarica il client, le librerie e le risorse (~550MB+) |
| Disco | Il runtime risiede in `.flint/minecraft`, tieni circa 1GB liberi |

## Inizio rapido

```bash
# 1. Compila l'agente Flint e avvia il client (librerie/risorse mancanti scaricate automaticamente)
./gradlew runClient
```

`runClient` farà: compilare `Flint-<version>.jar` → scaricare librerie e risorse
→ avviare il gioco con l'account offline `Dev` e montare Flint come agente Java.

Altri task utili:

```bash
# Scarica solo il manifest delle versioni e client.jar
./gradlew downloadMinecraft

# Scarica solo le librerie richieste dal sistema operativo corrente (inclusi i nativi)
./gradlew downloadLibraries

# Scarica solo l'indice delle risorse e le risorse di gioco (prima esecuzione ~484MB)
./gradlew downloadAssets

# Compila solo il jar dell'agente, senza avviare il gioco
./gradlew jar

# Stampa il comando di avvio completo senza avviare il gioco, utile per il debug
./gradlew printRunArgs
```

Puoi anche sovrascrivere qualsiasi cosa in `gradle.properties` dalla riga di comando:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Struttura delle cartelle

Nella directory di gioco (`--gamedir`, predefinita alla directory di lavoro
corrente) Flint crea per te quanto segue:

```
<game dir>/
├── mods/            # Cartella delle mod Modsrt, inserisci qui i *.jar per caricarli
├── pubSystem/       # Cartella dei moduli PubSystem, inserisci qui i *.jar per caricarli
├── logs/            # Log di gioco
└── packager.txt     # File di gestione dei pacchetti
```

Il runtime (client jar, librerie, risorse) è memorizzato in `.flint/minecraft`
alla radice del progetto — volutamente tenuto fuori da `build/`, altrimenti un
semplice `gradle clean` cancellerebbe ~550MB di download. Puoi riportarlo in
`build/minecraft` modificando `minecraftDir` in `gradle.properties`.

## Scrivere mod

### Opzione 1: Mod Modsrt (mod in-game)

Implementa l'interfaccia `Modsrt` e annota la classe con `@MODS`. Quando Flint
scansiona la cartella `mods/` istanzia la classe e chiama `onLoad()`:

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

### Opzione 2: Moduli PubSystem (estensioni a livello di sistema)

Implementa l'interfaccia `PubModule`, marca la classe con l'annotazione
`@PUBCOM` e inserisci il jar in `pubSystem/`:

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
        // Con l'istanza di Instrumentation puoi registrare il tuo proprio
        // ClassFileTransformer e riscrivere qualsiasi classe di gioco, senza
        // toccare il codice di Flint.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` ti fornisce un'istanza di `Instrumentation`, così un modulo può registrare
il proprio `ClassFileTransformer` — è questo che rende Flint basato su plugin.

### Opzione 3: Mixin

Flint avvia Mixin out of the box, così le mod possono usare le annotazioni
Mixin come di consueto. Il file di configurazione di Flint è
`flint.mixins.json` (`compatibilityLevel: JAVA_25`), e l'esempio di iniezione
si trova in `org.flint.mixin.TitleScreenTransformer`.

## Configurazione

Chiavi comuni in `gradle.properties` (la riga di comando ha sempre precedenza):

| Chiave | Predefinito | Descrizione |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Versione di Minecraft obiettivo |
| `version` | `26.2-1.22.2-0.1.0` | Versione del jar dell'agente Flint |
| `minecraftDir` | `.flint/minecraft` | Directory del runtime (client.jar / librerie / risorse) |
| `offlinePlayerName` | `Dev` | Nome dell'account offline usato da `runClient` |
| `windowWidth` / `windowHeight` | (commentato) | Decommenta per forzare la dimensione della finestra |
| `assetDownloadThreads` | `8` | Thread per il primo passaggio di download delle risorse |
| `downloadRetries` | `4` | Numero di tentativi per i download falliti |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Timeout di connessione / lettura HTTP |

## Struttura del progetto

```
Flint/
├── src/main/java/org/flint/        # Nucleo di Flint
│   ├── AgentMain.java              # Punto di ingresso dell'agente Java (premain)
│   ├── modsrt/                     # Caricatore di mod Modsrt
│   ├── pubsystem/                  # Caricatore di moduli PubSystem
│   ├── dirpath/                    # Inizializzazione della struttura delle cartelle
│   ├── mixin/                      # Mixin di esempio integrato
│   └── mixinservice/               # Adattatori del servizio Mixin
├── src/main/resources/             # Risorse, manifest dell'agente, config Mixin
├── Mixin/                          # Sorgenti di SpongePowered Mixin
├── buildSrc/                        # Script di build
├── build.gradle                     # Definizioni dei task di build ed esecuzione
└── gradle.properties                # Configurazione di versione e runtime
```

## Risoluzione dei problemi

- **La launcher non rileva Flint**: vedi "Problema noto" sopra — per ora serve
  la launcher vanilla 26.3.
- **`client.jar is missing`**: esegui prima `./gradlew downloadMinecraft`.
- **`missing ... libraries`**: esegui prima `./gradlew downloadLibraries`.
- **Errore di versione Java**: punta la JVM di Gradle a Java 25.
- **Vuoi vedere cosa viene effettivamente eseguito**: `./gradlew printRunArgs`
  stampa il comando completo senza avviare il gioco.

## Riconoscimenti

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — il framework
  di iniezione di bytecode su cui si basa il supporto Mixin di Flint
- [ASM](https://asm.ow2.io/) — la libreria di manipolazione del bytecode a basso livello

---

💡 Segnalazioni e PR sono benvenute, aiutano Flint a migliorare.
> ⚠️ Questo README è una traduzione e potrebbe non essere del tutto accurato. In caso di discrepanze, fai riferimento alla versione inglese: [English](../README.md)
