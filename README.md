# Flint Mod Loader

Flint是一个为Minecraft 1.21.10设计的模组加载器。

## 功能特性

- 启动Minecraft 1.21.10客户端
- 支持模组加载
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

## 模组开发

### 创建模组

创建一个类并使用 `@Mod` 注解标记：

```java
package net.Flint.yourmod;

import net.Flint.modloader.Mod;
import net.Flint.modloader.IFlintMod;
import static net.Flint.logs.logger.logger;

@Mod(value = "your_mod_id", name = "Your Mod Name", version = "1.0.0", description = "Description of your mod")
public class YourMod implements IFlintMod {
    
    @Override
    public void onInitialize() {
        logger.info("Your mod is initializing...");
        // 在这里添加模组初始化逻辑
    }
}
```

### 模组配置文件

创建 `flint.mods.json` 文件：

```json
{
  "id": "your_mod_id",
  "version": "1.0.0",
  "name": "Your Mod Name",
  "description": "Description of your mod",
  "mainClass": "net.Flint.yourmod.YourMod",
  "depends": [],
  "authors": [
    "Your Name"
  ]
}
```

### 打包和部署

1. 将模组类和配置文件打包成 JAR
2. 将 JAR 文件放入 Flint 的 `mods` 目录
3. 启动 Flint，模组会自动加载

## 项目结构

- `src/main/java/net/Flint/` - Flint模组加载器核心代码
- `src/main/java/net/Flint/logs/` - 日志系统
- `src/main/java/net/Flint/modloader/` - 模组加载相关代码

## 许可证

请参阅LICENSE文件。