<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  ตัวโหลดมอดสำหรับ Minecraft ที่ฟรี โอเพนซอร์ส และทำงานด้วยปลั๊กอิน
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

## เกี่ยวกับ Flint

Flint ไม่ใช่โซลูชันแบบดั้งเดิมที่ "แทนที่ตัวเริ่มเกมของคุณ": มันเข้าไปแนบกับ
กระบวนการ Minecraft เวอร์ชันดั้งเดิมในฐานะ **เอเจนต์ Java** (`-javaagent`) และเข้าควบคุม
การโหลดคลาสให้เร็วที่สุดเท่าที่จะเป็นไปได้ ดังนั้นมอดและโมดูลจึงสามารถโหลดได้ในขณะที่
คุณยังคงใช้ตัวเริ่มเกมเดิมตามปกติ

> ⚠️ **ปัญหาที่ทราบแล้ว**
> สำหรับตอนนี้ คุณต้องใช้**ตัวเริ่มเกมเวอร์ชันดั้งเดิม 26.3** เพื่อให้ตัวโหลด Flint ตรวจพบได้
> (ยังไม่ทราบสาเหตุที่แน่นอน)

### คุณสมบัติ

- 🧩 **สถาปัตยกรรมปลั๊กอิน** — มอดและโมดูลระบบเป็นไฟล์ jar อิสระ เพียงวาง
  ลงในโฟลเดอร์ก็จะถูกหยิบมาใช้งาน โดยไม่ต้องแก้ไขตัว Flint เอง
- 🪝 **รองรับ Mixin** — รวมและบูตสแตรป SpongePowered Mixin มาให้ในตัว ให้คุณฉีด
  และเขียนไบต์โค้ดของคลาสเกมใดก็ได้ใหม่
- ⚙️ **สร้างบนพื้นฐานของ Java Agent** — สร้างบน `Instrumentation` พร้อมรองรับการ
  นิยามคลาสใหม่และการแปลงสภาพใหม่
- 🚀 **รันไทม์ที่ครบในตัว** — งาน Gradle จะดาวน์โหลดไฟล์ jar ของไคลเอนต์
  ไลบรารี และแอสเซ็ตให้คุณ ไม่ต้องมี HMCL หรือตัวเริ่มเกมอื่น ๆ
- 📦 **ระบบโมดูลสองแบบ** — `Modsrt` (มอด) และ `PubSystem` (โมดูลระบบ)
  เป็นคนละส่วนกันและไม่รบกวนซึ่งกันและกัน

### เวอร์ชันเกมที่รองรับ

| Support | Version |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## ความต้องการ

| รายการ | ความต้องการ |
|-----|------|
| JDK | **Java 25** (จำเป็นอย่างยิ่งสำหรับ 26.3; Gradle และเกมใช้ JVM เดียวกัน) |
| การสร้าง | Gradle Wrapper (`./gradlew` ไม่ต้องติดตั้ง Gradle แยกต่างหาก) |
| เครือข่าย | การสร้างครั้งแรกจะดาวน์โหลดไคลเอนต์ ไลบรารี และแอสเซ็ต (~550MB+) |
| ดิสก์ | รันไทม์อยู่ใน `.flint/minecraft` ควรเว้นพื้นที่ว่างประมาณ 1GB |

## เริ่มต้นใช้งานอย่างรวดเร็ว

```bash
# 1. สร้างเอเจนต์ Flint และเริ่มไคลเอนต์ (ไลบรารี/แอสเซ็ตที่ขาดจะถูกดาวน์โหลดอัตโนมัติ)
./gradlew runClient
```

`runClient` จะ: สร้าง `Flint-<version>.jar` → ดาวน์โหลดไลบรารีและแอสเซ็ต →
เริ่มเกมด้วยบัญชีออฟไลน์ชื่อ `Dev` และติดตั้ง Flint เป็นเอเจนต์ Java

งานที่มีประโยชน์อื่น ๆ:

```bash
# ดาวน์โหลดเฉพาะรายการเวอร์ชันและ client.jar เท่านั้น
./gradlew downloadMinecraft

# ดาวน์โหลดเฉพาะไลบรารีรันไทม์ที่ระบบปัจจุบันต้องการ (รวมไฟล์ native)
./gradlew downloadLibraries

# ดาวน์โหลดเฉพาะดัชนีแอสเซ็ตและทรัพยากรเกม (การรันครั้งแรก ~484MB)
./gradlew downloadAssets

# สร้างเฉพาะไฟล์ jar ของเอเจนต์ ไม่เริ่มเกม
./gradlew jar

# พิมพ์คำสั่งเริ่มเกมเต็มโดยไม่เริ่มเกมจริง สะดวกสำหรับการดีบัก
./gradlew printRunArgs
```

คุณยังสามารถเขียนทับค่าใด ๆ ใน `gradle.properties` ได้จากคำสั่งบรรทัดคำสั่ง:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## โครงสร้างไดเรกทอรี

ในไดเรกทอรีเกม (`--gamedir` ค่าเริ่มต้นคือไดเรกทอรีการทำงานปัจจุบัน)
Flint จะสร้างสิ่งต่อไปนี้ให้คุณ:

```
<game dir>/
├── mods/            # โฟลเดอร์มอด Modsrt วาง *.jar ที่นี่เพื่อโหลด
├── pubSystem/       # โฟลเดอร์โมดูล PubSystem วาง *.jar ที่นี่เพื่อโหลด
├── logs/            # บันทึกเกม
└── packager.txt     # ไฟล์การจัดการแพ็กเกจ
```

รันไทม์ (ไฟล์ jar ของไคลเอนต์ ไลบรารี แอสเซ็ต) จะเก็บไว้ใน `.flint/minecraft` ที่
รากของโปรเจกต์ — จงใจเก็บไว้นอก `build/` เพราะไม่เช่นนั้นเพียงคำสั่ง `gradle clean`
ครั้งเดียวก็จะล้างการดาวน์โหลด ~550MB ทิ้งหมด เปลี่ยนกลับเป็น
`build/minecraft` ได้โดยแก้ไข `minecraftDir` ใน `gradle.properties`

## การเขียนมอด

### ตัวเลือกที่ 1: มอด Modsrt (มอดในเกม)

ใช้อินเทอร์เฟซ `Modsrt` และทำเครื่องหมายคลาสด้วย `@MODS` เมื่อ Flint
สแกนโฟลเดอร์ `mods/` มันจะสร้างอินสแตนซ์ของคลาสและเรียก `onLoad()`:

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

### ตัวเลือกที่ 2: โมดูล PubSystem (ส่วนขยายระดับระบบ)

นำอินเทอร์เฟซ `PubModule` ไปใช้ ทำเครื่องหมายคลาสด้วยแอนโนเทชัน `@PUBCOM`
แล้ววางไฟล์ jar ลงใน `pubSystem/`:

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
        // ด้วยอินสแตนซ์ Instrumentation คุณสามารถลงทะเบียน
        // ClassFileTransformer ของคุณเองและเขียนคลาสเกมใดก็ได้ใหม่
        // โดยไม่แตะต้องโค้ดของ Flint เอง
    }

    @Override
    public void onUnload() { }
}
```

`onLoad` จะส่งอินสแตนซ์ `Instrumentation` มาให้คุณ ดังนั้นโมดูลจึงสามารถลงทะเบียน
`ClassFileTransformer` ของตัวเองได้ — นี่แหละที่ทำให้ Flint เป็นแบบปลั๊กอิน

### ตัวเลือกที่ 3: Mixin

Flint บูตสแตรป Mixin มาพร้อมใช้งานทันที ดังนั้นมอดจึงใช้แอนโนเทชันของ Mixin
ได้ตามปกติ ไฟล์กำหนดค่าของ Flint เองคือ `flint.mixins.json`
(`compatibilityLevel: JAVA_25`) และตัวอย่างการฉีดโค้ดอยู่ใน
`org.flint.mixin.TitleScreenTransformer`

## การตั้งค่า

คีย์ที่ใช้บ่อยใน `gradle.properties` (ค่าจากบรรทัดคำสั่งมีความสำคัญกว่าเสมอ):

| คีย์ | ค่าเริ่มต้น | คำอธิบาย |
|---|:---:|-----|
| `minecraft_version` | `26.3` | เวอร์ชัน Minecraft เป้าหมาย |
| `version` | `26.2-1.22.2-0.1.0` | เวอร์ชันของไฟล์ jar เอเจนต์ Flint |
| `minecraftDir` | `.flint/minecraft` | ไดเรกทอรีรันไทม์ (client.jar / ไลบรารี / แอสเซ็ต) |
| `offlinePlayerName` | `Dev` | ชื่อบัญชีออฟไลน์ที่ `runClient` ใช้ |
| `windowWidth` / `windowHeight` | (ปิดคอมเมนต์ไว้) | เอาคอมเมนต์ออกเพื่อบังคับขนาดหน้าต่าง |
| `assetDownloadThreads` | `8` | เธรดสำหรับการดาวน์โหลดแอสเซ็ตครั้งแรก |
| `downloadRetries` | `4` | จำนวนครั้งที่ลองใหม่เมื่อดาวน์โหลดล้มเหลว |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | หมดเวลาเชื่อมต่อ / อ่าน HTTP |

## โครงสร้างโปรเจกต์

```
Flint/
├── src/main/java/org/flint/        # แกนหลักของ Flint
│   ├── AgentMain.java              # จุดเข้าของเอเจนต์ Java (premain)
│   ├── modsrt/                     # ตัวโหลดมอด Modsrt
│   ├── pubsystem/                  # ตัวโหลดโมดูล PubSystem
│   ├── dirpath/                    # การบูตสแตรปโครงสร้างไดเรกทอรี
│   ├── mixin/                      # Mixin ตัวอย่างในตัว
│   └── mixinservice/               # อะแดปเตอร์บริการ Mixin
├── src/main/resources/             # ทรัพยากร ไฟล์ manifest ของเอเจนต์ กำหนดค่า Mixin
├── Mixin/                          # ซอร์สโค้ด SpongePowered Mixin
├── buildSrc/                        # สคริปต์การสร้าง
├── build.gradle                     # นิยามงานสร้างและการรัน
└── gradle.properties                # การกำหนดค่าเวอร์ชันและรันไทม์
```

## การแก้ไขปัญหา

- **ตัวเริ่มเกมไม่ตรวจพบ Flint**: ดู "ปัญหาที่ทราบแล้ว" ด้านบน — ยังต้องใช้ตัวเริ่มเกม
  เวอร์ชันดั้งเดิม 26.3 อยู่ในตอนนี้
- **`client.jar is missing`**: รัน `./gradlew downloadMinecraft` ก่อน
- **`missing ... libraries`**: รัน `./gradlew downloadLibraries` ก่อน
- **ข้อผิดพลาดเวอร์ชัน Java**: ชี้ JVM ของ Gradle ไปที่ Java 25
- **อยากเห็นว่ามีคำสั่งอะไรรันจริง**: `./gradlew printRunArgs` จะพิมพ์คำสั่ง
  เต็มโดยไม่เริ่มเกม

## แหล่งอ้างอิง

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — เฟรมเวิร์กการฉีด
  ไบต์โค้ดที่การรองรับ Mixin ของ Flint สร้างอยู่บนนั้น
- [ASM](https://asm.ow2.io/) — ไลบรารีการจัดการไบต์โค้ดระดับต่ำ

---

💡 ยินดีต้อนรับ issues และ PR ทุกความคิดเห็น ช่วยให้ Flint ดีขึ้น
> ⚠️ เอกสารนี้เป็นฉบับแปล อาจไม่ถูกต้องทั้งหมด หากพบข้อไม่ตรงกัน โปรดอ้างอิงฉบับภาษาอังกฤษ: [English](../README.md)
