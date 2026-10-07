<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Ingyenes, nyílt forráskódú, plugin-alapú modbetöltő a Minecrafthoz
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

## A Flintről

A Flint nem a hagyományos „cseréld le a launcherodat" megoldás: egy **Java-ügynök**
(`-javaagent`) formájában csatlakozik egy eredeti Minecraft-folyamathoz, és a
lehetséges legkorábbi pillanatban veszi át az osztálybetöltést, így a modok és
modulok úgy tölthetők be, hogy továbbra is az eredeti launchert használhatod.

> ⚠️ **Ismert hiba**
> Egyelőre a Flint betöltőjének észleléséhez az **eredeti 26.3-as launcher**
> használata szükséges. (Az ok pontosan még nem ismert.)

### Funkciók

- 🧩 **Plugin architektúra** — a modok és a rendszermodulok önálló jar fájlok;
  dobd őket egy mappába, és a Flint automatikusan betölti őket, a Flint módosítása nélkül
- 🪝 **Mixin támogatás** — becsomagolja és elindítja a SpongePowered Mixint, így
  bármelyik játékosztály bájtkódjába befecskendezhetsz és azt átírhatod
- ⚙️ **Java-ügynök alapú** — az `Instrumentation`-ra épül, támogatja az
  osztályok újradefiniálását és újratranszformációját
- 🚀 **Önellátó futtatókörnyezet** — a Gradle-feladatok letöltik a kliens jar
  fájlt, a könyvtárakat és az erőforrásokat; nem kell HMCL vagy más launcher
- 📦 **Két modulrendszer** — a `Modsrt` (modok) és a `PubSystem` (rendszermodulok)
  elkülönülnek, és soha nem zavarják egymást

### Támogatott játékverziók

| Támogatás | Verzió |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Követelmények

| Elem | Követelmény |
|-----|------|
| JDK | **Java 25** (szigorú követelmény a 26.3-hoz; a Gradle és a játék ugyanazt a JVM-et használja) |
| Build | Gradle Wrapper (`./gradlew`, nem kell külön Gradle telepítés) |
| Hálózat | Az első build letölti a klienst, a könyvtárakat és az erőforrásokat (~550MB+) |
| Lemez | A futtatókörnyezet a `.flint/minecraft` könyvtárban található, tartsd szabadon kb. 1GB-ot |

## Gyors elindítás

```bash
# 1. Buildeld a Flint ügynököt, és indítsd el a klienst (a hiányzó libraryk/erőforrások automatikusan letöltődnek)
./gradlew runClient
```

A `runClient` a következőket végzi: buildeli a `Flint-<version>.jar` fájlt → letölti a könyvtárakat és az
erőforrásokat → elindítja a játékot az offline `Dev` fiókkal, és a Flinthet Java-ügynökként rendeli hozzá.

További hasznos feladatok:

```bash
# Csak a verziómanifesztum és a client.jar letöltése
./gradlew downloadMinecraft

# Csak az aktuális OS által megkövetelt futtatókönyvtárak letöltése (beleértve a native fájlokat)
./gradlew downloadLibraries

# Csak az erőforrás-index és a játék erőforrásainak letöltése (első futás ~484MB)
./gradlew downloadAssets

# Csak az ügynök jar buildelése, a játék indítása nélkül
./gradlew jar

# A teljes indítási parancs kiírása a játék indítása nélkül, hasznos hibakereséshez
./gradlew printRunArgs
```

A `gradle.properties` bármely beállítását felülírhatod a parancssorból:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Könyvtárszerkezet

A játék könyvtárában (`--gamedir`, alapértelmezés szerint a jelenlegi munkakönyvtár)
a Flint a következőket hozza létre neked:

```
<game dir>/
├── mods/            # Modsrt mod mappa, ide dobd be a *.jar fájlokat a betöltésükhöz
├── pubSystem/       # PubSystem modul mappa, ide dobd be a *.jar fájlokat a betöltésükhöz
├── logs/            # Játéknaplók
└── packager.txt     # Csomagkezelési fájl
```

A futtatókörnyezet (kliens jar, könyvtárak, erőforrások) a projekt gyökerében levő
`.flint/minecraft` könyvtárban tárolódik — szándékosan a `build/` mappán kívül, különben egyetlen
`gradle clean` törölné az ~550MB letöltést. Változd `build/minecraft` értékre a
`gradle.properties` `minecraftDir` kulcsát.

## Modok írása

### 1. lehetőség: Modsrt modok (játékon belüli modok)

Valósítsd meg a `Modsrt` felületet, és lásd el az osztályt az `@MODS` jelöléssel. Amikor
a Flint a `mods/` mappát pásztázza, példányosítja az osztályt, és meghívja az `onLoad()` függvényt:

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

### 2. lehetőség: PubSystem modulok (rendszer szintű kiterjesztések)

Valósítsd meg a `PubModule` felületet, lásd el az osztályt a `@PUBCOM`
jelöléssel, és dobd a jar fájlt a `pubSystem/` mappába:

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
        // Az Instrumentation példánnyal saját ClassFileTransformer-edet
        // regisztrálhatod, és bármely játékosztályt átírhatsz, anélkül,
        // hogy a Flint saját kódjához hozzányúlnál.
    }

    @Override
    public void onUnload() { }
}
```

Az `onLoad` egy `Instrumentation` példányt ad át neked, így egy modul saját
`ClassFileTransformer`-t regisztrálhat — ez teszi a Flinthet plugin-alapúvá.

### 3. lehetőség: Mixin

A Flint beépítetten elindítja a Mixint, így a modok a szokásos módon
használhatják a Mixin-annotációkat. A Flint saját konfigurációs fájlja a
`flint.mixins.json` (`compatibilityLevel: JAVA_25`), a példa-befecskendezés
pedig az `org.flint.mixin.TitleScreenTransformer` osztályban található.

## Konfiguráció

Gyakori kulcsok a `gradle.properties` fájlban (a parancssori értékek mindig érvényesülnek):

| Kulcs | Alapértelmezés | Leírás |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Cél Minecraft-verzió |
| `version` | `26.2-1.22.2-0.1.0` | A Flint ügynök jar verziója |
| `minecraftDir` | `.flint/minecraft` | Futtatókönyvtár (client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | A `runClient` által használt offline fiók neve |
| `windowWidth` / `windowHeight` | (megjegyzéssel kikapcsolva) | Vedd ki a megjegyzést az ablakméret kényszerítéséhez |
| `assetDownloadThreads` | `8` | Szálak az első erőforrásletöltési fázishoz |
| `downloadRetries` | `4` | Újrapróbálkozások száma a sikertelen letöltéseknél |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP kapcsolódási / olvasási időkorlát |

## Projektszerkezet

```
Flint/
├── src/main/java/org/flint/        # Flint magja
│   ├── AgentMain.java              # Java-ügynök belépési pont (premain)
│   ├── modsrt/                     # Modsrt modbetöltő
│   ├── pubsystem/                  # PubSystem modulbetöltő
│   ├── dirpath/                    # Könyvtárszerkezet inicializálása
│   ├── mixin/                      # Beépített példa Mixin
│   └── mixinservice/               # Mixin-szolgáltatás adapterek
├── src/main/resources/             # Erőforrások, ügynök-manifest, Mixin-konfiguráció
├── Mixin/                          # SpongePowered Mixin források
├── buildSrc/                        # Build szkriptek
├── build.gradle                     # Build- és futtatási feladatdefiníciók
└── gradle.properties                # Verzió- és futtatókörnyezet-konfiguráció
```

## Hibakeresés

- **A launcher nem észleli a Flintet**: lásd fent az „Ismert hiba" részt — egyelőre
  a 26.3-as eredeti launcher szükséges.
- **`client.jar is missing`**: futtasd először a `./gradlew downloadMinecraft` parancsot.
- **`missing ... libraries`**: futtasd először a `./gradlew downloadLibraries` parancsot.
- **Java-verzió hiba**: állítsd a Gradle JVM-jét Java 25-re.
- **Látni szeretnéd, mi fut valójában**: a `./gradlew printRunArgs` kiírja a teljes
  parancsot a játék indítása nélkül.

## Köszönet

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — a bájtkód-
  befecskendezési keretrendszer, amelyre a Flint Mixin-támogatása épül
- [ASM](https://asm.ow2.io/) — az alacsony szintű bájtkód-kezelő könyvtár

---

💡 Az issue-kat és PR-kat örömmel fogadjuk, segítenek a Flintnek jobbá válni.
> ⚠️ Ez a README fordítás, előfordulhat, hogy nem teljesen pontos. Eltérés esetén az angol nyelvű verzió az irányadó: [English](../README.md)
