<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Un încărcător de moduri (loader) pentru Minecraft, gratuit, open-source și
  bazat pe pluginuri
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

## Despre Flint

Flint nu este o soluție tradițională de tip „înlocuiește-ți launcher-ul": se
atașează la un proces Minecraft vanilla ca **agent Java** (`-javaagent`) și
preia încărcarea claselor cât mai devreme cu putință, astfel încât modurile și
modulele pot fi încărcate în timp ce continuați să folosiți launcher-ul
original.

> ⚠️ **Problemă cunoscută**
> Deocamdată trebuie să folosiți **launcher-ul original (vanilla) 26.3** pentru
> ca încărcătorul de moduri (loader) Flint să fie detectat. (Motivul exact nu
> este încă cunoscut.)

### Caracteristici

- 🧩 **Arhitectură bazată pe pluginuri** — modurile și modulele de sistem sunt
  fișiere jar independente; puneți-le într-un folder și vor fi preluate automat,
  fără modificări la Flint
- 🪝 **Suport Mixin** — include și initializează SpongePowered Mixin, astfel
  încât puteți injecta și rescrie bytecode-ul oricărei clase din joc
- ⚙️ **Bazat pe agent Java** — construit pe `Instrumentation`, cu suport pentru
  redefineirea și retransformarea claselor
- 🚀 **Mediu de rulare autonom** — sarcinile Gradle descarcă jar-ul clientului,
  bibliotecile și resursele pentru dvs.; nu este nevoie de HMCL sau de alt
  launcher
- 📦 **Două sisteme de module** — `Modsrt` (moduri) și `PubSystem` (module de
  sistem) sunt separate și nu se suprapun niciodată

### Versiuni de joc acceptate

| Suport | Versiune |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Cerințe

| Element | Cerință |
|-----|------|
| JDK | **Java 25** (cerință obligatorie pentru 26.3; Gradle și jocul împart aceeași JVM) |
| Compilare | Gradle Wrapper (`./gradlew`, nu este nevoie de o instalare separată a Gradle) |
| Rețea | Prima compilare descarcă clientul, bibliotecile și resursele (~550MB+) |
| Disc | Mediul de rulare se află în `.flint/minecraft`; lăsați liberi aproximativ 1GB |

## Pornire rapidă

```bash
# 1. Construiește agentul Flint și lansează clientul (bibliotecile/resursele lipsă sunt descărcate automat)
./gradlew runClient
```

`runClient` va: construi `Flint-<version>.jar` → descărca bibliotecile și
resursele → porni jocul cu contul offline `Dev` și monta Flint ca agent Java.

Alte sarcini utile:

```bash
# Descarcă doar manifestul versiunii și client.jar
./gradlew downloadMinecraft

# Descarcă doar bibliotecile de rulare necesare sistemului de operare curent (inclusiv fișierele native)
./gradlew downloadLibraries

# Descarcă doar indexul de resurse și resursele jocului (prima rulare ~484MB)
./gradlew downloadAssets

# Construiește doar jar-ul agentului, fără a porni jocul
./gradlew jar

# Afișează comanda completă de lansare fără a porni jocul, util pentru depanare
./gradlew printRunArgs
```

Puteți, de asemenea, suprascrie orice cheie din `gradle.properties` din linia
de comandă:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Structura directoarelor

În directorul jocului (`--gamedir`, implicit directorul de lucru curent) Flint
creează următoarele pentru dvs.:

```
<game dir>/
├── mods/            # folder de moduri Modsrt, puneți aici fișierele *.jar pentru a le încărca
├── pubSystem/       # folder de module PubSystem, puneți aici fișierele *.jar pentru a le încărca
├── logs/            # jurnalele jocului
└── packager.txt     # fișier de gestionare a pachetelor
```

Mediul de rulare (jar-ul clientului, bibliotecile, resursele) este stocat în
`.flint/minecraft` la rădăcina proiectului — ținut intenționat în afara
`build/`, altfel o singură comandă `gradle clean` ar șterge ~550MB de
descărcări. Îl puteți readuce la `build/minecraft` editând `minecraftDir` din
`gradle.properties`.

## Scrierea de moduri

### Varianta 1: moduri Modsrt (moduri din joc)

Implementați interfața `Modsrt` și anotați clasa cu `@MODS`. Când Flint
scanează folderul `mods/` creează o instanță a clasei și apelează `onLoad()`:

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

### Varianta 2: module PubSystem (extensii la nivel de sistem)

Implementați interfața `PubModule`, marcați clasa cu anotația `@PUBCOM` și
puneți fișierul jar în `pubSystem/`:

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
        // Cu instanța Instrumentation vă puteți înregistra propriul
        // ClassFileTransformer și puteți rescrie orice clasă din joc, fără
        // a modifica codul lui Flint.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` vă oferă o instanță `Instrumentation`, astfel încât un modul își
poate înregistra propriul `ClassFileTransformer` — acesta este lucrul care face
Flint bazat pe pluginuri.

### Varianta 3: Mixin

Flint initializează Mixin din start, astfel încât modurile pot folosi
anotațiile Mixin în mod obișnuit. Fișierul de configurare propriu al lui Flint
este `flint.mixins.json` (`compatibilityLevel: JAVA_25`), iar exemplul de
injectare se află în `org.flint.mixin.TitleScreenTransformer`.

## Configurare

Chei comune din `gradle.properties` (valorile din linia de comandă au
întotdeauna prioritate):

| Cheie | Valoare implicită | Descriere |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Versiunea țintă Minecraft |
| `version` | `26.2-1.22.2-0.1.0` | Versiunea jar-ului agentului Flint |
| `minecraftDir` | `.flint/minecraft` | Directorul de rulare (client.jar / biblioteci / resurse) |
| `offlinePlayerName` | `Dev` | Numele contului offline folosit de `runClient` |
| `windowWidth` / `windowHeight` | (comentate) | Decomentați pentru a forța dimensiunea ferestrei |
| `assetDownloadThreads` | `8` | Fire de execuție pentru prima trecere de descărcare a resurselor |
| `downloadRetries` | `4` | Numărul de reîncercări pentru descărcările eșuate |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Timeout HTTP la conectare / citire |

## Structura proiectului

```
Flint/
├── src/main/java/org/flint/        # nucleul Flint
│   ├── AgentMain.java              # punctul de intrare al agentului Java (premain)
│   ├── modsrt/                     # încărcătorul de moduri (loader) Modsrt
│   ├── pubsystem/                  # încărcătorul de module PubSystem
│   ├── dirpath/                    # inițializarea structurii de directoare
│   ├── mixin/                      # exemplu de Mixin integrat
│   └── mixinservice/               # adaptoare de serviciu Mixin
├── src/main/resources/             # resurse, manifestul agentului, configurația Mixin
├── Mixin/                          # sursele SpongePowered Mixin
├── buildSrc/                        # scripturi de compilare
├── build.gradle                     # definițiile sarcinilor de compilare și rulare
└── gradle.properties                # versiunea și configurația de rulare
```

## Depanare

- **Launcher-ul nu detectează Flint**: vedeți „Problemă cunoscută" mai sus —
  launcher-ul original (vanilla) 26.3 este necesar deocamdată.
- **`client.jar is missing`**: rulați mai întâi `./gradlew downloadMinecraft`.
- **`missing ... libraries`**: rulați mai întâi `./gradlew downloadLibraries`.
- **Eroare de versiune Java**: configurați JVM-ul Gradle pe Java 25.
- **Vreți să vedeți ce rulează de fapt**: `./gradlew printRunArgs` afișează
  comanda completă fără a porni jocul.

## Mulțumiri

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — cadrul de
  injectare a bytecode-ului pe care se bazează suportul Mixin al lui Flint
- [ASM](https://asm.ow2.io/) — biblioteca de manipulare a bytecode-ului de
  nivel jos

---

💡 Sunt binevenite rapoartele de probleme și PR-urile, ele ajută Flint să
devină mai bun.
> ⚠️ Acest README este o traducere și s-ar putea să nu fie complet exactă. În caz de discrepanțe, consultați versiunea în limba engleză: [English](../README.md)
