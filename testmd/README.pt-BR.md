<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Um carregador de mods para Minecraft, gratuito, de código aberto e baseado em plugins
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

## Sobre o Flint

O Flint não é uma solução tradicional de "substituir seu launcher": ele se
anexa a um processo vanilla do Minecraft como um **agente Java** (`-javaagent`)
e assume o carregamento de classes o mais cedo possível, para que mods e
módulos possam ser carregados enquanto você continua usando o launcher
original.

> ⚠️ **Problema conhecido**
> Por enquanto, você deve usar o **launcher vanilla 26.3** para que o
> carregador Flint seja detectado. (O motivo exato ainda não é conhecido.)

### Recursos

- 🧩 **Arquitetura de plugins** — mods e módulos do sistema são jars
  independentes; coloque-os em uma pasta e eles são reconhecidos, sem alterar
  o próprio Flint
- 🪝 **Suporte a Mixin** — empacota e inicializa o SpongePowered Mixin para que
  você possa injetar e reescrever o bytecode de qualquer classe do jogo
- ⚙️ **Baseado em agente Java** — construído sobre `Instrumentation`, com
  suporte a redefinição e retransformação de classes
- 🚀 **Runtime autossuficiente** — as tarefas do Gradle baixam o client jar,
  bibliotecas e recursos para você; não é necessário HMCL nem outro launcher
- 📦 **Dois sistemas de módulos** — `Modsrt` (mods) e `PubSystem` (módulos do
  sistema) são separados e nunca se atrapalham mutuamente

### Versões do jogo suportadas

| Suporte | Versão |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Requisitos

| Item | Requisito |
|-----|------|
| JDK | **Java 25** (requisito obrigatório para o 26.3; o Gradle e o jogo compartilham a mesma JVM) |
| Build | Gradle Wrapper (`./gradlew`, não é necessária uma instalação separada do Gradle) |
| Rede | A primeira build baixa o client, as bibliotecas e os recursos (~550MB+) |
| Disco | O runtime fica em `.flint/minecraft`, mantenha cerca de 1GB livres |

## Início rápido

```bash
# 1. Construa o agente Flint e inicie o client (libs/recursos ausentes são baixados automaticamente)
./gradlew runClient
```

`runClient` vai: construir `Flint-<version>.jar` → baixar bibliotecas e
recursos → iniciar o jogo com a conta offline `Dev` e montar o Flint como um
agente Java.

Outras tarefas úteis:

```bash
# Apenas baixar o manifest de versões e o client.jar
./gradlew downloadMinecraft

# Apenas baixar as bibliotecas de runtime exigidas pelo sistema atual (incluindo nativos)
./gradlew downloadLibraries

# Apenas baixar o índice de recursos e os arquivos do jogo (primeira execução ~484MB)
./gradlew downloadAssets

# Apenas construir o jar do agente, sem iniciar o jogo
./gradlew jar

# Imprimir o comando completo de inicialização sem iniciar o jogo, útil para depuração
./gradlew printRunArgs
```

Você também pode sobrescrever qualquer coisa em `gradle.properties` pela
linha de comando:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Estrutura de diretórios

No diretório do jogo (`--gamedir`, padrão é o diretório de trabalho atual)
o Flint cria o seguinte para você:

```
<game dir>/
├── mods/            # Pasta de mods Modsrt, coloque *.jar aqui para carregá-los
├── pubSystem/       # Pasta de módulos PubSystem, coloque *.jar aqui para carregá-los
├── logs/            # Logs do jogo
└── packager.txt     # Arquivo de gerenciamento de pacotes
```

O runtime (client jar, bibliotecas, recursos) é armazenado em
`.flint/minecraft` na raiz do projeto — mantido deliberadamente fora de
`build/`, caso contrário um único `gradle clean` apagaria ~550MB de
downloads. Volte para `build/minecraft` editando `minecraftDir` em
`gradle.properties`.

## Escrevendo mods

### Opção 1: Mods Modsrt (mods in-game)

Implemente a interface `Modsrt` e anote a classe com `@MODS`. Quando o Flint
escaneia a pasta `mods/`, ele instancia a classe e chama `onLoad()`:

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

### Opção 2: Módulos PubSystem (extensões em nível de sistema)

Implemente a interface `PubModule`, marque a classe com a anotação `@PUBCOM`
e coloque o jar em `pubSystem/`:

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
        // Com a instância de Instrumentation você pode registrar seu próprio
        // ClassFileTransformer e reescrever qualquer classe do jogo, sem
        // tocar no código do próprio Flint.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` entrega a você uma instância de `Instrumentation`, de modo que um
módulo pode registrar seu próprio `ClassFileTransformer` — é isso que torna o
Flint baseado em plugins.

### Opção 3: Mixin

O Flint já inicializa o Mixin de fábrica, de modo que mods podem usar as
anotações do Mixin normalmente. O arquivo de configuração do próprio Flint é
`flint.mixins.json` (`compatibilityLevel: JAVA_25`), e o exemplo de injeção
está em `org.flint.mixin.TitleScreenTransformer`.

## Configuração

Chaves comuns em `gradle.properties` (os valores da linha de comando sempre
prevalecem):

| Chave | Padrão | Descrição |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Versão alvo do Minecraft |
| `version` | `26.2-1.22.2-0.1.0` | Versão do jar do agente Flint |
| `minecraftDir` | `.flint/minecraft` | Diretório de runtime (client.jar / bibliotecas / recursos) |
| `offlinePlayerName` | `Dev` | Nome da conta offline usada por `runClient` |
| `windowWidth` / `windowHeight` | (comentado) | Descomente para forçar o tamanho da janela |
| `assetDownloadThreads` | `8` | Threads para a primeira passada de download de recursos |
| `downloadRetries` | `4` | Número de tentativas para downloads com falha |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Tempo limite de conexão / leitura HTTP |

## Estrutura do projeto

```
Flint/
├── src/main/java/org/flint/        # Núcleo do Flint
│   ├── AgentMain.java              # Ponto de entrada do agente Java (premain)
│   ├── modsrt/                     # Carregador de mods Modsrt
│   ├── pubsystem/                  # Carregador de módulos PubSystem
│   ├── dirpath/                    # Inicialização da estrutura de diretórios
│   ├── mixin/                      # Mixin de exemplo embutido
│   └── mixinservice/               # Adaptadores de serviço do Mixin
├── src/main/resources/             # Recursos, manifesto do agente, configuração do Mixin
├── Mixin/                          # Código-fonte do SpongePowered Mixin
├── buildSrc/                        # Scripts de build
├── build.gradle                     # Definições de tarefas de build e execução
└── gradle.properties                # Configuração de versão e runtime
```

## Solução de problemas

- **O launcher não detecta o Flint**: veja "Problema conhecido" acima — por
  enquanto, o launcher vanilla 26.3 é obrigatório.
- **`client.jar is missing`**: execute `./gradlew downloadMinecraft` primeiro.
- **`missing ... libraries`**: execute `./gradlew downloadLibraries` primeiro.
- **Erro de versão do Java**: aponte a JVM do Gradle para o Java 25.
- **Quer ver o que realmente é executado**: `./gradlew printRunArgs` imprime o
  comando completo sem iniciar o jogo.

## Créditos

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — o framework
  de injeção de bytecode no qual o suporte a Mixin do Flint é construído
- [ASM](https://asm.ow2.io/) — a biblioteca de baixo nível de manipulação de
  bytecode

---

💡 Issues e PRs são bem-vindos, eles ajudam o Flint a melhorar.
> ⚠️ Este README é uma tradução e pode não estar totalmente correto. Em caso de divergência, consulte a versão em inglês: [English](../README.md)
