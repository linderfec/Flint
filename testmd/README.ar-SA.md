<p align="center">
  <img src="../src/main/resources/Flint.png" alt="Flint" width="180">
</p>

<h1 align="center">Flint</h1>

<p align="center">
  مُحمِّل مودات (mod loader) مجاني ومفتوح المصدر قائم على الإضافات لعالم Minecraft
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

## نبذة عن Flint

لم يعد Flint حلًّا تقليديًا يقوم على "استبدال مشغّلك" (launcher)، بل يرتبط بعملية
Minecraft الفانيلية بوصفه **وكيل Java** (Java Agent) عبر `-javaagent` ويستولي على
تحميل الأصناف (class loading) في أبكر لحظة ممكنة، بحيث يمكن تحميل المودات
والوحدات مع بقائك على استخدام المشغّل الأصلي.

> ⚠️ **مشكلة معروفة**
> حتى الآن يجب استخدام **مشغّل الفانيلية 26.3** ليتمكن محمّل Flint من
> الاكتشاف. (السبب الدقيق غير معروف بعد.)

### المزايا

- 🧩 **بنية مبنية على الإضافات** — المودات والوحدات النظامية عبارة عن ملفات jar
  مستقلة؛ ضعها في مجلد واحد وتُحمَّل تلقائيًا دون أي تعديل على Flint نفسه
- 🪝 **دعم المكسين (Mixin)** — يُرفِق ويهيّئ SpongePowered Mixin بحيث يمكنك حقن
  وأعد كتابة بايت كود أي صنف في اللعبة
- ⚙️ **قائم على وكيل Java** — مبني على `Instrumentation` مع دعم إعادة تعريف
  الأصناف (redefinition) وإعادة تحويلها (retransformation)
- 🚀 **بيئة تشغيل متكاملة** — مهام Gradle تنزّل ملف العميل والمكتبات والأصول
  نيابةً عنك؛ لا حاجة لـ HMCL أو أي مشغّل آخر
- 📦 **نظاما وحدات** — `Modsrt` (للمودات) و`PubSystem` (للوحدات النظامية)
  منفصلان تمامًا ولا يتعارضان مع بعضهما أبدًا

### إصدارات اللعبة المدعومة

| الدعم | الإصدار |
|:----:|:----:|
| ✅ | 26.3 |
| ✅ | 26.2 |

## المتطلبات

| البند | المتطلب |
|-----|------|
| JDK | **Java 25** (متطلب صارم لإصدار 26.3؛ تتشارك Gradle واللعبة نفس آلة الجافا JVM) |
| البناء | غلاف Gradle ‏(`./gradlew`، دون حاجة لتثبيت Gradle منفصل) |
| الشبكة | أول بناء ينزّل اللعبة والمكتبات والأصول (~550MB+) |
| القرص | تقيم بيئة التشغيل في `.flint/minecraft`، اترك نحو 1GB من المساحة الحرة |

## البدء السريع

```bash
# 1. بناء وكيل Flint وتشغيل العميل (تُنزَّل المكتبات والأصول الناقصة تلقائيًا)
./gradlew runClient
```

سيقوم `runClient` بما يلي: بناء `Flint-<version>.jar` ← تنزيل المكتبات
والأصول ← تشغيل اللعبة بالحساب غير المتصل `Dev` وتحميل Flint بوصفه وكيل Java.

مهام مفيدة أخرى:

```bash
# تنزيل قائمة الإصدارات و client.jar فقط
./gradlew downloadMinecraft

# تنزيل مكتبات التشغيل التي يتطلبها نظام التشغيل الحالي فقط (بما في ذلك الملفات الأصلية natives)
./gradlew downloadLibraries

# تنزيل فهرس الأصول وموارد اللعبة فقط (أول تشغيل ~484MB)
./gradlew downloadAssets

# بناء ملف الوكيل jar فقط، دون تشغيل اللعبة
./gradlew jar

# طباعة أمر التشغيل الكامل دون بدء اللعبة، مفيد لأغراض التصحيح
./gradlew printRunArgs
```

يمكنك أيضًا تجاوز أي إعداد في `gradle.properties` من سطر الأوامر:

```bash
./gradlew runClient -PofflinePlayerName=Alice -PwindowWidth=1280 -PwindowHeight=720
```

## تخطيط المجلدات

في مجلد اللعبة (`--gamedir`، وهو افتراضيًا مجلد العمل الحالي)
ينشئ Flint لك ما يلي:

```
<game dir>/
├── mods/            # مجلد مودات Modsrt، ضع ملفات *.jar هنا لتحميلها
├── pubSystem/       # مجلد وحدات PubSystem، ضع ملفات *.jar هنا لتحميلها
├── logs/            # سجلات اللعبة
└── packager.txt     # ملف إدارة الحزم
```

تُخزَّن بيئة التشغيل (ملف العميل والمكتبات والأصول) في `.flint/minecraft` داخل
جذر المشروع — وهي متروكة خارج `build/` عن قصد، وإلا لمحو أمر `gradle clean`
واحد نحو 550 ميغابايت من التنزيلات. يمكنك إعادتها إلى `build/minecraft` بتعديل
`minecraftDir` في `gradle.properties`.

## كتابة المودات

### الخيار الأول: مودات Modsrt (مودات داخل اللعبة)

نفّذ واجهة `Modsrt` وعلِّم الصنف بـ `@MODS`. عندما يفحص Flint مجلد `mods/`
ينشئ نسخة من الصنف ويستدعي `onLoad()`:

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

### الخيار الثاني: وحدات PubSystem (امتدادات على مستوى النظام)

نفّذ واجهة `PubModule`، وعلّم الصنف بمعرّف `@PUBCOM`، ثم ضع ملف
الـ jar في `pubSystem/`:

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
        // بفضل نسخة Instrumentation يمكنك تسجيل ClassFileTransformer
        // خاص بك وإعادة كتابة أي صنف في اللعبة، دون
        // المساس بشفرة Flint نفسها.
    }

    @Override
    public void onUnload() { }
}
```

زوّدت `onLoad` بنسخة من `Instrumentation`، بحيث يمكن للوحدة تسجيل
`ClassFileTransformer` خاص بها — وهذا ما يجعل Flint قائمًا على الإضافات.

### الخيار الثالث: المكسين (Mixin)

يهيّئ Flint ماكسين (Mixin) جاهزًا منذ البداية، لذا يمكن للمودات استخدام معرّفات
Mixin (annotations) كالمعتاد. ملف الإعدادات الخاص بـ Flint هو `flint.mixins.json`
‏(`compatibilityLevel: JAVA_25`)، ويقيم مثال الحقن في
`org.flint.mixin.TitleScreenTransformer`.

## الإعدادات

المفاتيح الشائعة في `gradle.properties` (قيم سطر الأوامر لها الأولوية دائمًا):

| المفتاح | الافتراضي | الوصف |
|---|:---:|-----|
| `minecraft_version` | `26.3` | إصدار Minecraft المستهدف |
| `version` | `26.2-1.22.2-0.1.0` | إصدار ملف وكيل Flint ‏(jar) |
| `minecraftDir` | `.flint/minecraft` | مجلد بيئة التشغيل (client.jar / libraries / assets) |
| `offlinePlayerName` | `Dev` | اسم الحساب غير المتصل الذي يستخدمه `runClient` |
| `windowWidth` / `windowHeight` | (معلَّقة) | أزل التعليق لإجبار حجم النافذة |
| `assetDownloadThreads` | `8` | عدد الخيوط (Threads) المخصصة لعملية التنزيل الأولى للأصول |
| `downloadRetries` | `4` | عدد مرات إعادة المحاولة لعمليات التنزيل الفاشلة |
| `connectTimeoutMs` / `readTimeoutMs` | `10000` / `20000` | مهلة الاتصال / القراءة عبر HTTP |

## هيكل المشروع

```
Flint/
├── src/main/java/org/flint/        # نواة Flint
│   ├── AgentMain.java              # نقطة دخول وكيل Java ‏(premain)
│   ├── modsrt/                     # محمّل مودات Modsrt
│   ├── pubsystem/                  # محمّل وحدات PubSystem
│   ├── dirpath/                    # تهيئة تخطيط المجلدات
│   ├── mixin/                      # مثال ماكسين مدمج
│   └── mixinservice/               # محولات خدمة المكسين (Mixin service adapters)
├── src/main/resources/             # الموارد، وبيان الوكيل (agent manifest)، وإعدادات المكسين
├── Mixin/                          # مصادر SpongePowered Mixin
├── buildSrc/                        # سكربتات البناء
├── build.gradle                     # تعريفات مهام البناء والتشغيل
└── gradle.properties                # إعدادات الإصدار وبيئة التشغيل
```

## استكشاف الأخطاء وإصلاحها

- **المشغّل لا يكشف Flint**: راجع "مشكلة معروفة" أعلاه — يلزم استخدام مشغّل
  الفانيلية 26.3 حاليًا.
- **`client.jar is missing`**: نفّذ `./gradlew downloadMinecraft` أولًا.
- **`missing ... libraries`**: نفّذ `./gradlew downloadLibraries` أولًا.
- **خطأ في إصدار Java**: وجّه آلة الجافا (JVM) الخاصة بـ Gradle إلى Java 25.
- **تريد معرفة ما يُنفَّذ فعليًا**: يطبع `./gradlew printRunArgs` الأمر الكامل
  دون تشغيل اللعبة.

## الإقرارات

- [SpongePowered Mixin](https://github.com/SpongePowered/Mixin) — إطار حقن البايت
  كود الذي بُنيت عليه مزايا المكسين في Flint
- [ASM](https://asm.ow2.io/) — مكتبة منخفضة المستوى للتعامل مع البايت كود

---

💡 ترحَّب بالبلاغات وطلبات الدمج (PRs)، إذ إنها تساعد Flint على التحسّن.
> ⚠️ هذه الترجمة قد لا تكون دقيقة تمامًا. عند وجود أي تعارض، يُرجى الرجوع إلى النسخة الإنجليزية: [English](../README.md)
