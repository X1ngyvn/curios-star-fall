# Star Fall · 星陨饰品栏

> 用一颗「星陨」开启成长：每过一个游戏日，Curios 饰品栏就多一点。

一个 Forge 1.20.1 的 Curios 附属模组。它不新增花哨的饰品，而是接管**栏位数量**本身——
让玩家的饰品栏随着生存天数一点点长出来，并且可以用配置精确安排「第几天、加几个、加哪种」。

- **模组名**：星陨饰品栏 / Star Fall
- **modId**：`curios_starfall`
- **支持版本**：Minecraft 1.20.1 · Forge 47.x
- **前置**：Curios API 5.7.0+（已在 5.12.1 / 5.14.1 实测）

---

## 玩法

1. 击杀末影龙、拿到龙蛋，用 **4 个下界之星 + 1 个龙蛋**（十字摆放，龙蛋居中）合成 **星陨 / Star Fall**
2. 右键使用星陨：物品消耗，**解锁史诗成就「渐强」**，每日饰品栏从当天开始计时
3. 之后每过一个游戏日，聊天栏提示 `渐强:饰品栏+1(累计1)`，Curios 里对应的原生栏位就多一格
4. 想要「什么都能装」的通用栏位？用 `/dailyslot convert @s curio 1` 把 1 个原生 curio 位换成通用位

未激活的玩家不会获得任何栏位，也不会累计天数。

## 栏位类型

发放的是 **Curios 原生栏位**，因此 GUI 图标、tooltip、死亡掉落规则、属性结算、客户端同步
全部沿用 Curios 自己的逻辑：

`head` `necklace` `ring` `belt` `body` `charm` `hands` `bracelet` `curio`

外加本模组自建的通用栏位 `daily_curio`（可放任意物品，需手动转换获得）。

> 说明：原版 Curios 只带栏位定义、不带「玩家拥有哪些栏位」的数据，所以本模组把上述 9 种原生栏位
> 挂到玩家身上并把基础数量设为 0——栏位完全靠天数/里程碑长出来，不会白送。

## 配置

`config/curios-star-fall-common.toml`

```toml
# 里程碑规则，默认空（只有每日规则生效）
milestones = []

[general]
	intervalDays = 1        # 每几个游戏日发放一次；0 = 关闭每日发放
	slotsPerGrant = 1       # 每次发放几个
	defaultSlot = "curio"   # 每日发放用哪种栏位
	maxSlotsPerType = 32    # 每种栏位最多发放多少（0 = 不限）
	announce = true         # 发放时是否聊天栏提示
```

里程碑写法（一次性，跨天会补发）：

```toml
milestones = [
  "day=7;slot=ring;count=1",
  "day=30;slot=necklace;count=2"
]
```

## 命令

需要权限等级 2（单人开作弊 / 服务器 OP）。

| 命令 | 说明 |
| --- | --- |
| `/dailyslot` / `/dailyslot status` | 查看配置、激活状态与自己的栏位 |
| `/dailyslot set <天> <数量> [栏位]` | 修改每日规则（写回配置文件） |
| `/dailyslot rule add <天> <栏位> <数量>` | 追加里程碑 |
| `/dailyslot rule remove <天> <栏位>` | 删除里程碑 |
| `/dailyslot rule list` | 列出里程碑 |
| `/dailyslot grant <目标> <栏位> <数量>` | 立即补发 |
| `/dailyslot convert <目标> <原栏位> <数量>` | 原生栏位 → 通用栏位 |
| `/dailyslot sync <目标>` | 重新同步栏位数量 |
| `/dailyslot reset <目标>` | 清空所有发放的栏位（栏内物品会掉落） |
| `/dailyslot activate <目标>` | 免物品激活（调试用） |

## 从源码构建

本项目不依赖 Gradle，直接调用 JDK 编译（需要 JDK 17+）：

```bash
# 依赖取自本机 Forge 1.20.1 安装（Forge / Minecraft SRG / Curios）
MC_DIR=/path/to/.minecraft ./build.sh
```

- classpath 必须把 `forge-*-client.jar` 与 `client-*-srg.jar` 放在最前面（源码里用的是 SRG 名 `m_xxx_` / `f_xxx_`）
- 产物：`dist/curios-star-fall-1.3.0.jar`

## 兼容性

- Curios 5.12.1 / 5.14.1 实测通过（`addPermanentSlotModifier` 等 API 与槽位 JSON 合并逻辑均未变化）
- 与任何通过 Curios 增加栏位的模组共存：本模组只做 `Math.max` 式合并，不会覆盖别人设的更大值
- 旧版本存档数据会自动迁移（`curios_xingyun` / `curios_api_curios_a_08a2`）

## 授权

All Rights Reserved。整合包可直接收录并分发本模组原版文件；如需修改/二次分发源码请先联系作者。
