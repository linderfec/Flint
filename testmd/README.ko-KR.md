<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Minecraft를 위한 무료 오픈소스 플러그인 기반 모드 로더
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

## Flint 소개

Flint은 기존의 "런처를 교체하는" 방식의 솔루션이 아닙니다. Flint은 바닐라 Minecraft 프로세스에
**Java 에이전트**(`-javaagent`)로 첨부되어 가능한 한 이른 시점에 클래스 로딩을 대신 담당하므로,
원래 런처를 그대로 사용하면서 모드와 모듈을 로드할 수 있습니다.

> ⚠️ **알려진 문제**
> 현재 Flint 로더가 감지되려면 **바닐라 26.3 런처**를 사용해야 합니다.
> (정확한 원인은 아직 확인되지 않았습니다.)

### 주요 기능

- 🧩 **플러그인 아키텍처** — 모드와 시스템 모듈은 독립적인 jar 파일입니다. 폴더에 넣기만 하면
  자동으로 로드되며, Flint 자체를 수정할 필요가 없습니다
- 🪝 **믹신 지원** — SpongePowered Mixin을 번들로 포함하고 부트스트랩하므로, 모든 게임 클래스의
  바이트코드에 주입하고 재작성할 수 있습니다
- ⚙️ **Java 에이전트 기반** — `Instrumentation` 위에 구축되었으며, 클래스 재정의(redefinition)와
  재변환(retransformation)을 지원합니다
- 🚀 **자체 완결형 런타임** — Gradle 태스크가 클라이언트 jar, 라이브러리, 에셋을 대신
  다운로드합니다. HMCL이나 다른 런처가 필요 없습니다
- 📦 **두 가지 모듈 시스템** — `Modsrt`(모드)와 `PubSystem`(시스템 모듈)은 서로 독립적이며
  서로 간섭하지 않습니다

### 지원되는 게임 버전

| 지원 | 버전 |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## 요구 사항

| 항목 | 요구 사항 |
|-----|------|
| JDK | **Java 25** (26.3의 필수 요구 사항. Gradle과 게임이 같은 JVM을 공유합니다) |
| 빌드 | Gradle Wrapper (`./gradlew`, 별도의 Gradle 설치 불필요) |
| 네트워크 | 최초 빌드 시 클라이언트, 라이브러리, 에셋을 다운로드합니다 (~550MB 이상) |
| 디스크 | 런타임은 `.flint/minecraft`에 있으므로 약 1GB의 여유 공간을 확보하세요 |

## 빠른 시작

```bash
# 1. Flint 에이전트를 빌드하고 클라이언트를 실행합니다 (누락된 라이브러리/에셋은 자동으로 다운로드됩니다)
./gradlew runClient
```

`runClient`는 `Flint-<version>.jar`를 빌드하고 → 라이브러리와 에셋을 다운로드한 뒤 →
오프라인 계정 `Dev`로 게임을 시작하며 Flint를 Java 에이전트로 마운트합니다.

유용한 다른 태스크:

```bash
# 버전 매니페스트와 client.jar만 다운로드
./gradlew downloadMinecraft

# 현재 OS에 필요한 런타임 라이브러리만 다운로드 (네이티브 포함)
./gradlew downloadLibraries

# 에셋 인덱스와 게임 리소스만 다운로드 (최초 실행 시 ~484MB)
./gradlew downloadAssets

# 에이전트 jar만 빌드하고 게임은 실행하지 않음
./gradlew jar

# 게임을 시작하지 않고 전체 실행 명령어를 출력합니다 (디버깅에 유용)
./gradlew printRunArgs
```

`gradle.properties`의 모든 값은 명령줄에서도 재정의할 수 있습니다:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## 디렉터리 구성

게임 디렉터리(`--gamedir`, 기본값은 현재 작업 디렉터리)에서
Flint은 다음 항목들을 생성합니다:

```
<game dir>/
├── mods/            # Modsrt 모드 폴더, *.jar를 여기에 넣으면 로드됩니다
├── pubSystem/       # PubSystem 모듈 폴더, *.jar를 여기에 넣으면 로드됩니다
├── logs/            # 게임 로그
└── packager.txt     # 패키지 관리 파일
```

런타임(클라이언트 jar, 라이브러리, 에셋)은 프로젝트 루트의 `.flint/minecraft`에
저장됩니다. `build/` 바깥에 일부러 보관한 것으로, 그렇지 않으면 단 한 번의
`gradle clean`으로 약 550MB의 다운로드 파일이 모두 삭제되기 때문입니다.
`gradle.properties`의 `minecraftDir`을 수정하면 `build/minecraft`로
다시 되돌릴 수 있습니다.

## 모드 만들기

### 방법 1: Modsrt 모드 (인게임 모드)

`Modsrt` 인터페이스를 구현하고 클래스에 `@MODS` 어노테이션을 붙입니다. Flint이
`mods/` 폴더를 스캔하면 해당 클래스를 인스턴스화하고 `onLoad()`를 호출합니다:

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

### 방법 2: PubSystem 모듈 (시스템 수준 확장)

`PubModule` 인터페이스를 구현하고 클래스에 `@PUBCOM` 어노테이션을 붙인 뒤,
jar 파일을 `pubSystem/`에 넣습니다:

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
        // Instrumentation 인스턴스를 사용해 직접 ClassFileTransformer를 등록하고
        // 어떤 게임 클래스든 재작성할 수 있습니다.
        // Flint 자체 코드는 건드리지 않습니다.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad`는 `Instrumentation` 인스턴스를 전달하므로, 모듈이 자체
`ClassFileTransformer`를 등록할 수 있습니다. 이것이 Flint을 플러그인 기반으로
만드는 핵심입니다.

### 방법 3: 믹신

Flint은 기본 내장으로 Mixin을 부트스트랩하므로, 모드는 평소처럼 Mixin 어노테이션을
사용할 수 있습니다. Flint 자체의 구성 파일은 `flint.mixins.json`
(`compatibilityLevel: JAVA_25`)이며, 예제 주입은
`org.flint.mixin.TitleScreenTransformer`에 있습니다.

## 구성

`gradle.properties`의 주요 키 (명령줄 값이 항상 우선합니다):

| 키 | 기본값 | 설명 |
|---|:---:|-----|
| `minecraft_version` | `26.3` | 대상 Minecraft 버전 |
| `version` | `26.2-1.22.2-0.1.0` | Flint 에이전트 jar의 버전 |
| `minecraftDir` | `.flint/minecraft` | 런타임 디렉터리 (client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | `runClient`가 사용하는 오프라인 계정 이름 |
| `windowWidth` / `windowHeight` | (주석 처리됨) | 주석을 해제하면 창 크기를 강제합니다 |
| `assetDownloadThreads` | `8` | 최초 에셋 다운로드 단계의 스레드 수 |
| `downloadRetries` | `4` | 다운로드 실패 시 재시도 횟수 |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP 연결 / 읽기 타임아웃 |

## 프로젝트 구조

```
Flint/
├── src/main/java/org/flint/        # Flint 코어
│   ├── AgentMain.java              # Java 에이전트 진입점 (premain)
│   ├── modsrt/                     # Modsrt 모드 로더
│   ├── pubsystem/                  # PubSystem 모듈 로더
│   ├── dirpath/                    # 디렉터리 구성 부트스트랩
│   ├── mixin/                      # 내장 예제 믹신
│   └── mixinservice/               # 믹신 서비스 어댑터
├── src/main/resources/             # 리소스, 에이전트 매니페스트, 믹신 구성
├── Mixin/                          # SpongePowered Mixin 소스
├── buildSrc/                        # 빌드 스크립트
├── build.gradle                     # 빌드 및 실행 태스크 정의
└── gradle.properties                # 버전 및 런타임 구성
```

## 문제 해결

- **런처가 Flint을 감지하지 못함**: 위의 "알려진 문제"를 참조하세요 — 현재로서는
  바닐라 26.3 런처가 필요합니다.
- **`client.jar is missing`**: 먼저 `./gradlew downloadMinecraft`를 실행하세요.
- **`missing ... libraries`**: 먼저 `./gradlew downloadLibraries`를 실행하세요.
- **Java 버전 오류**: Gradle의 JVM을 Java 25로 지정하세요.
- **실제로 실행되는 것을 확인하고 싶다면**: `./gradlew printRunArgs`가 게임을
  시작하지 않고 전체 명령어를 출력합니다.

## 감사의 말

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — Flint의 믹신 지원이
  구축된 바이트코드 주입 프레임워크
- [ASM](https://asm.ow2.io/) — 저수준 바이트코드 조작 라이브러리

---

💡 이슈와 PR을 환영합니다. Flint을 더 나은 프로젝트로 만들어 주세요.
> ⚠️ 이 README는 번역본으로, 정확하지 않을 수 있습니다. 차이가 있으면 English 버전을 참고하세요: [English](../README.md)
