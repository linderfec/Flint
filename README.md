# Flint Mod Loader

Flint是一个为Minecraft 1.21.10设计的模组加载器。

## 功能特性

- 启动Minecraft 1.21.10客户端
- 支持模组加载（开发中）
- 使用ASM进行字节码操作
- 支持Java 21

## 构建

```bash
./gradlew build
```

## 运行

```bash
./gradlew runClient
```

## 项目结构

- `src/main/java/net/Flint/` - Flint模组加载器核心代码
- `src/main/java/net/Flint/logs/` - 日志系统
- `src/main/java/net/Flint/modloader/` - 模组加载相关代码

## 许可证

请参阅LICENSE文件。