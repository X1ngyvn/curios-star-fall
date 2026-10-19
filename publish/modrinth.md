# Modrinth 发布文本

## 一、项目字段（创建项目时逐项填写）

| 字段 | 建议填写 |
| --- | --- |
| Project name | `Star Fall`（不要写 `curios-star-fall`，页面上方提示避免把类别/前置名塞进项目名） |
| Slug | `star-fall` |
| Summary（**必须英文**，≤256 字符） | `Grow your Curios accessory slots over time: right-click Star Fall, then gain a configurable number of native slots every in-game day.` |
| Class（页面上的 Class 字段） | `Mod` |
| License | `All Rights Reserved` |
| Client side | `required` |
| Server side | `required` |
| Main category | `Equipment` |
| Additional categories | `Game Mechanics`、`Adventure` |
| Allow Comments | 勾选（否则玩家只能去 Issues 反馈） |
| Unlisted | 不勾 |
| Tags / Loaders | `Forge` |
| Game versions | `1.20.1` |
| Links | Source / Issues 填仓库地址；Wiki 可留空 |
| Dependencies | 必填依赖：`Curios API`（version range `>=5.7.0`） |

## 二、Description（直接粘贴到 Description 编辑器，Markdown）

```markdown
# Star Fall · 星陨饰品栏

> 用一颗「星陨」开启成长：每过一个游戏日，Curios 饰品栏就多一点。

它不新增花哨的饰品，而是接管**栏位数量**本身——让玩家的饰品栏随生存天数一点点长出来，并且可以精确安排「第几天、加几个、加哪种栏位」。

## 玩法流程

1. 击杀末影龙拿到龙蛋，用 **4 个下界之星 + 1 个龙蛋**（十字摆放，龙蛋居中）合成 **星陨 / Star Fall**
2. 右键使用：物品消耗，解锁史诗成就 **「渐强」**，从当天起开始计时
3. 每过一个游戏日，聊天栏提示 `渐强:饰品栏+1(累计1)`，对应原生饰品栏 +1 格
4. 想要「什么都能装」的通用栏位时，用 `/dailyslot convert @s curio 1` 把原生栏位换成通用位

未使用星陨的玩家不会获得任何栏位，也不会累计天数。

## 发放的是「原生」栏位

`head` `necklace` `ring` `belt` `body` `charm` `hands` `bracelet` `curio`

因此 GUI 图标、物品提示、死亡掉落规则、属性加成结算、客户端同步，全部沿用 Curios 自身逻辑，与其他饰品模组没有任何行为差异。另有一个本模组自建的通用栏位 `daily_curio`（可放任意物品，需手动转换获得）。

## 配置

`config/curios-star-fall-common.toml`

```toml
# 里程碑：到第 N 天一次性发放（默认空）
milestones = []

[general]
	intervalDays = 1        # 每几个游戏日发放一次；0 = 关闭
	slotsPerGrant = 1       # 每次几个
	defaultSlot = "curio"   # 每日发放的栏位类型
	maxSlotsPerType = 32    # 每种栏位发放上限；0 = 不限
	announce = true
```

想做到「第 7 天 +1 戒指、第 30 天 +2 项链」直接写：

```toml
milestones = ["day=7;slot=ring;count=1", "day=30;slot=necklace;count=2"]
```

## 命令

需要权限等级 2（单人开作弊 / 服务器 OP）。

| 命令 | 说明 |
| --- | --- |
| `/dailyslot status` | 查看配置、激活状态与自己的栏位 |
| `/dailyslot set <天> <数量> [栏位]` | 修改每日规则（写回配置文件） |
| `/dailyslot rule add\|remove\|list` | 管理里程碑 |
| `/dailyslot grant <目标> <栏位> <数量>` | 立即补发 |
| `/dailyslot convert <目标> <原栏位> <数量>` | 原生栏位 → 通用栏位 |
| `/dailyslot sync <目标>` | 重新同步栏位数量 |
| `/dailyslot reset <目标>` | 清空发放的栏位（栏内物品会掉落） |

## 兼容性

- Minecraft 1.20.1 / Forge 47.x
- Curios API 5.7.0+，已在 **5.12.1 / 5.14.1** 实测
- 与其他增加 Curios 栏位的模组共存：本模组只做 `Math.max` 式合并，不会覆盖更大的数值
- 旧版本存档数据自动迁移

## 授权

All Rights Reserved。整合包可直接收录本模组的未修改 jar。
```

## 三、版本（Create a version）

| 字段 | 填写 |
| --- | --- |
| Name | `1.3.0` |
| Version number | `1.3.0` |
| Release channel | `Release` |
| Game versions | `1.20.1` |
| Loaders | `Forge` |
| Dependencies | `Curios API` → `required`，version `>=5.7.0` |
| File | `curios-star-fall-1.3.0.jar` |
| Changelog | 见下 |

```markdown
- 更名为 Star Fall / 星陨饰品栏，modId 改为 curios_starfall
- 物品 id 改为 curios_starfall:star_fall，配置文件改为 config/curios-star-fall-common.toml
- 适配 Curios 5.14.1（仍兼容 5.12.1）
- 新增史诗成就「渐强」，使用星陨激活时解锁
- 发放提示改为 `渐强:饰品栏+1(累计1)` 格式
- 旧存档数据自动迁移
```

## 四、发布前检查清单

- [ ] `mods.toml` 的 version 与文件名一致（1.3.0）
- [ ] jar 内没有 `.build/`、`build/`、旧版 class 残留
- [ ] 上传的 jar 与实际测试跑通的版本一致（md5 对齐）
- [ ] Dependencies 里挂上 Curios，否则玩家会带着 `curios` 缺失崩溃
- [ ] 截图：Curios 界面多出来的栏位、星陨物品与合成表、成就弹窗
