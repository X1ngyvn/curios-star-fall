<div align="center">

# ⭐ Star Fall · 星陨饰品栏

**用一颗星陨开启成长：每过一个游戏日，你的 Curios 饰品栏就多一点。**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-62B47A?style=flat-square)](https://www.minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.x-DFA86A?style=flat-square)](https://files.minecraftforge.net/)
[![Curios](https://img.shields.io/badge/Curios-5.7.0%2B-8A2BE2?style=flat-square)](https://www.curseforge.com/minecraft/mc-mods/curios)
[![Version](https://img.shields.io/badge/version-1.3.0-2F81F7?style=flat-square)](https://github.com/X1ngyvn/curios-star-fall/releases)
[![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-red?style=flat-square)](LICENSE)

[玩法](#玩法) · [栏位](#发放的是原生栏位) · [配置](#配置) · [命令](#命令) · [安装](#安装) · [FAQ](#常见问题)

</div>

---

## 它做什么

绝大多数饰品模组在**增加饰品**，Star Fall 则在管理**栏位数量**本身：

> 玩家的饰品栏不是一开始就全给你的，而是随着生存天数一格一格长出来。

它解决了整合包里一个很具体的痛点：饰品越加越多、栏位却永远不够，最后只能靠开局直接塞满。
现在你可以让栏位变成**进度**——第 1 天几个、第 7 天多一个戒指位、第 30 天再送两个项链位。

## 玩法

### 1. 合成「星陨」

需要先击杀末影龙拿到龙蛋。十字摆放，龙蛋居中，四角留空：

```
      ★
  ★   蛋   ★
      ★
```

`★` = 下界之星 ×4　`蛋` = 龙蛋 ×1 → **星陨 / Star Fall**

### 2. 右键激活

手持星陨右键使用：

- 物品**消耗 1 个**
- 解锁挑战成就 **「渐强 / Crescendo」**
- 从当天开始计时（激活前过去的天数不补发，不会出现「老玩家一进来几十格」）

未激活的玩家不会获得任何栏位，也不会累计天数。

### 3. 开始成长

每过一个游戏日，聊天栏提示：

```
渐强:饰品栏+1(累计1)
```

同时 Curios 界面里对应的原生栏位多一格。离线跨过的天数会在上线时补结算。

### 4. （可选）换成通用栏位

原生栏位只能用对应标签的饰品。如果你想要一个「什么都能放」的栏位：

```
/dailyslot convert @s curio 1
```

把 1 个原生 curio 位换成通用位 `daily_curio`。**默认不送通用位**，需要时再手动换。

## 发放的是原生栏位

| 栏位 id | 中文 | 说明 |
| --- | --- | --- |
| `curio` | 饰品 | **默认每日发放目标** |
| `head` | 首饰 | |
| `necklace` | 项链 | 常用里程碑目标 |
| `ring` | 戒指 | 常用里程碑目标 |
| `belt` | 腰带 | |
| `body` | 胸饰 | |
| `charm` | 护符 | |
| `hands` | 手饰 | |
| `bracelet` | 手镯 | |
| `daily_curio` | 通用饰品 | 本模组自建，可放任意物品，需 convert 获得 |

因为发的就是 Curios 原生栏位，所以 **GUI 图标、槽位排版与分页、物品 tooltip、死亡掉落规则、属性加成结算、客户端同步** 全部沿用 Curios 自身逻辑，与其他饰品模组没有任何行为差异。

> 实现细节：原版 Curios 只带栏位定义、不带「玩家拥有哪些栏位」的数据，因此本模组把上述 9 种原生栏位挂到玩家身上。
> **基础数量完全沿用 Curios 自己的定义**（每种 1 格），本模组不改动任何默认数值，只在基础上按天数/里程碑叠加永久修饰符——与 `/curios set` 是同一套机制。

## 配置

首次启动生成 `config/curios-star-fall-common.toml`：

```toml
# 里程碑：世界天数到达 day 时一次性发放（默认空 = 只有每日规则）
milestones = []

[general]
	intervalDays   = 1       # 每几个游戏日发放一次；0 = 关闭每日发放
	slotsPerGrant  = 1       # 每次发放几个
	defaultSlot    = "curio" # 每日发放用哪种栏位
	maxSlotsPerType = 32     # 每种栏位最多发放多少；0 = 不限
	announce       = true    # 发放时是否聊天栏提示
```

**「第 7 天 +1 戒指、第 30 天 +2 项链」**：

```toml
milestones = ["day=7;slot=ring;count=1", "day=30;slot=necklace;count=2"]
```

规则说明：

- 格式 `day=天数;slot=栏位类型;count=数量`，分号分隔、顺序任意
- 只发一次；离线跨过的天数会补发
- 同一天同一类型写多条时以最后一条为准
- 改完文件需重启，或直接用下面的命令改（会写回文件）

## 命令

需要权限等级 2（单人开作弊 / 服务器 OP），与 `/curios` 系列同级。

| 命令 | 说明 |
| --- | --- |
| `/dailyslot` · `/dailyslot status` | 查看配置、激活状态、自己拥有的栏位 |
| `/dailyslot set <天> <数量> [栏位]` | 修改每日规则，立即生效并写回配置文件 |
| `/dailyslot rule add <天> <栏位> <数量>` | 追加里程碑 |
| `/dailyslot rule remove <天> <栏位>` | 删除里程碑 |
| `/dailyslot rule list` | 列出所有里程碑 |
| `/dailyslot grant <目标> <栏位> <数量>` | 立即补发 |
| `/dailyslot convert <目标> <原栏位> <数量>` | 原生栏位 → 通用栏位 |
| `/dailyslot sync <目标>` | 重新同步栏位数量（显示不同步时用） |
| `/dailyslot reset <目标>` | 清空所有发放的栏位（栏内物品会掉落） |
| `/dailyslot activate <目标>` | 免物品激活（调试用） |

## 安装

1. 安装 **Forge 1.20.1（47.x）**
2. 把 **Curios API**（≥ 5.7.0）与 `curios-star-fall-1.3.0.jar` 一起放进 `mods/`
3. 启动游戏 → 首次运行生成配置文件
4. 合成星陨、右键激活，开始攒栏位

## 兼容性

- **Minecraft** 1.20.1 · **Forge** 47.x · **Java** 17
- **Curios API** 5.7.0+，已在 **5.12.1 / 5.14.1** 实测
- 与其他增加 Curios 栏位的模组共存：本模组只做 `Math.max` 式合并，不会覆盖别人给的更大数值
- 与旅行者背包等使用 Curios 的模组共存正常
- 旧版本数据自动迁移（`curios_xingyun` / `curios_api_curios_a_08a2` → `curios_starfall`）

## 从源码构建

不依赖 Gradle，直接用 JDK 编译（需要 JDK 17+）：

```bash
# 依赖取自本机 Forge 1.20.1 安装目录（Forge / Minecraft SRG / Curios）
MC_DIR=/path/to/.minecraft ./build.sh
```

产物：`dist/curios-star-fall-1.3.0.jar`

编译要点：classpath 必须把 `forge-*-client.jar` 与 `client-*-srg.jar` 放在最前面——源码里直接使用的是 SRG 名（`m_xxx_` / `f_xxx_`），这是 1.20.1 Forge 生产环境的命名方式。

## 项目结构

```
src/main/java/cn/blockforge/generated/curiosapicuriosa08a2/
├─ GeneratedMod.java      # 入口：物品注册 / 配置 / 事件总线
├─ StarFallItem.java      # 星陨：右键激活（消耗 + 发成就）
├─ DailySlotHandler.java  # 发放逻辑：天数结算、里程碑、Curios 修饰符同步
├─ DailySlotConfig.java   # ForgeConfigSpec 与 milestones 解析
├─ DailySlotCommand.java  # /dailyslot 命令族
└─ ModItems.java          # 创造模式物品栏归类
src/main/resources/
├─ META-INF/mods.toml
├─ assets/curios_starfall/   # 贴图、模型、中英文语言文件
└─ data/curios_starfall/     # 槽位定义、实体槽位、配方、成就
```

## 常见问题

<details>
<summary><b>装好了，但栏位一直没增加？</b></summary>

1. `/dailyslot status` 看是否显示「已激活」——没激活说明还没使用星陨；
2. 已激活但栏位没变化：`/dailyslot sync @s` 强制同步一次；
3. 还在同一天内：默认 1 天（24000 tick）+1 格，睡一觉或等天亮。

</details>

<details>
<summary><b>和别的饰品模组冲突吗？</b></summary>

不会。本模组只通过 Curios 公开 API 增加栏位修饰符，与 `/curios add` 是同一套机制。

</details>

## 更新日志

见 [CHANGELOG.md](CHANGELOG.md)。当前版本 **1.3.0**。

## 授权

**All Rights Reserved**（详见 [LICENSE](LICENSE)）。

- ✅ 整合包可直接收录、分发本模组的**未修改** jar，无需申请
- ✅ 可通过平台的「依赖 / 整合包引用」功能引用
- ❌ 修改、反编译后二次分发；搬运到其他下载站

---

<div align="center">

### English

**Star Fall** is a small Curios addon for Minecraft 1.20.1 (Forge). Instead of adding new trinkets,
it grows the *number of slots* over time: craft **Star Fall** (4 nether stars + 1 dragon egg),
right-click to activate, and every in-game day your Curios slots increase by a configurable amount.
Schedules like "day 7 → +1 ring slot, day 30 → +2 necklace slots" are configurable via `milestones`,
and the whole thing unlocks the *Crescendo* advancement.

Requires **Curios API 5.7.0+** (tested on 5.12.1 / 5.14.1).

</div>
