# Changelog

## 1.3.0

**更名 & 适配 & 成就**

- 模组更名为 **Star Fall / 星陨饰品栏**，modId 改为 `curios_starfall`，物品 id 改为 `curios_starfall:star_fall`
- 配置文件更名为 `config/curios-star-fall-common.toml`
- 适配 Curios 5.14.1（同时兼容 5.12.1）：栏位修饰符、槽位 JSON 合并、持久化行为均无变化
- 新增史诗成就「渐强 / Crescendo」（challenge 边框），使用星陨激活时解锁
- 发放提示改为 `渐强:饰品栏+1(累计1)` 格式
- 旧存档数据自动迁移（`curios_xingyun` / `curios_api_curios_a_08a2` → `curios_starfall`）

## 1.2.0

- 发放目标改为 Curios 原生栏位（curio / ring / necklace / …），默认每天 +1 个原生 curio 栏位
- 新增 `milestones` 里程碑规则：第 a 天 +x 个某类型、第 b 天 +y 个某类型
- 通用栏位（`daily_curio`）不再自动发放，改为 `/dailyslot convert` 手动转换
- 新增物品「星陨」：4 个下界之星 + 1 个龙蛋（十字摆放），右键消耗并激活功能
- 功能默认关闭，未使用星陨的玩家不发放任何栏位
- 新增 `/dailyslot rule` / `convert` / `activate` 子命令

## 1.1.0

- 栏位显示名改为「饰品」，补齐 `curios.modifiers.daily_curio` 提示文本
- 新增 Forge 配置（间隔天数 / 每次数量 / 上限 / 提示开关）
- 新增 `/dailyslot` 命令族，可在游戏内修改配置并写回 toml
- 发放算法改为按整间隔结算，保留不足一个间隔的进度

## 1.0.0-r2

- 初版：每个游戏日为玩家增加 1 个 Curios 通用饰品栏
