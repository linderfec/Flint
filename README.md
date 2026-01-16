# Flint Mod Loader

Flint是一个为 Minecraft 的模组加载器。

## 功能特性

- 启动Minecraft 1.21.10客户端
- 支持模组加载
- 使用ASM进行字节码操作
- 支持Java 21
- 其他的事件系统，注册系统，网络系统 等。 都是以模组形式出现的

## 项目结构

    Flint/
    ├─ src/
       ├─ main/
       |  └─ java/net/Flint/
       |     ├─ api/            -模组加载器的api
       |     ├─ lifecycle/      -生命周期
       |     ├─ logs/           -日志系统
       |     ├─ mixin/          -mixin系统
       |     └─ modloader/      -模组加载
       └─ test/

## 许可证

请参阅LICENSE文件。