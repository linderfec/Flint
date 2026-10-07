![Mixin Logo](docs/javadoc/resources/logo.png?raw=true)

**Mixin** is a trait/mixin framework for Java using [ASM](https://asm.ow2.io/)
and hooking into the runtime classloading process via a set of pluggable
built-in or user-provided services. Built-in services currently support Mojang's
[LegacyLauncher](https://github.com/Mojang/LegacyLauncher) system, though this
is deprecated in favour of [ModLauncher](https://github.com/cpw/modlauncher) by
cpw, which has greater extensibility and has support for Java 8 and later.

### Documentation

The main documentation for **Mixin** can be found in the
[Wiki](https://github.com/SpongePowered/Mixin/wiki).

Additional documentation for individual features and annotations can be found in
the extensive [Javadoc](http://jenkins.liteloader.com/job/Mixin/javadoc/). For
additional help use the channel [`#mixin` on the Sponge Discord Server](https://discord.gg/tBcwxz2).

### Binaries

Mixin binaries are available via [Jenkins](https://jenkins.liteloader.com/view/Other/job/Mixin/)
and are published to the following maven repositories:

* https://repo.spongepowered.org/repository/maven-public/ - SNAPHOTs and RELEASE
  builds
* https://files.minecraftforge.net/maven/ - RELEASE builds only

### Tooling

For handling obfuscation tasks, Mixin provides an [Annotation Processor](https://github.com/SpongePowered/Mixin/wiki/Using-the-Mixin-Annotation-Processor)
which works at compile time to generate obfuscation mappings for your toolchain
to apply. If using Gradle 5 or later, annotation processors are no longer
automatically loaded from `compile` configurations and must be specified
explicitly via `annotationProcessor` configurations. For this purpose, Mixin
provides "fat jar" artefacts containing all required dependencies via the
`:processor` classifier. For example if your build uses the dependency
`org.spongepowered:mixin:1.2.3` then your annotationProcessor configuration
should specify dependency `org.spongepowered:mixin:1.2.3:processor`.

If you are using Mixin in a [Minecraft Forge](https://minecraftforge.net/)
project then the [MixinGradle](https://github.com/SpongePowered/MixinGradle)
plugin can be used to simplify the configuration of the Mixin Annotation
Processor. It provides a simple syntax for configuring the Mixin AP for your
project, see the [MixinGradle README](https://github.com/SpongePowered/MixinGradle/blob/master/README.md)
for how to configure MixinGradle.  

### Integration with Eclipse

When developing using **Mixin**, you can use the **Mixin Annotation Processor**
within Eclipse to provide context-sensitive errors and warnings to help you more
easily troubleshoot your mixins. To do so:

1. Run the `gradle build` command to generate the mixin jar
2. Open the properties of your eclipse project and navigate to `Java Compiler`
  -> `Annotation Processing` -> `Factory Path`  
3. Check the `Enable project specific settings` checkbox
4. Click the `Add External JARs` button and select the generated mixin jar with
 the suffix **-processor** (hint: it should be in `Mixin/build/libs`)
5. Navigate up one level to `Java Compiler` -> `Annotation Processing`
6. Check the `Enable project specific settings` checkbox
7. Check the `Enable annotation processing` checkbox
8. Click the `New...` button next to the `Processor options` box
 * Set `Key` to **reobfSrgFile**
 * Set `Value` to the fully-qualified path to the `mcp-srg.srg` file (the
   location of the mapping file varies by platform, if you are unsure where to
   find it please follow the discord link below). 
9. Click `OK` to apply the changes

### Integration with IntelliJ IDEA

Enhanced functionality for working with **Mixin** in IntelliJ IDEA is available
via the [Minecraft Development for IntelliJ IDEA](https://plugins.jetbrains.com/idea/plugin/8327)
plugin developed by [DemonWav](https://github.com/demonwav).  

### Version History

[Specifying the `minVersion` property in your configurations](https://github.com/SpongePowered/Mixin/wiki/PSA-Forward-Compatibility-Features-in-Mixin)
is extrememly important. The following version history can be used to determine
when features were introduced (and sometimes when major bugs are squashed) in
order to help you determine which `minVersion` you should specify.