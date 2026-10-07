<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Un cargador de mods (loader) para Minecraft, gratuito, de código abierto y basado en plugins
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

## Acerca de Flint

Flint no es una solución tradicional de "reemplaza tu lanzador": se adjunta a un
proceso de Minecraft original (vanilla) como **agente de Java** (`-javaagent`) y
se hace cargo de la carga de clases en el momento más temprano posible, de modo
que los mods y los módulos pueden cargarse mientras sigues usando el lanzador
original.

> ⚠️ **Problema conocido**
> Por ahora debes usar el **lanzador original (vanilla) 26.3** para que se
> detecte el cargador de Flint. (La razón exacta aún no se conoce.)

### Características

- 🧩 **Arquitectura de plugins** — los mods y los módulos del sistema son jars
  independientes; déjalos en una carpeta y se cargan automáticamente, sin
  cambios en Flint
- 🪝 **Soporte de Mixin** — incluye y arranca SpongePowered Mixin para que
  puedas inyectar y reescribir el código intermedio (bytecode) de cualquier
  clase del juego
- ⚙️ **Basado en agente de Java** — construido sobre `Instrumentation`, con
  soporte de redefinición y retransformación de clases
- 🚀 **Entorno de ejecución autónomo** — las tareas de Gradle descargan el jar
  del cliente, las bibliotecas y los recursos por ti; no hace falta HMCL ni
  otro lanzador
- 📦 **Dos sistemas de módulos** — `Modsrt` (mods) y `PubSystem` (módulos del
  sistema) son independientes y nunca se estorban entre sí

### Versiones del juego compatibles

| Support | Version |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Requisitos

| Elemento | Requisito |
|-----|------|
| JDK | **Java 25** (requisito obligatorio para 26.3; Gradle y el juego comparten la misma JVM) |
| Compilación | Gradle Wrapper (`./gradlew`, no necesitas instalar Gradle por separado) |
| Red | La primera compilación descarga el cliente, las bibliotecas y los recursos (~550MB+) |
| Disco | El entorno de ejecución vive en `.flint/minecraft`; deja unos 1GB libres |

## Inicio rápido

```bash
# 1. Compila el agente de Flint y lanza el cliente (las bibliotecas/recursos que falten se descargan automáticamente)
./gradlew runClient
```

`runClient` hará lo siguiente: compilar `Flint-<version>.jar` → descargar las
bibliotecas y los recursos → iniciar el juego con la cuenta sin conexión `Dev` y
montar Flint como agente de Java.

Otras tareas útiles:

```bash
# Solo descargar el manifiesto de versiones y client.jar
./gradlew downloadMinecraft

# Solo descargar las bibliotecas de tiempo de ejecución que necesita el sistema operativo actual (incluidas las nativas)
./gradlew downloadLibraries

# Solo descargar el índice de recursos y los recursos del juego (primera ejecución ~484MB)
./gradlew downloadAssets

# Solo compilar el jar del agente, sin iniciar el juego
./gradlew jar

# Imprimir el comando de lanzamiento completo sin iniciar el juego, útil para depurar
./gradlew printRunArgs
```

También puedes sobrescribir cualquier valor de `gradle.properties` desde la
línea de comandos:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Estructura de directorios

En el directorio del juego (`--gamedir`, por defecto el directorio de trabajo
actual) Flint crea lo siguiente por ti:

```
<game dir>/
├── mods/            # Carpeta de mods de Modsrt, coloca aquí los *.jar para cargarlos
├── pubSystem/       # Carpeta de módulos de PubSystem, coloca aquí los *.jar para cargarlos
├── logs/            # Registros del juego
└── packager.txt     # Archivo de gestión de paquetes
```

El entorno de ejecución (jar del cliente, bibliotecas, recursos) se guarda en
`.flint/minecraft` en la raíz del proyecto — se mantiene deliberadamente fuera
de `build/`, ya que de otro modo un simple `gradle clean` borraría ~550MB de
descargas. Puedes volver a `build/minecraft` editando `minecraftDir` en
`gradle.properties`.

## Crear mods

### Opción 1: Mods de Modsrt (mods dentro del juego)

Implementa la interfaz `Modsrt` y anota la clase con `@MODS`. Cuando Flint
escanea la carpeta `mods/` instancia la clase y llama a `onLoad()`:

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

### Opción 2: Módulos de PubSystem (extensiones a nivel de sistema)

Implementa la interfaz `PubModule`, marca la clase con la anotación `@PUBCOM`
y coloca el jar en `pubSystem/`:

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
        // Con la instancia de Instrumentation puedes registrar tu propio
        // ClassFileTransformer y reescribir cualquier clase del juego, sin
        // tocar el código de Flint.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` te entrega una instancia de `Instrumentation`, de modo que un módulo
puede registrar su propio `ClassFileTransformer` — esto es lo que hace que Flint
se base en plugins.

### Opción 3: Mixin

Flint arranca Mixin de serie, de modo que los mods pueden usar las anotaciones
de Mixin con normalidad. El archivo de configuración propio de Flint es
`flint.mixins.json` (`compatibilityLevel: JAVA_25`), y el ejemplo de inyección
está en `org.flint.mixin.TitleScreenTransformer`.

## Configuración

Claves comunes en `gradle.properties` (los valores de la línea de comandos
tienen prioridad):

| Clave | Valor predeterminado | Descripción |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Versión de Minecraft objetivo |
| `version` | `26.2-1.22.2-0.1.0` | Versión del jar del agente de Flint |
| `minecraftDir` | `.flint/minecraft` | Directorio del entorno de ejecución (client.jar / bibliotecas / recursos) |
| `offlinePlayerName` | `Dev` | Nombre de la cuenta sin conexión que usa `runClient` |
| `windowWidth` / `windowHeight` | (comentado) | Descomenta para forzar el tamaño de la ventana |
| `assetDownloadThreads` | `8` | Hilos para la primera pasada de descarga de recursos |
| `downloadRetries` | `4` | Número de reintentos para descargas fallidas |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Tiempo de espera de conexión / lectura HTTP |

## Estructura del proyecto

```
Flint/
├── src/main/java/org/flint/        # Núcleo de Flint
│   ├── AgentMain.java              # Punto de entrada del agente de Java (premain)
│   ├── modsrt/                     # Cargador de mods de Modsrt
│   ├── pubsystem/                  # Cargador de módulos de PubSystem
│   ├── dirpath/                    # Inicialización de la estructura de directorios
│   ├── mixin/                      # Mixin de ejemplo integrado
│   └── mixinservice/               # Adaptadores de servicio de Mixin
├── src/main/resources/             # Recursos, manifiesto del agente, configuración de Mixin
├── Mixin/                          # Fuentes de SpongePowered Mixin
├── buildSrc/                        # Scripts de compilación
├── build.gradle                     # Definición de tareas de compilación y ejecución
└── gradle.properties                # Configuración de versión y de entorno de ejecución
```

## Solución de problemas

- **El lanzador no detecta Flint**: consulta "Problema conocido" más arriba — por
  ahora se necesita el lanzador original (vanilla) 26.3.
- **`client.jar is missing`**: ejecuta primero `./gradlew downloadMinecraft`.
- **`missing ... libraries`**: ejecuta primero `./gradlew downloadLibraries`.
- **Error de versión de Java**: apunta la JVM de Gradle a Java 25.
- **Quieres ver lo que se ejecuta realmente**: `./gradlew printRunArgs` imprime
  el comando completo sin iniciar el juego.

## Agradecimientos

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — el framework
  de inyección de código intermedio (bytecode) sobre el que se construye el
  soporte de Mixin de Flint
- [ASM](https://asm.ow2.io/) — la biblioteca de bajo nivel para manipulación de
  código intermedio (bytecode)

---

💡 ¡Son bienvenidos los issues y los PRs, ayudan a que Flint mejore!
> ⚠️ Este README es una traducción y puede no ser del todo exacta. Si detectas alguna discrepancia, consulta la versión en inglés: [English](../README.md)
