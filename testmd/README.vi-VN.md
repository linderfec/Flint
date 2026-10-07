<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Trình tải mod (mod loader) miễn giấy phép, mã nguồn mở, dựa trên plugin cho Minecraft
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

## Về Flint

Flint không phải là giải pháp kiểu "thay thế trình khởi chạy" thông thường: nó
gắn vào một tiến trình Minecraft gốc dưới dạng **đại lý Java (Java Agent)**
(`-javaagent`) và tiếp quản việc nạp lớp sớm nhất có thể, nhờ vậy các mod và
mô-đun có thể được tải trong khi bạn vẫn tiếp tục dùng trình khởi chạy gốc.

> ⚠️ **Vấn đề đã biết**
> Hiện tại bạn phải dùng **trình khởi chạy gốc bản 26.3** để trình tải Flint được
> phát hiện. (Lý do chính xác vẫn chưa được xác định.)

### Tính năng

- 🧩 **Kiến trúc plugin** — các mod và mô-đun hệ thống là các jar độc lập; chỉ cần
  bỏ chúng vào một thư mục là chúng được nạp, không cần thay đổi gì Flint
- 🪝 **Hỗ trợ Mixin** — kèm sẵn và khởi tạo SpongePowered Mixin để bạn có thể
  chèn và viết lại mã bytecode của bất kỳ lớp game nào
- ⚙️ **Dựa trên đại lý Java (Java Agent)** — được xây dựng trên `Instrumentation`,
  hỗ trợ định nghĩa lại và biến đổi lại lớp
- 🚀 **Runtime tự chứa** — các tác vụ Gradle tải về client jar, thư viện và tài
  nguyên cho bạn; không cần HMCL hay trình khởi chạy nào khác
- 📦 **Hai hệ thống mô-đun** — `Modsrt` (mod) và `PubSystem` (mô-đun hệ thống)
  tách biệt và không bao giờ cản trở lẫn nhau

### Phiên bản game được hỗ trợ

| Hỗ trợ | Phiên bản |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Yêu cầu

| Hạng mục | Yêu cầu |
|-----|------|
| JDK | **Java 25** (bắt buộc đối với 26.3; Gradle và game dùng chung một JVM) |
| Build | Gradle Wrapper (`./gradlew`, không cần cài đặt Gradle riêng) |
| Mạng | Lần build đầu tiên tải client, thư viện và tài nguyên (~550MB+) |
| Ổ đĩa | Runtime nằm trong `.flint/minecraft`, hãy giữ khoảng 1GB trống |

## Bắt đầu nhanh

```bash
# 1. Build đại lý Flint và khởi chạy client (thư viện/tài nguyên thiếu sẽ được tải tự động)
./gradlew runClient
```

`runClient` sẽ: build `Flint-<version>.jar` → tải thư viện và tài nguyên →
khởi chạy game với tài khoản ngoại tuyến `Dev` và gắn Flint làm đại lý Java (Java Agent).

Một số tác vụ hữu ích khác:

```bash
# Chỉ tải version manifest và client.jar
./gradlew downloadMinecraft

# Chỉ tải các thư viện runtime mà hệ điều hành hiện tại cần (gồm cả natives)
./gradlew downloadLibraries

# Chỉ tải asset index và tài nguyên của game (lần đầu ~484MB)
./gradlew downloadAssets

# Chỉ build jar của đại lý, không khởi chạy game
./gradlew jar

# In ra toàn bộ lệnh khởi chạy mà không chạy game, hữu ích khi gỡ lỗi
./gradlew printRunArgs
```

Bạn cũng có thể ghi đè bất kỳ giá trị nào trong `gradle.properties` từ dòng lệnh:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Cấu trúc thư mục

Trong thư mục game (`--gamedir`, mặc định là thư mục làm việc hiện tại)
Flint tạo ra cho bạn các thứ sau:

```
<game dir>/
├── mods/            # Thư mục mod của Modsrt, bỏ *.jar vào đây để nạp chúng
├── pubSystem/       # Thư mục mô-đun của PubSystem, bỏ *.jar vào đây để nạp chúng
├── logs/            # Nhật ký game
└── packager.txt     # Tệp quản lý gói
```

Runtime (client jar, thư viện, tài nguyên) được lưu trong `.flint/minecraft` ở
thư mục gốc dự án — cố ý đặt bên ngoài `build/`, nếu không một lần
`gradle clean` đơn giản sẽ xóa sạch ~550MB đã tải về. Đổi lại thành
`build/minecraft` bằng cách sửa `minecraftDir` trong `gradle.properties`.

## Viết mod

### Cách 1: Mod Modsrt (mod trong game)

Triển khai giao diện `Modsrt` và thêm chú thích `@MODS` cho lớp. Khi Flint quét
thư mục `mods/`, nó sẽ khởi tạo lớp đó và gọi `onLoad()`:

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

### Cách 2: Mô-đun PubSystem (phần mở rộng ở cấp hệ thống)

Triển khai giao diện `PubModule`, đánh dấu lớp bằng chú thích `@PUBCOM` và bỏ
jar vào thư mục `pubSystem/`:

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
        // Với thể hiện Instrumentation, bạn có thể đăng ký
        // ClassFileTransformer của riêng mình và viết lại bất kỳ
        // lớp game nào, mà không đụng vào mã của Flint.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` trao cho bạn một thể hiện `Instrumentation`, nhờ vậy một mô-đun có thể đăng ký
`ClassFileTransformer` của riêng mình — đây chính là điều khiến Flint theo plugin.

### Cách 3: Mixin

Flint khởi tạo Mixin sẵn ngay từ đầu, nên các mod có thể dùng các chú thích
Mixin như bình thường. Tệp cấu hình của chính Flint là `flint.mixins.json`
(`compatibilityLevel: JAVA_25`), và ví dụ tiêm nằm trong
`org.flint.mixin.TitleScreenTransformer`.

## Cấu hình

Các khóa phổ biến trong `gradle.properties` (giá trị từ dòng lệnh luôn thắng):

| Khóa | Mặc định | Mô tả |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Phiên bản Minecraft mục tiêu |
| `version` | `26.2-1.22.2-0.1.0` | Phiên bản của jar đại lý Flint |
| `minecraftDir` | `.flint/minecraft` | Thư mục runtime (client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | Tên tài khoản ngoại tuyến được `runClient` dùng |
| `windowWidth` / `windowHeight` | (đang bị comment) | Bỏ comment để ép kích thước cửa sổ |
| `assetDownloadThreads` | `8` | Số luồng cho lượt tải tài nguyên đầu tiên |
| `downloadRetries` | `4` | Số lần thử lại khi tải thất bại |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Thời gian chờ kết nối / đọc HTTP |

## Cấu trúc dự án

```
Flint/
├── src/main/java/org/flint/        # Lõi Flint
│   ├── AgentMain.java              # Điểm vào của java agent (premain)
│   ├── modsrt/                     # Trình tải mod Modsrt
│   ├── pubsystem/                  # Trình tải mô-đun PubSystem
│   ├── dirpath/                    # Khởi tạo cấu trúc thư mục
│   ├── mixin/                      # Ví dụ mixin tích hợp sẵn
│   └── mixinservice/               # Bộ chuyển dịch dịch vụ Mixin
├── src/main/resources/             # Tài nguyên, manifest của agent, cấu hình mixin
├── Mixin/                          # Mã nguồn SpongePowered Mixin
├── buildSrc/                        # Script build
├── build.gradle                     # Định nghĩa tác vụ build và chạy
└── gradle.properties                # Phiên bản và cấu hình runtime
```

## Xử lý sự cố

- **Trình khởi chạy không phát hiện Flint**: xem "Vấn đề đã biết" ở trên — hiện
  vẫn cần trình khởi chạy gốc bản 26.3.
- **`client.jar is missing`**: hãy chạy `./gradlew downloadMinecraft` trước.
- **`missing ... libraries`**: hãy chạy `./gradlew downloadLibraries` trước.
- **Lỗi phiên bản Java**: trỏ JVM của Gradle về Java 25.
- **Muốn xem thực sự chạy những gì**: `./gradlew printRunArgs` in ra toàn bộ
  lệnh mà không khởi chạy game.

## Ghi công

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — khung tiêm mã
  bytecode mà tính năng Mixin của Flint được xây dựng trên đó
- [ASM](https://asm.ow2.io/) — thư viện thao tác mã bytecode ở cấp thấp

---

💡 Rất hoan nghênh các issue và PR, chúng giúp Flint tốt hơn.
> ⚠️ README này là bản dịch và có thể chưa hoàn toàn chính xác. Nếu phát hiện sai lệch, vui lòng tham khảo phiên bản tiếng Anh: [English](../README.md)
