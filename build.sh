#!/usr/bin/env bash
# 重新编译「每日饰品栏」模组（Forge 1.20.1）
#
# 依赖来源：本机樱之城整合包实例里的 Forge / Minecraft(SRG) / Curios 库。
# 关键点：classpath 必须把 forge-*-client.jar 与 client-*-srg.jar 放在最前面，
#        只有这两个 jar 里的 Minecraft 类才是 SRG 命名（m_xxx_ / f_xxx_），
#        源码里调用的就是这些名字；且路径必须是 Windows 形式（javac 是 Windows 程序）。
set -euo pipefail

MC_DIR="${MC_DIR:-/d/mod/我的世界樱之城幸运方块1.20.1 正式版1.0/我的世界樱之城幸运方块1.20.1 正式版1.0/.minecraft}"
LIB="$MC_DIR/libraries"
FORGE_JAR="$LIB/net/minecraftforge/forge/1.20.1-47.3.22/forge-1.20.1-47.3.22-client.jar"
SRG_JAR="$LIB/net/minecraft/client/1.20.1-20230612.114412/client-1.20.1-20230612.114412-srg.jar"
CURIOS_JAR="${CURIOS_JAR:-/c/Users/35155/Downloads/curios-forge-5.14.1+1.20.1.jar}"
if [ ! -f "$CURIOS_JAR" ]; then
    CURIOS_JAR="$MC_DIR/mods/curios-forge-5.12.1+1.20.1.jar"
fi
JAVA_BIN="${JAVA_BIN:-/c/Program Files/Java/latest/jdk-21/bin}"

cd "$(dirname "$0")"

BUILD_LIBS=".build/libs"
mkdir -p "$BUILD_LIBS"

# 依赖 jar 只需复制一次（Windows 上首次复制 150+ 个 jar 约 1 分钟）
if [ ! -f "$BUILD_LIBS/forge-1.20.1-47.3.22-client.jar" ]; then
    cp -n "$FORGE_JAR" "$SRG_JAR" "$CURIOS_JAR" "$BUILD_LIBS/"
    find "$LIB" -name "*.jar" ! -name "client-*-slim.jar" ! -name "client-*-extra.jar" \
        -exec cp -n {} "$BUILD_LIBS/" \;
fi

LIBW="$(cygpath -w "$PWD/$BUILD_LIBS")"
CP="$LIBW\\forge-1.20.1-47.3.22-client.jar;$LIBW\\client-1.20.1-20230612.114412-srg.jar;"
for f in "$BUILD_LIBS"/*.jar; do
    base="${f##*/}"
    case "$base" in
        forge-1.20.1-47.3.22-client.jar|client-1.20.1-20230612.114412-srg.jar) continue ;;
    esac
    CP="$CP$LIBW\\$base;"
done

rm -rf build/classes
mkdir -p build/classes

"$JAVA_BIN/javac.exe" --release 17 -nowarn -encoding UTF-8 \
    -d build/classes -cp "$CP" $(find src/main/java -name "*.java")

mkdir -p dist
rm -f dist/*.jar
"$JAVA_BIN/jar.exe" --create --file "dist/curios-star-fall-1.3.0.jar" \
    -C build/classes . -C src/main/resources .

echo "built: dist/curios-star-fall-1.3.0.jar"
