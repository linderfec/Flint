#!/usr/bin/env bash

# 设置Minecraft测试环境的脚本
echo "正在设置Minecraft测试环境..."

# 定义目录路径
TEST_ENV_PATH="src/test-env"
LIBRARIES_PATH="src/test-env/libraries"
MINECRAFT_VERSION="1.21.10"

# 创建目录
mkdir -p $TEST_ENV_PATH
mkdir -p $LIBRARIES_PATH

echo "测试环境目录已创建: $TEST_ENV_PATH"
echo "依赖库目录已创建: $LIBRARIES_PATH"

# 检查Minecraft主jar文件是否存在
MINECRAFT_JAR_PATH="$TEST_ENV_PATH/$MINECRAFT_VERSION.jar"
if [ -f "$MINECRAFT_JAR_PATH" ]; then
    echo "Minecraft主jar文件已存在: $MINECRAFT_JAR_PATH"
else
    echo "Minecraft主jar文件不存在: $MINECRAFT_JAR_PATH"
    echo "请手动下载Minecraft $MINECRAFT_VERSION jar文件并放置到 $TEST_ENV_PATH 目录下"
    echo "或者使用Minecraft启动器下载对应版本后复制该文件"
fi

echo ""
echo "测试环境设置完成!"
echo "目录结构如下:"
echo "├── $TEST_ENV_PATH"
echo "    ├── $MINECRAFT_VERSION.jar  <- 需要手动添加"
echo "    └── libraries/               <- 需要手动添加所有Minecraft依赖库"
echo ""
echo "要运行测试环境，请使用: ./gradlew runTestEnv"