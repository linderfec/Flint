<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Pemuat mod gratis, sumber terbuka, dan berbasis plugin untuk Minecraft
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

## Tentang Flint

Flint bukan solusi tradisional "gantikan peluncur Anda": Flint menempel pada
proses Minecraft asli (vanilla) sebagai **agen Java** (`-javaagent`) dan mengambil
alih pemuatan kelas sesaat mungkin, sehingga mod dan modul dapat dimuat sementara
Anda tetap menggunakan peluncur asli.

> ⚠️ **Masalah yang diketahui**
> Untuk saat ini Anda harus menggunakan **peluncur asli (vanilla) 26.3** agar
> pemuat mod Flint terdeteksi. (Alasan pastinya belum diketahui.)

### Fitur

- 🧩 **Arsitektur plugin** — mod dan modul sistem adalah jar mandiri; cukup
  letakkan ke dalam sebuah folder dan file akan terdeteksi, tanpa perubahan apa
  pun pada Flint sendiri
- 🪝 **Dukungan Mixin** — menyertakan dan mem-bootstrap SpongePowered Mixin
  sehingga Anda dapat menyuntikkan dan menulis ulang bytecode dari kelas game
  mana pun
- ⚙️ **Berbasis Java Agent** — dibangun di atas `Instrumentation`, dengan dukungan
  untuk definisi ulang dan transformasi ulang kelas
- 🚀 **Runtime mandiri** — tugas Gradle mengunduh jar klien, pustaka, dan aset
  untuk Anda; tidak perlu HMCL atau peluncur lain
- 📦 **Dua sistem modul** — `Modsrt` (mod) dan `PubSystem` (modul sistem) terpisah
  dan tidak pernah saling mengganggu

### Versi game yang didukung

| Dukungan | Versi |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Prasyarat

| Item | Persyaratan |
|-----|------|
| JDK | **Java 25** (persyaratan mutlak untuk 26.3; Gradle dan game berbagi JVM yang sama) |
| Build | Gradle Wrapper (`./gradlew`, tidak perlu instalasi Gradle terpisah) |
| Jaringan | Build pertama mengunduh klien, pustaka, dan aset (~550MB+) |
| Disk | Runtime berada di `.flint/minecraft`, sediakan sekitar 1GB ruang kosong |

## Memulai cepat

```bash
# 1. Build agen Flint dan luncurkan klien (pustaka/aset yang hilang diunduh otomatis)
./gradlew runClient
```

`runClient` akan: membangun `Flint-<version>.jar` → mengunduh pustaka dan aset →
menjalankan game dengan akun luring (offline) `Dev` dan memasang Flint sebagai
agen Java.

Tugas berguna lainnya:

```bash
# Hanya mengunduh manifes versi dan client.jar
./gradlew downloadMinecraft

# Hanya mengunduh pustaka runtime yang dibutuhkan oleh OS saat ini (termasuk native)
./gradlew downloadLibraries

# Hanya mengunduh indeks aset dan sumber daya game (run pertama ~484MB)
./gradlew downloadAssets

# Hanya membangun jar agen, jangan menjalankan game
./gradlew jar

# Mencetak perintah peluncuran lengkap tanpa menjalankan game, berguna untuk debugging
./gradlew printRunArgs
```

Anda juga dapat menimpa apa pun di `gradle.properties` dari baris perintah:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Tata letak direktori

Di direktori game (`--gamedir`, secara bawaan adalah direktori kerja saat ini)
Flint membuat hal berikut untuk Anda:

```
<game dir>/
├── mods/            # Folder mod Modsrt, letakkan *.jar di sini untuk memuatnya
├── pubSystem/       # Folder modul PubSystem, letakkan *.jar di sini untuk memuatnya
├── logs/            # Log game
└── packager.txt     # Berkas manajemen paket
```

Runtime (jar klien, pustaka, aset) disimpan di `.flint/minecraft` pada akar
proyek — sengaja diletakkan di luar `build/`, karena jika tidak, satu kali
`gradle clean` akan menghapus unduhan ~550MB. Kembalikan ke `build/minecraft`
dengan mengubah `minecraftDir` di `gradle.properties`.

## Menulis mod

### Opsi 1: Mod Modsrt (mod dalam game)

Implementasikan antarmuka `Modsrt` dan beri anotasi pada kelas dengan `@MODS`.
Ketika Flint memindai folder `mods/`, Flint membuat instansi kelas tersebut dan
memanggil `onLoad()`:

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

### Opsi 2: Modul PubSystem (ekstensi tingkat sistem)

Implementasikan antarmuka `PubModule`, beri tanda pada kelas dengan anotasi
`@PUBCOM`, dan letakkan jar ke dalam `pubSystem/`:

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
        // Dengan instance Instrumentation Anda dapat mendaftarkan
        // ClassFileTransformer milik Anda sendiri dan menulis ulang
        // kelas game mana pun, tanpa menyentuh kode Flint sendiri.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` memberikan Anda sebuah instance `Instrumentation`, sehingga sebuah modul
dapat mendaftarkan `ClassFileTransformer` miliknya sendiri — inilah yang membuat
Flint berbasis plugin.

### Opsi 3: Mixin

Flint mem-bootstrap Mixin sejak awal, sehingga mod dapat menggunakan anotasi
Mixin seperti biasa. Berkas konfigurasi Flint sendiri adalah
`flint.mixins.json` (`compatibilityLevel: JAVA_25`), dan contoh injeksi berada di
`org.flint.mixin.TitleScreenTransformer`.

## Konfigurasi

Kunci umum di `gradle.properties` (nilai baris perintah selalu menang):

| Kunci | Bawaan | Deskripsi |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Versi Minecraft target |
| `version` | `26.2-1.22.2-0.1.0` | Versi jar agen Flint |
| `minecraftDir` | `.flint/minecraft` | Direktori runtime (client.jar / pustaka / aset) |
| `offlinePlayerName` | `Dev` | Nama akun luring (offline) yang digunakan oleh `runClient` |
| `windowWidth` / `windowHeight` | (dikomentari) | Hapus tanda komentar untuk memaksa ukuran jendela |
| `assetDownloadThreads` | `8` | Jumlah thread untuk unduhan aset pertama |
| `downloadRetries` | `4` | Jumlah percobaan ulang untuk unduhan yang gagal |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | Batas waktu sambung / baca HTTP |

## Struktur proyek

```
Flint/
├── src/main/java/org/flint/        # Inti Flint
│   ├── AgentMain.java              # Titik masuk agen Java (premain)
│   ├── modsrt/                     # Pemuat mod Modsrt
│   ├── pubsystem/                  # Pemuat modul PubSystem
│   ├── dirpath/                    # Bootstrap tata letak direktori
│   ├── mixin/                      # Mixin contoh bawaan
│   └── mixinservice/               # Adapter layanan Mixin
├── src/main/resources/             # Sumber daya, manifes agen, konfigurasi Mixin
├── Mixin/                          # Sumber SpongePowered Mixin
├── buildSrc/                        # Skrip build
├── build.gradle                     # Definisi tugas build dan run
└── gradle.properties                # Konfigurasi versi dan runtime
```

## Pemecahan masalah

- **Peluncur tidak mendeteksi Flint**: lihat "Masalah yang diketahui" di atas —
  peluncur asli (vanilla) 26.3 diperlukan untuk saat ini.
- **`client.jar is missing`**: jalankan `./gradlew downloadMinecraft` terlebih
  dahulu.
- **`missing ... libraries`**: jalankan `./gradlew downloadLibraries` terlebih
  dahulu.
- **Kesalahan versi Java**: arahkan JVM Gradle ke Java 25.
- **Ingin melihat apa yang benar-benar dijalankan**: `./gradlew printRunArgs`
  mencetak perintah lengkap tanpa menjalankan game.

## Penghargaan

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — kerangka
  injeksi bytecode yang menjadi dasar dukungan Mixin Flint
- [ASM](https://asm.ow2.io/) — pustaka manipulasi bytecode tingkat rendah

---

💡 Masalah dan PR sangat membantu, kontribusi tersebut membantu Flint menjadi
lebih baik.
> ⚠️ README ini merupakan terjemahan dan mungkin tidak sepenuhnya akurat. Jika menemukan perbedaan, silakan merujuk pada versi bahasa Inggris: [English](../README.md)
