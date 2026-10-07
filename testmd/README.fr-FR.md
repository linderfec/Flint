<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Un chargeur de mods pour Minecraft, gratuit, open source et fondé sur des plugins
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

## À propos de Flint

Flint n'est pas une solution traditionnelle qui « remplace votre lanceur » : il
se rattache à un processus Minecraft vanilla en tant que **Java Agent**
(`-javaagent`) et prend le contrôle du chargement des classes dès le plus tôt
possible, afin que les mods et les modules puissent être chargés tout en
continuant d'utiliser le lanceur d'origine.

> ⚠️ **Problème connu**
> Pour l'instant, vous devez utiliser le **lanceur vanilla 26.3** pour que le
> chargeur Flint soit détecté. (La raison exacte n'est pas encore connue.)

### Fonctionnalités

- 🧩 **Architecture par plugins** — les mods et les modules système sont des
  jars autonomes ; déposez-les dans un dossier et ils sont pris en charge, sans
  rien modifier à Flint lui-même
- 🪝 **Prise en charge de Mixin** — intègre et initialise SpongePowered Mixin
  afin de pouvoir injecter du code et réécrire le code intermédiaire (bytecode)
  de n'importe quelle classe du jeu
- ⚙️ **Basé sur Java Agent** — fondé sur `Instrumentation`, avec prise en charge
  de la redéfinition et de la retransformation des classes
- 🚀 **Environnement d'exécution autonome** — les tâches Gradle téléchargent le
  jar du client, les bibliothèques et les ressources à votre place ; aucun HMCL
  ni autre lanceur n'est requis
- 📦 **Deux systèmes de modules** — `Modsrt` (mods) et `PubSystem` (modules
  système) sont distincts et ne se gênent jamais mutuellement

### Versions du jeu prises en charge

| Prise en charge | Version |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Prérequis

| Élément | Exigence |
|-----|------|
| JDK | **Java 25** (exigence stricte pour 26.3 ; Gradle et le jeu partagent la même JVM) |
| Build | Gradle Wrapper (`./gradlew`, aucune installation séparée de Gradle nécessaire) |
| Réseau | Le premier build télécharge le client, les bibliothèques et les ressources (~550MB+) |
| Disque | L'environnement d'exécution se trouve dans `.flint/minecraft`, gardez environ 1GB d'espace libre |

## Démarrage rapide

```bash
# 1. Construire l'agent Flint et lancer le client (les bibliothèques et ressources manquantes sont téléchargées automatiquement)
./gradlew runClient
```

`runClient` va : construire `Flint-<version>.jar` → télécharger les
bibliothèques et les ressources → démarrer le jeu avec le compte hors ligne
`Dev` et monter Flint en tant que Java Agent.

Autres tâches utiles :

```bash
# Télécharger uniquement le manifeste de version et client.jar
./gradlew downloadMinecraft

# Télécharger uniquement les bibliothèques d'exécution requises par l'OS actuel (natives incluses)
./gradlew downloadLibraries

# Télécharger uniquement l'index des ressources et les ressources du jeu (premier téléchargement ~484MB)
./gradlew downloadAssets

# Construire uniquement le jar de l'agent, sans démarrer le jeu
./gradlew jar

# Imprimer la commande de lancement complète sans démarrer le jeu, pratique pour le débogage
./gradlew printRunArgs
```

Vous pouvez également remplacer n'importe quel élément de `gradle.properties`
depuis la ligne de commande :

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Organisation des répertoires

Dans le répertoire du jeu (`--gamedir`, par défaut le répertoire de travail
courant), Flint crée ce qui suit pour vous :

```
<game dir>/
├── mods/            # Dossier de mods Modsrt, déposez vos *.jar ici pour les charger
├── pubSystem/       # Dossier de modules PubSystem, déposez vos *.jar ici pour les charger
├── logs/            # Journaux du jeu
└── packager.txt     # Fichier de gestion des paquets
```

L'environnement d'exécution (jar du client, bibliothèques, ressources) est
stocké dans `.flint/minecraft` à la racine du projet — volontairement conservé
en dehors de `build/`, sinon un simple `gradle clean` effacerait ~550MB de
téléchargements. Pour revenir à `build/minecraft`, modifiez `minecraftDir` dans
`gradle.properties`.

## Écrire des mods

### Option 1 : les mods Modsrt (mods en jeu)

Implémentez l'interface `Modsrt` et annotez la classe avec `@MODS`. Lorsque
Flint scanne le dossier `mods/`, il instancie la classe et appelle `onLoad()` :

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

### Option 2 : les modules PubSystem (extensions au niveau système)

Implémentez l'interface `PubModule`, marquez la classe avec l'annotation
`@PUBCOM` et déposez le jar dans `pubSystem/` :

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
        // Avec l'instance Instrumentation, vous pouvez enregistrer votre propre
        // ClassFileTransformer et réécrire n'importe quelle classe du jeu, sans
        // toucher au code de Flint lui-même.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` vous transmet une instance `Instrumentation`, ce qui permet à un module
d'enregistrer son propre `ClassFileTransformer` — c'est ce qui fait de Flint un
logiciel extensible par plugins.

### Option 3 : Mixin

Flint initialise Mixin dès son démarrage, si bien que les mods peuvent utiliser
les annotations Mixin comme d'habitude. Le fichier de configuration de Flint
lui-même est `flint.mixins.json` (`compatibilityLevel: JAVA_25`), et
l'exemple d'injection se trouve dans `org.flint.mixin.TitleScreenTransformer`.

## Configuration

Clés courantes dans `gradle.properties` (les valeurs passées en ligne de
commande priment toujours) :

| Clé | Valeur par défaut | Description |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Version de Minecraft ciblée |
| `version` | `26.2-1.22.2-0.1.0` | Version du jar de l'agent Flint |
| `minecraftDir` | `.flint/minecraft` | Répertoire d'exécution (client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | Nom du compte hors ligne utilisé par `runClient` |
| `windowWidth` / `windowHeight` | (commenté) | Décommentez pour forcer la taille de la fenêtre |
| `assetDownloadThreads` | `8` | Nombre de threads pour le premier téléchargement des ressources |
| `downloadRetries` | `4` | Nombre de nouvelles tentatives en cas d'échec de téléchargement |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Délais d'attente HTTP de connexion / de lecture |

## Structure du projet

```
Flint/
├── src/main/java/org/flint/        # Cœur de Flint
│   ├── AgentMain.java              # Point d'entrée de l'agent Java (premain)
│   ├── modsrt/                     # Chargeur de mods Modsrt
│   ├── pubsystem/                  # Chargeur de modules PubSystem
│   ├── dirpath/                    # Initialisation de l'arborescence des dossiers
│   ├── mixin/                      # Mixin d'exemple intégré
│   └── mixinservice/               # Adaptateurs de service Mixin
├── src/main/resources/             # Ressources, manifeste de l'agent, configuration Mixin
├── Mixin/                          # Sources de SpongePowered Mixin
├── buildSrc/                        # Scripts de build
├── build.gradle                     # Définition des tâches de build et d'exécution
└── gradle.properties                # Configuration des versions et de l'environnement d'exécution
```

## Dépannage

- **Le lanceur ne détecte pas Flint** : voir « Problème connu » ci-dessus — le
  lanceur vanilla 26.3 est requis pour l'instant.
- **`client.jar is missing`** : exécutez d'abord `./gradlew downloadMinecraft`.
- **`missing ... libraries`** : exécutez d'abord `./gradlew downloadLibraries`.
- **Erreur de version de Java** : faites pointer la JVM de Gradle vers Java 25.
- **Vous voulez voir ce qui s'exécute réellement** : `./gradlew printRunArgs`
  imprime la commande complète sans démarrer le jeu.

## Crédits

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — le cadre
  d'injection de code intermédiaire (bytecode) sur lequel repose la prise en
  charge de Mixin par Flint
- [ASM](https://asm.ow2.io/) — la bibliothèque de manipulation de code
  intermédiaire de bas niveau

---

💡 Les issues et les PR sont les bienvenues, elles aident Flint à s'améliorer.
> ⚠️ Ce README est une traduction et peut ne pas être tout à fait exact. En cas de divergence, veuillez vous référer à la version anglaise : [English](../README.md)
