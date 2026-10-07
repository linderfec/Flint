<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  Minecraft için ücretsiz, açık kaynaklı, eklenti tabanlı bir mod yükleyici
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

## Flint Hakkında

Flint, "başlatıcınızı değiştirin" şeklinde geleneksel bir çözüm değildir: Orijinal bir
Minecraft sürecine bir **Java ajanı** (`-javaagent`) olarak bağlanır ve sınıf
yüklemeyi mümkün olan en erken anda üzerine alır; böylece orijinal başlatıcıyı
kullanmaya devam ederken modlar ve modüller yüklenebilir.

> ⚠️ **Bilinen sorun**
> Şimdilik Flint yükleyicisinin algılanabilmesi için **orijinal 26.3 başlatıcısını**
> kullanmanız gerekir. (Kesin neden henüz bilinmiyor.)

### Özellikler

- 🧩 **Eklenti mimarisi** — modlar ve sistem modülleri bağımsız jar dosyalarıdır; bir
  klasöre atın, kendiliğinden yüklenirler, Flint üzerinde hiçbir değişiklik gerekmez
- 🪝 **Mixin desteği** — SpongePowered Mixin'i paketler ve başlatır; böylece herhangi
  bir oyun sınıfının bayt koduna enjeksiyon yapabilir ve onu yeniden yazabilirsiniz
- ⚙️ **Java ajanı tabanlı** — `Instrumentation` üzerine kuruludur; sınıf
  yeniden tanılama ve yeniden dönüştürme desteklenir
- 🚀 **Kendinden bağımsız çalışma zamanı** — Gradle görevleri istemci jar dosyasını,
  kütüphaneleri ve varlıkları (assets) sizin için indirir; HMCL veya başka bir
  başlatıcıya gerek yoktur
- 📦 **İki modül sistemi** — `Modsrt` (modlar) ve `PubSystem` (sistem modülleri)
  birbirinden ayrıdır ve birbirinin yoluna asla girmez

### Desteklenen oyun sürümleri

| Destek | Sürüm |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## Gereksinimler

| Öğe | Gereksinim |
|-----|------|
| JDK | **Java 25** (26.3 için zorunlu; Gradle ve oyun aynı JVM'yi paylaşır) |
| Derleme | Gradle Wrapper (`./gradlew`, ayrı bir Gradle kurulumu gerekmez) |
| Ağ | İlk derleme istemciyi, kütüphaneleri ve varlıkları indirir (~550MB+) |
| Disk | Çalışma zamanı `.flint/minecraft` içinde bulunur, yaklaşık 1GB boş alan bırakın |

## Hızlı başlangıç

```bash
# 1. Flint ajanını derleyin ve istemciyi başlatın (eksik kütüphaneler/varlıklar otomatik olarak indirilir)
./gradlew runClient
```

`runClient` şunları yapar: `Flint-<version>.jar` dosyasını derler → kütüphaneleri ve
varlıkları indirir → oyunu `Dev` çevrimdışı hesabı ile başlatır ve Flint'i bir Java
ajanı olarak bağlar.

Diğer yararlı görevler:

```bash
# Yalnızca sürüm manifestesini ve client.jar dosyasını indir
./gradlew downloadMinecraft

# Yalnızca geçerli işletim sisteminin gerektirdiği çalışma zamanı kütüphanelerini indir (yerel bileşenler dahil)
./gradlew downloadLibraries

# Yalnızca varlık dizinini ve oyun kaynaklarını indir (ilk çalıştırma ~484MB)
./gradlew downloadAssets

# Yalnızca ajan jar dosyasını derle, oyunu başlatma
./gradlew jar

# Oyunu başlatmadan tam başlatma komutunu yazdır, hata ayıklama için kullanışlıdır
./gradlew printRunArgs
```

Ayrıca `gradle.properties` içindeki her şeyi komut satırından geçersiz kılabilirsiniz:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## Dizin düzeni

Oyun dizininde (`--gamedir`, varsayılan olarak geçerli çalışma dizini) Flint sizin
için şunları oluşturur:

```
<game dir>/
├── mods/            # Modsrt mod klasörü, yüklemek için *.jar dosyalarını buraya atın
├── pubSystem/       # PubSystem modül klasörü, yüklemek için *.jar dosyalarını buraya atın
├── logs/            # Oyun günlükleri
└── packager.txt     # Paket yönetim dosyası
```

Çalışma zamanı (istemci jar dosyası, kütüphaneler, varlıklar) proje kökündeki
`.flint/minecraft` içinde saklanır — bunun amacı bilinçli olarak `build/` dışında
tutmaktır, aksi halde tek bir `gradle clean` ~550MB'lık indirmeyi silerdi. Bunu geri
`build/minecraft` yapmak için `gradle.properties` içindeki `minecraftDir` anahtarını
düzenleyin.

## Mod yazma

### Seçenek 1: Modsrt modları (oyun içi modlar)

`Modsrt` arayüzünü uygulayın ve sınıfa `@MODS` bildirimini ekleyin. Flint `mods/`
klasörünü taradığında sınıfı örnekler ve `onLoad()` çağırır:

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

### Seçenek 2: PubSystem modülleri (sistem düzeyi uzantılar)

`PubModule` arayüzünü uygulayın, sınıfa `@PUBCOM` bildirimini ekleyin ve jar
dosyasını `pubSystem/` klasörüne atın:

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
        // Instrumentation örneği ile kendi ClassFileTransformer sınıfınızı
        // kaydedebilir ve Flint'in kendi koduna dokunmadan herhangi bir
        // oyun sınıfını yeniden yazabilirsiniz.
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` size bir `Instrumentation` örneği verir; böylece bir modül kendi
`ClassFileTransformer` sınıfını kaydedebilir — Flint'i eklenti tabanlı yapan da
budur.

### Seçenek 3: Mixin

Flint, Mixin'i hazır olarak başlatır; bu nedenle modlar her zamanki gibi Mixin
bildirimlerini kullanabilir. Flint'in kendi yapılandırma dosyası
`flint.mixins.json` (`compatibilityLevel: JAVA_25`) olup örnek enjeksiyon
`org.flint.mixin.TitleScreenTransformer` sınıfında yer alır.

## Yapılandırma

`gradle.properties` içindeki yaygın anahtarlar (komut satırı değerleri her zaman
geçerlidir):

| Anahtar | Varsayılan | Açıklama |
|---|:---:|-----|
| `minecraft_version` | `26.3` | Hedef Minecraft sürümü |
| `version` | `26.2-1.22.2-0.1.0` | Flint ajan jar dosyasının sürümü |
| `minecraftDir` | `.flint/minecraft` | Çalışma zamanı dizini (client.jar / kütüphaneler / varlıklar) |
| `offlinePlayerName` | `Dev` | `runClient` tarafından kullanılan çevrimdışı hesap adı |
| `windowWidth` / `windowHeight` | (yorum satırında) | Pencere boyutunu zorlamak için yorum satırını kaldırın |
| `assetDownloadThreads` | `8` | İlk varlık indirme turu için iş parçacığı sayısı |
| `downloadRetries` | `4` | Başarısız indirmeler için yeniden deneme sayısı |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | HTTP bağlanma / okuma zaman aşımı |

## Proje yapısı

```
Flint/
├── src/main/java/org/flint/        # Flint çekirdeği
│   ├── AgentMain.java              # Java ajanı giriş noktası (premain)
│   ├── modsrt/                     # Modsrt mod yükleyicisi
│   ├── pubsystem/                  # PubSystem modül yükleyicisi
│   ├── dirpath/                    # Dizin düzeni başlatma (bootstrap)
│   ├── mixin/                      # Dahili örnek mixin
│   └── mixinservice/               # Mixin hizmeti dönüştürücüleri
├── src/main/resources/             # Kaynaklar, ajan manifestesi, mixin yapılandırması
├── Mixin/                          # SpongePowered Mixin kaynakları
├── buildSrc/                        # Derleme betikleri
├── build.gradle                     # Derleme ve çalıştırma görevi tanımları
└── gradle.properties                # Sürüm ve çalışma zamanı yapılandırması
```

## Sorun giderme

- **Başlatıcı Flint'i algılamıyor**: yukarıdaki "Bilinen sorun" bölümüne bakın —
  şu anda orijinal 26.3 başlatıcısı gereklidir.
- **`client.jar is missing`**: önce `./gradlew downloadMinecraft` komutunu çalıştırın.
- **`missing ... libraries`**: önce `./gradlew downloadLibraries` komutunu çalıştırın.
- **Java sürümü hatası**: Gradle'ın JVM'ini Java 25'e yönlendirin.
- **Gerçekte ne çalıştığını görmek istiyorsanız**: `./gradlew printRunArgs`, oyunu
  başlatmadan tam komutu yazdırır.

## Teşekkürler

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — Flint'in Mixin
  desteğinin üzerine kurulduğu bayt kodu enjeksiyonu çerçevesi
- [ASM](https://asm.ow2.io/) — düşük düzey bayt kodu işleme kütüphanesi

---

💡 Sorun ve PR'lar hoştur, Flint'in daha iyi olmasına yardımcı olurlar.
> ⚠️ Bu README bir çeviridir ve tam olarak doğru olmayabilir. Çelişki durumunda İngilizce sürüme bakınız: [English](../README.md)
