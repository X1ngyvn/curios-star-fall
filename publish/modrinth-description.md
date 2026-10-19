# ⭐ Star Fall · 星陨饰品栏

**Grow your accessory slots over time — activate Star Fall, then earn new native Curios slots every in-game day.**

---

## What it does

Most trinket mods add *items*. Star Fall manages **how many slots you have**, and turns that into progression.

Craft **Star Fall** (4 nether stars + 1 dragon egg), right-click to activate, and from that day on your Curios slots grow on a schedule you control — +1 every day, +2 every 3 days, or *"day 7 → +1 ring slot, day 30 → +2 necklace slots"*.

Players who never use Star Fall gain nothing, so returning players can't log in with dozens of free slots.

## How it works

1. **Craft** — 4 nether stars around 1 dragon egg (cross shape, egg in the center)
2. **Activate** — right-click Star Fall. The item is consumed, the challenge advancement **Crescendo** unlocks, and the counter starts from that day
3. **Grow** — every in-game day the chat shows `渐强:饰品栏+1(累计1)` and the matching Curios slot gains one more slot
4. **Optional** — convert a native slot into a universal slot that accepts any item: `/dailyslot convert @s curio 1`

## Native slots, not custom ones

`head` · `necklace` · `ring` · `belt` · `body` · `charm` · `hands` · `bracelet` · `curio`

Because the granted slots *are* Curios' own slot types, GUI icons, tooltips, death drop rules, attribute calculation and client sync all follow Curios' logic. Nothing behaves differently from a vanilla Curios slot.

A separate universal slot (`daily_curio`, accepts any item) exists, but it is never granted automatically — convert to it only when you actually want it.

## Configuration

`config/curios-star-fall-common.toml`

```toml
# one-shot milestones, empty by default
milestones = []

[general]
	intervalDays    = 1       # grant every N in-game days; 0 = disabled
	slotsPerGrant   = 1       # how many slots per grant
	defaultSlot     = "curio" # which native slot type
	maxSlotsPerType = 32      # per-type cap; 0 = unlimited
	announce        = true    # chat feedback
```

Milestones are one-shot rewards that also catch up while offline:

```toml
milestones = ["day=7;slot=ring;count=1", "day=30;slot=necklace;count=2"]
```

## Commands

Requires permission level 2 (cheats in singleplayer / OP on servers).

| Command | Description |
| --- | --- |
| `/dailyslot status` | Show config, activation state and your slots |
| `/dailyslot set <days> <amount> [slot]` | Edit the daily rule (writes back to the config file) |
| `/dailyslot rule add\|remove\|list` | Manage milestones |
| `/dailyslot grant <targets> <slot> <amount>` | Grant slots immediately |
| `/dailyslot convert <targets> <from> <amount>` | Native slot → universal slot |
| `/dailyslot sync <targets>` | Re-apply modifiers (fix desync) |
| `/dailyslot reset <targets>` | Clear all granted slots (items inside are dropped) |

## Compatibility

- Minecraft **1.20.1** · Forge **47.x** · Java 17
- Curios API **5.7.0+** (tested on 5.12.1 and 5.14.1)
- Coexists with other mods that add Curios slots: sizes merge with `Math.max`, nothing is overwritten
- Older save data is migrated automatically

## 中文说明

**玩法**：4 个下界之星 + 1 个龙蛋（十字摆放）合成「星陨」→ 右键使用（消耗物品，解锁史诗成就「渐强」）→ 之后每过一个游戏日，Curios 里的原生饰品栏就 +1 格，聊天栏提示 `渐强:饰品栏+1(累计1)`。没用过星陨的玩家不会获得任何栏位。

**发放的是原生栏位**：`head` `necklace` `ring` `belt` `body` `charm` `hands` `bracelet` `curio`，所以图标、tooltip、掉落规则、属性结算、客户端同步全部沿用 Curios 自身逻辑，和原生栏位没有任何区别。另有一个「通用位」（可放任意物品），默认不发放，需要时用 `/dailyslot convert` 把原生位换过去。

**配置**：`config/curios-star-fall-common.toml`

```toml
milestones = []          # 里程碑，默认空；例："day=7;slot=ring;count=1"

[general]
	intervalDays    = 1  # 每 N 个游戏日发一次，0 = 关闭
	slotsPerGrant   = 1  # 每次发几个
	defaultSlot     = "curio"
	maxSlotsPerType = 32 # 每种栏位最多发多少，0 = 不限
	announce        = true
```

**命令**：`/dailyslot status`、`set <天> <数量> [栏位]`、`rule add|remove|list`、`grant <目标> <栏位> <数量>`、`convert <目标> <原栏位> <数量>`、`sync <目标>`、`reset <目标>`（需要权限等级 2）。

**兼容**：Minecraft 1.20.1 / Forge 47.x，Curios 5.7.0+（5.12.1、5.14.1 实测）。与其他增加 Curios 栏位的模组共存，容量按取较大值合并，不会覆盖别人给的数值。

---

Source & issues: [github.com/X1ngyvn/curios-star-fall](https://github.com/X1ngyvn/curios-star-fall)
