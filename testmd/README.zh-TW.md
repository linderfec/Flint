<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  一個自由、開源、以插件為基礎的 Minecraft 模組載入器
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

## 關於 Flint

Flint 不是傳統的「取代你的啟動器」解決方案：它以 **Java 代理（Java Agent）**
（`-javaagent`）的形式附加到原版 Minecraft 進程上，並在盡可能最早的時刻
接管類別載入，讓模組與系統模組得以載入，同時你仍可繼續使用
原本的啟動器。

> ⚠️ **已知問題**
> 目前你必須使用 **原版 26.3 啟動器**，Flint 載入器才能被偵測到。
> （確切原因目前尚不清楚。）

### 功能特色

- 🧩 **插件架構** — 模組與系統模組皆為獨立的 jar 檔；把它們放進
  資料夾就會自動載入，無需修改 Flint 本身
- 🪝 **Mixin 支援** — 內建並啟動 SpongePowered Mixin，讓你可以注入
  並改寫任何遊戲類別的位元組碼
- ⚙️ **以 Java Agent 為基礎** — 建構於 `Instrumentation` 之上，支援類別
  的重新定義與重新轉換
- 🚀 **自帶執行環境** — Gradle 任務會為你下載用戶端 jar 檔、函式庫
  與資源檔；不需要 HMCL 或其他啟動器
- 📦 **兩套模組系統** — `Modsrt`（模組）與 `PubSystem`（系統模組）
  彼此獨立，互不干擾

### 支援的遊戲版本

| 支援 | 版本 |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## 需求

| 項目 | 需求 |
|-----|------|
| JDK | **Java 25**（26.3 的硬性需求；Gradle 與遊戲共用同一個 JVM） |
| 建置 | Gradle Wrapper（`./gradlew`，無需另行安裝 Gradle） |
| 網路 | 首次建置會下載用戶端、函式庫與資源檔（約 550MB+） |
| 磁碟 | 執行環境位於 `.flint/minecraft`，請保留約 1GB 可用空間 |

## 快速開始

```bash
# 1. 建置 Flint 代理並啟動用戶端（缺少的函式庫／資源檔會自動下載）
./gradlew runClient
```

`runClient` 會：建置 `Flint-<version>.jar` → 下載函式庫與資源檔 →
以離線帳號 `Dev` 啟動遊戲，並將 Flint 掛載為 Java 代理（Java Agent）。

其他實用的任務：

```bash
# 只下載版本資訊清單與 client.jar
./gradlew downloadMinecraft

# 只下載目前作業系統所需的執行函式庫（包含原生庫）
./gradlew downloadLibraries

# 只下載資源索引與遊戲資源（首次執行約 484MB）
./gradlew downloadAssets

# 只建置代理 jar 檔，不啟動遊戲
./gradlew jar

# 不啟動遊戲，僅印出完整啟動指令，方便除錯
./gradlew printRunArgs
```

你也可以從指令列覆寫 `gradle.properties` 中的任何設定：

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## 目錄結構

在遊戲目錄（`--gamedir`，預設為目前的工作目錄）中，
Flint 會為你建立以下內容：

```
<game dir>/
├── mods/            # Modsrt 模組資料夾，把 *.jar 放進來即可載入
├── pubSystem/       # PubSystem 模組資料夾，把 *.jar 放進來即可載入
├── logs/            # 遊戲日誌
└── packager.txt     # 套件管理檔案
```

執行環境（用戶端 jar 檔、函式庫、資源檔）存放在專案根目錄的
`.flint/minecraft` — 刻意保持在 `build/` 之外，否則一次
`gradle clean` 就會清除約 550MB 的下載內容。若要改回
`build/minecraft`，請編輯 `gradle.properties` 中的 `minecraftDir`。

## 編寫模組

### 選項 1：Modsrt 模組（遊戲內模組）

實作 `Modsrt` 介面，並在類別上加上 `@MODS` 註解。當 Flint 掃描
`mods/` 資料夾時，會實例化該類別並呼叫 `onLoad()`：

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

### 選項 2：PubSystem 模組（系統層級擴充）

實作 `PubModule` 介面，以 `@PUBCOM` 註解標記該類別，
並將 jar 檔放入 `pubSystem/`：

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
        // 有了 Instrumentation 實例，你就能註冊自己的
        // ClassFileTransformer，改寫任何遊戲類別，
        // 而不必更動 Flint 本身的程式碼。
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` 會交給你一個 `Instrumentation` 實例，因此模組可以註冊
自己的 `ClassFileTransformer` — 這正是 Flint 以插件為基礎的關鍵。

### 選項 3：Mixin

Flint 開箱即用便會啟動 Mixin，因此模組可以照常使用 Mixin 註解。
Flint 自身的設定檔是 `flint.mixins.json`
（`compatibilityLevel: JAVA_25`），範例注入位於
`org.flint.mixin.TitleScreenTransformer`。

## 設定

`gradle.properties` 中的常用鍵值（指令列的值一律優先）：

| 鍵值 | 預設值 | 說明 |
|---|:---:|-----|
| `minecraft_version` | `26.3` | 目標 Minecraft 版本 |
| `version` | `26.2-1.22.2-0.1.0` | Flint 代理 jar 檔的版本 |
| `minecraftDir` | `.flint/minecraft` | 執行目錄（client.jar／函式庫／資源檔） |
| `offlinePlayerName` | `Dev` | `runClient` 使用的離線帳號名稱 |
| `windowWidth` / `windowHeight` | （已註解） | 取消註解即可強制視窗大小 |
| `assetDownloadThreads` | `8` | 首次下載資源檔時使用的執行緒數 |
| `downloadRetries` | `4` | 下載失敗時的重試次數 |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP 連線／讀取逾時 |

## 專案結構

```
Flint/
├── src/main/java/org/flint/        # Flint 核心
│   ├── AgentMain.java              # Java 代理進入點（premain）
│   ├── modsrt/                     # Modsrt 模組載入器
│   ├── pubsystem/                  # PubSystem 模組載入器
│   ├── dirpath/                    # 目錄結構初始化
│   ├── mixin/                      # 內建範例 mixin
│   └── mixinservice/               # Mixin 服務轉接器
├── src/main/resources/             # 資源檔、代理 manifest、mixin 設定
├── Mixin/                          # SpongePowered Mixin 原始碼
├── buildSrc/                        # 建置腳本
├── build.gradle                     # 建置與執行任務定義
└── gradle.properties                # 版本與執行環境設定
```

## 疑難排解

- **啟動器偵測不到 Flint**：請見上方的「已知問題」— 目前需要
  原版 26.3 啟動器。
- **`client.jar is missing`**：請先執行 `./gradlew downloadMinecraft`。
- **`missing ... libraries`**：請先執行 `./gradlew downloadLibraries`。
- **Java 版本錯誤**：請將 Gradle 使用的 JVM 指向 Java 25。
- **想查看實際執行的內容**：`./gradlew printRunArgs` 會在不啟動
  遊戲的情況下印出完整指令。

## 致謝

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — Flint 的
  Mixin 支援所建構的位元組碼注入框架
- [ASM](https://asm.ow2.io/) — 底層的位元組碼操作函式庫

---

💡 歡迎提出 Issue 與 PR，它們能讓 Flint 變得更好。
> ⚠️ 本 README 為翻譯版本，內容不一定準確。如有出入，請以 English 版本為準：[English](../README.md)
