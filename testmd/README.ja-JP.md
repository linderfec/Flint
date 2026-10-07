<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Minecraft 向けの、無料・オープンソースのプラグインベースのモッドローダー
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

## Flint について

Flint は従来型の「ランチャーごと入れ替える」ソリューションではありません。バニラの Minecraft プロセスに **Java エージェント**(`-javaagent`)としてアタッチし、可能な限り早期にクラスローディングを引き継ぐことで、従来のランチャーをそのまま使い続けながら、モッドやモジュールを読み込めるようにします。

> ⚠️ **既知の問題**
> 現時点では、Flint ローダーを検出するために **バニラの 26.3 ランチャー**を使用する必要があります。(正確な理由はまだ分かっていません。)

### 特徴

- 🧩 **プラグインアーキテクチャ** — モッドとシステムモジュールは単体の jar です。フォルダに放り込むだけで読み込まれ、Flint 本体を変更する必要はありません
- 🪝 **ミクシン対応** — SpongePowered Mixin を同梱・ブートストラップするため、任意のゲームクラスのバイトコードに注入・書き換えができます
- ⚙️ **Java エージェントベース** — `Instrumentation` 上に構築されており、クラスの再定義と再変換に対応しています
- 🚀 **自己完結型ランタイム** — Gradle タスクがクライアント jar、ライブラリ、アセットを自動的にダウンロードします。HMCL やその他のランチャーは不要です
- 📦 **2 つのモジュールシステム** — `Modsrt`(モッド)と `PubSystem`(システムモジュール)は独立しており、互いに干渉しません

### 対応ゲームバージョン

| サポート | バージョン |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## 必要環境

| 項目 | 要件 |
|-----|------|
| JDK | **Java 25**(26.3 では必須。Gradle とゲームは同じ JVM を共有します) |
| ビルド | Gradle Wrapper(`./gradlew`。Gradle の個別インストールは不要) |
| ネットワーク | 初回ビルド時にクライアント、ライブラリ、アセットをダウンロードします(約 550MB 以上) |
| ディスク | ランタイムは `.flint/minecraft` に配置されます。約 1GB の空き容量を確保してください |

## クイックスタート

```bash
# 1. Flint エージェントをビルドしてクライアントを起動します(不足しているライブラリ/アセットは自動的にダウンロードされます)
./gradlew runClient
```

`runClient` は次の処理を行います:`Flint-<version>.jar` のビルド → ライブラリとアセットのダウンロード → オフラインアカウント `Dev` でゲームを起動し、Flint を Java エージェントとしてマウントします。

その他の便利なタスク:

```bash
# バージョンマニフェストと client.jar のみをダウンロード
./gradlew downloadMinecraft

# 現在の OS に必要なランタイムライブラリ(ネイティブを含む)のみをダウンロード
./gradlew downloadLibraries

# アセットインデックスとゲームリソースのみをダウンロード(初回は約 484MB)
./gradlew downloadAssets

# エージェント jar のみをビルドし、ゲームは起動しない
./gradlew jar

# ゲームを起動せずに完全な起動コマンドを出力します(デバッグに便利)
./gradlew printRunArgs
```

`gradle.properties` の内容はコマンドラインから上書きすることもできます:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## ディレクトリ構成

ゲームディレクトリ(`--gamedir`。デフォルトはカレントワークディレクトリ)に、Flint は次の内容を作成します:

```
<game dir>/
├── mods/            # Modsrt モッドフォルダ。*.jar をここに置くと読み込まれます
├── pubSystem/       # PubSystem モジュールフォルダ。*.jar をここに置くと読み込まれます
├── logs/            # ゲームログ
└── packager.txt     # パッケージ管理ファイル
```

ランタイム(クライアント jar、ライブラリ、アセット)はプロジェクトルートの `.flint/minecraft` に保存されます。意図的に `build/` の外に置かれており、そうでなければ `gradle clean` ひとつで約 550MB のダウンロードデータが消えてしまいます。`gradle.properties` の `minecraftDir` を編集すれば `build/minecraft` に戻すこともできます。

## モッドの書き方

### 方法 1:Modsrt モッド(ゲーム内モッド)

`Modsrt` インターフェースを実装し、クラスに `@MODS` アノテーションを付けます。Flint が `mods/` フォルダをスキャンすると、そのクラスをインスタンス化して `onLoad()` を呼び出します:

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

### 方法 2:PubSystem モジュール(システムレベルの拡張)

`PubModule` インターフェースを実装し、クラスに `@PUBCOM` アノテーションを付けて、jar を `pubSystem/` に置きます:

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
        // Instrumentation インスタンスを使えば、Flint 自身のコードに
        // 手を触れずに、独自の ClassFileTransformer を登録して
        // 任意のゲームクラスを書き換えられます。
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` は `Instrumentation` インスタンスを受け取るため、モジュールは独自の `ClassFileTransformer` を登録できます。これが Flint をプラグインベースにしている仕組みです。

### 方法 3:ミクシン

Flint は同梱の Mixin をブートストラップするため、モッドはいつものようにミクシンのアノテーションを使用できます。Flint 自身の設定ファイルは `flint.mixins.json`(`compatibilityLevel: JAVA_25`)で、注入のサンプルは `org.flint.mixin.TitleScreenTransformer` にあります。

## 設定

`gradle.properties` の主要なキー(コマンドラインの値が常に優先されます):

| キー | デフォルト | 説明 |
|---|:---:|-----|
| `minecraft_version` | `26.3` | 対象の Minecraft バージョン |
| `version` | `26.2-1.22.2-0.1.0` | Flint エージェント jar のバージョン |
| `minecraftDir` | `.flint/minecraft` | ランタイムディレクトリ(client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | `runClient` が使用するオフラインアカウント名 |
| `windowWidth` / `windowHeight` | (コメントアウト済み) | コメントを外すとウィンドウサイズを強制できます |
| `assetDownloadThreads` | `8` | 初回のアセットダウンロード時のスレッド数 |
| `downloadRetries` | `4` | ダウンロード失敗時のリトライ回数 |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP の接続 / 読み取りタイムアウト |

## プロジェクト構成

```
Flint/
├── src/main/java/org/flint/        # Flint コア
│   ├── AgentMain.java              # Java エージェントのエントリポイント(premain)
│   ├── modsrt/                     # Modsrt モッドローダー
│   ├── pubsystem/                  # PubSystem モジュールローダー
│   ├── dirpath/                    # ディレクトリ構成のブートストラップ
│   ├── mixin/                      # 同梱のサンプルミクシン
│   └── mixinservice/               # ミクシンサービスアダプター
├── src/main/resources/             # リソース、エージェントマニフェスト、ミクシン設定
├── Mixin/                          # SpongePowered Mixin のソース
├── buildSrc/                        # ビルドスクリプト
├── build.gradle                     # ビルド・実行タスクの定義
└── gradle.properties                # バージョンとランタイムの設定
```

## トラブルシューティング

- **ランチャーが Flint を検出しない**: 上記の「既知の問題」を参照してください。現時点ではバニラの 26.3 ランチャーが必要です。
- **`client.jar is missing`**: 先に `./gradlew downloadMinecraft` を実行してください。
- **`missing ... libraries`**: 先に `./gradlew downloadLibraries` を実行してください。
- **Java バージョンのエラー**: Gradle の JVM を Java 25 に向けてください。
- **実際に何が実行されているか知りたい**:`./gradlew printRunArgs` は、ゲームを起動せずに完全なコマンドを出力します。

## クレジット

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — Flint のミクシン対応の基盤となっているバイトコード注入フレームワーク
- [ASM](https://asm.ow2.io/) — 低レベルのバイトコード操作ライブラリ

---

💡 Issue や PR を歓迎します。Flint の改善に役立ちます。
> ⚠️ 本 README は翻訳版であり、正確でない可能性があります。相違があった場合は English 版をご参照ください：[English](../README.md)
