<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  一款面向 Minecraft 的免费、开源、插件式模组加载器
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

## 关于 Flint

Flint 不是传统的“替换启动器”式方案：它以 **Java Agent**（`-javaagent`）的方式挂载到原版 Minecraft 进程中，在游戏启动的最早期接管类加载，从而加载模组与扩展模块。你仍然使用原版启动器启动游戏，Flint 在背后工作。

> ⚠️ **已知问题**
> 目前必须使用**原版 26.3 版本的启动器**，才能被检测到 Flint 加载器。（暂时不确定具体原因）

### 核心特性

- 🧩 **插件式架构** —— 模组与系统模块都是独立 jar，放入目录即可被扫描加载，无需改动 Flint 本体
- 🪝 **Mixin 支持** —— 内置并引导 SpongePowered Mixin，可在任意游戏类上注入、篡改字节码
- ⚙️ **Java Agent 挂载** —— 基于 `Instrumentation`，支持类的重新定义（redefine）与重转换（retransform）
- 🚀 **自带运行时管理** —— Gradle 任务自动下载客户端 jar、依赖库与资源，无需 HMCL 等第三方启动器
- 📦 **双模块体系** —— `Modsrt`（模组）与 `PubSystem`（系统模块）分工明确，互不干扰

### 支持的游戏版本

| 支持 | 版本 |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## 环境要求

| 项目 | 要求 |
|-----|------|
| JDK | **Java 25**（26.3 版本硬性要求，Gradle 与游戏共用同一 JVM） |
| 构建 | Gradle Wrapper（`./gradlew`，无需单独安装 Gradle） |
| 网络 | 首次构建需联网下载 Minecraft 客户端、依赖库与资源（约 550MB+） |
| 磁盘 | 运行时放在 `.flint/minecraft`，请预留约 1GB 空间 |

## 快速开始

```bash
# 1. 构建 Flint Agent 并启动客户端（自动下载缺失的库与资源）
./gradlew runClient
```

`runClient` 会完成以下事情：构建 `Flint-<版本>.jar` → 下载依赖库与资源 → 以离线账号 `Dev` 启动游戏，并把 Flint 挂载为 Java Agent。

其它常用任务：

```bash
# 只下载版本清单与客户端 jar
./gradlew downloadMinecraft

# 只下载当前系统需要的运行库（含 natives）
./gradlew downloadLibraries

# 只下载资源索引与游戏资源（首次约 484MB）
./gradlew downloadAssets

# 只构建 Agent jar，不启动游戏
./gradlew jar

# 打印完整的启动命令（不实际启动），便于排查问题
./gradlew printRunArgs
```

也可以直接在命令行覆盖 `gradle.properties` 中的配置：

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## 目录结构

游戏目录（`--gamedir`，默认为当前工作目录）下，Flint 会自动创建：

```
<游戏目录>/
├── mods/            # Modsrt 模组目录，放入 *.jar 即可加载
├── pubSystem/       # PubSystem 系统模块目录，放入 *.jar 即可加载
├── logs/            # 游戏日志
└── packager.txt     # 包管理文件
```

运行时（客户端 jar、依赖库、资源）存放在项目根目录的 `.flint/minecraft` 下——这是刻意放在 `build/` 之外的，否则一次 `gradle clean` 就会删掉这 550MB 的下载内容。想改回 `build/minecraft` 只需修改 `gradle.properties` 里的 `minecraftDir`。

## 编写模组

### 方式一：Modsrt 模组（游戏模组）

实现 `Modsrt` 接口，并用 `@MODS` 注解标记，Flint 扫描 `mods/` 目录时会自动实例化并调用 `onLoad()`：

```java
import org.flint.modsrt.MODS;
import org.flint.modsrt.Modsrt;

@MODS("我的模组")
public class MyMod implements Modsrt {
    @Override
    public void onLoad() {
        System.out.println("我的模组已加载！");
    }

    @Override
    public void onUnload() {
        System.out.println("我的模组已卸载");
    }
}
```

### 方式二：PubSystem 模块（系统级扩展）

实现 `PubModule` 接口，并用 `@PUBCOM` 注解标记，放入 `pubSystem/` 目录即可：

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
        // 拿到 Instrumentation 后，可自行注册 ClassFileTransformer，
        // 拦截并修改任意游戏类的字节码，无需修改 Flint 本体
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` 会拿到 `Instrumentation` 实例，因此模块可以直接注册自己的 `ClassFileTransformer`——这正是 Flint 实现“插件式”的关键。

### 方式三：Mixin

Flint 已内建 Mixin 引导，模组可以照常使用 Mixin 注解。Flint 自带的配置文件为 `flint.mixins.json`（`compatibilityLevel: JAVA_25`），其中的示例注入位于 `org.flint.mixin.TitleScreenTransformer`。

## 配置项

`gradle.properties` 中的常用配置（命令行参数优先级更高）：

| 键 | 默认值 | 说明 |
|---|:---:|-----|
| `minecraft_version` | `26.3` | 目标 Minecraft 版本 |
| `version` | `26.2-1.22.2-0.1.0` | Flint Agent jar 版本号 |
| `minecraftDir` | `.flint/minecraft` | 运行时目录（client.jar / libraries / assets） |
| `offlinePlayerName` | `Dev` | `runClient` 使用的离线账号名 |
| `windowWidth` / `windowHeight` | （注释） | 取消注释后强制窗口尺寸 |
| `assetDownloadThreads` | `8` | 首次下载资源的并发线程数 |
| `downloadRetries` | `4` | 下载失败重试次数 |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP 连接 / 读取超时 |

## 项目结构

```
Flint/
├── src/main/java/org/flint/        # Flint 主体
│   ├── AgentMain.java              # Java Agent 入口（premain）
│   ├── modsrt/                     # Modsrt 模组加载器
│   ├── pubsystem/                  # PubSystem 系统模块加载器
│   ├── dirpath/                    # 目录结构初始化
│   ├── mixin/                      # 内置示例 Mixin
│   └── mixinservice/               # Mixin 服务适配
├── src/main/resources/             # 资源、Agent Manifest、Mixin 配置
├── Mixin/                          # SpongePowered Mixin 源码
├── buildSrc/                        # 构建脚本
├── build.gradle                     # 构建与运行任务定义
└── gradle.properties                # 版本与运行时配置
```

## 故障排查

- **启动器检测不到 Flint**：见上方“已知问题”，目前需使用原版 26.3 启动器。
- **`client.jar is missing`**：先执行 `./gradlew downloadMinecraft`。
- **`missing ... libraries`**：先执行 `./gradlew downloadLibraries`。
- **Java 版本报错**：把 Gradle 的 JVM 切换到 Java 25。
- **想看启动时到底执行了什么**：运行 `./gradlew printRunArgs`，它会打印完整命令而不启动游戏。

## 致谢

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) —— 字节码注入框架，Flint 的 Mixin 能力基于它实现
- [ASM](https://asm.ow2.io/) —— 底层字节码操作库

---

💡 欢迎提交 Issue 和 PR，帮助 Flint 变得更好。
> ⚠️ 本 README 为翻译版本，内容不一定准确。如有出入，请以 English 版本为准：[English](../README.md)
