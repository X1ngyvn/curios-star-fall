# CurseForge 发布文本

## 一、项目字段

| 字段 | 建议填写 |
| --- | --- |
| Project name | `Star Fall` |
| Project summary（≤255 字符） | `用「星陨」开启成长：每过一个游戏日，Curios 饰品栏就多一点。天数/数量/栏位类型全部可配置，附史诗成就「渐强」。` |
| Game | `Minecraft` |
| Category（主） | `Adventure and RPG` |
| Category（次） | `Armor, Tools, and Weapons` |
| Game version | `1.20.1` |
| Mod loader | `Forge` |
| License | `All Rights Reserved` |
| Relations | Required dependency：`Curios API`（≥5.7.0） |
| Source | 仓库地址 |
| Avatar / Cover | 用星陨贴图 + Curios 界面截图 |

## 二、Description（粘贴到描述编辑器，Markdown / 基础 HTML 均可）

```markdown
<h2>Star Fall · 星陨饰品栏</h2>

<p>用一颗「星陨」开启成长：每过一个游戏日，Curios 饰品栏就多一点。</p>

<p>它不新增花哨的饰品，而是接管<strong>栏位数量</strong>本身——让玩家的饰品栏随生存天数一点点长出来，并且可以精确安排「第几天、加几个、加哪种栏位」。</p>

<h3>玩法流程</h3>
<ol>
  <li>击杀末影龙拿到龙蛋，用 <strong>4 个下界之星 + 1 个龙蛋</strong>（十字摆放，龙蛋居中）合成 <strong>星陨 / Star Fall</strong></li>
  <li>右键使用：物品消耗，解锁史诗成就 <strong>「渐强」</strong>，从当天起开始计时</li>
  <li>每过一个游戏日，聊天栏提示 <code>渐强:饰品栏+1(累计1)</code>，对应原生饰品栏 +1 格</li>
  <li>需要「什么都能装」的通用栏位时，用 <code>/dailyslot convert @s curio 1</code> 把原生栏位换成通用位</li>
</ol>

<p>未使用星陨的玩家不会获得任何栏位，也不会累计天数。</p>

<h3>发放的是「原生」栏位</h3>
<p><code>head</code> <code>necklace</code> <code>ring</code> <code>belt</code> <code>body</code> <code>charm</code> <code>hands</code> <code>bracelet</code> <code>curio</code></p>
<p>因此 GUI 图标、物品提示、死亡掉落规则、属性加成结算、客户端同步全部沿用 Curios 自身逻辑，与其他饰品模组没有任何行为差异。另有一个自建通用栏位 <code>daily_curio</code>（可放任意物品，需手动转换获得）。</p>

<h3>配置</h3>
<p><code>config/curios-star-fall-common.toml</code></p>
<pre>
# 里程碑：到第 N 天一次性发放（默认空）
milestones = []

[general]
    intervalDays = 1        # 每几个游戏日发放一次；0 = 关闭
    slotsPerGrant = 1       # 每次几个
    defaultSlot = "curio"   # 每日发放的栏位类型
    maxSlotsPerType = 32    # 每种栏位发放上限；0 = 不限
    announce = true
</pre>
<p>「第 7 天 +1 戒指、第 30 天 +2 项链」直接写：</p>
<pre>milestones = ["day=7;slot=ring;count=1", "day=30;slot=necklace;count=2"]</pre>

<h3>命令（权限等级 2 / OP）</h3>
<table>
  <tr><th>命令</th><th>说明</th></tr>
  <tr><td>/dailyslot status</td><td>查看配置、激活状态与自己的栏位</td></tr>
  <tr><td>/dailyslot set &lt;天&gt; &lt;数量&gt; [栏位]</td><td>修改每日规则（写回配置文件）</td></tr>
  <tr><td>/dailyslot rule add|remove|list</td><td>管理里程碑</td></tr>
  <tr><td>/dailyslot grant &lt;目标&gt; &lt;栏位&gt; &lt;数量&gt;</td><td>立即补发</td></tr>
  <tr><td>/dailyslot convert &lt;目标&gt; &lt;原栏位&gt; &lt;数量&gt;</td><td>原生栏位 → 通用栏位</td></tr>
  <tr><td>/dailyslot sync &lt;目标&gt;</td><td>重新同步栏位数量</td></tr>
  <tr><td>/dailyslot reset &lt;目标&gt;</td><td>清空发放的栏位（栏内物品会掉落）</td></tr>
</table>

<h3>兼容性</h3>
<ul>
  <li>Minecraft 1.20.1 / Forge 47.x</li>
  <li>Curios API 5.7.0+，已在 5.12.1 / 5.14.1 实测</li>
  <li>与其他增加 Curios 栏位的模组共存，只做 Math.max 式合并，不覆盖更大的数值</li>
  <li>旧版本存档数据自动迁移</li>
</ul>

<h3>授权</h3>
<p>All Rights Reserved。整合包可直接收录本模组的未修改 jar。</p>
```

## 三、上传文件（Files）

| 字段 | 填写 |
| --- | --- |
| File | `curios-star-fall-1.3.0.jar` |
| Display name | `Star Fall 1.3.0` |
| Release type | `Release` |
| Game version | `1.20.1` |
| Mod loader | `Forge` |
| Changelog | 见下 |

```text
1.3.0
- 更名为 Star Fall / 星陨饰品栏，modId 改为 curios_starfall
- 物品 id 改为 curios_starfall:star_fall，配置改为 config/curios-star-fall-common.toml
- 适配 Curios 5.14.1（仍兼容 5.12.1）
- 新增史诗成就「渐强」，使用星陨激活时解锁
- 发放提示改为「渐强:饰品栏+1(累计1)」
- 旧存档数据自动迁移
```

## 四、发布前检查清单

- [ ] Relations 里把 Curios 标为 **Required dependency**（CurseForge 审核会检查）
- [ ] 项目分类不要选「API and Library」，本模组是玩法向
- [ ] 描述里不要放外链下载（外链会被审核打回）
- [ ] 版本号、文件名、mods.toml 三者一致
- [ ] 首次提交需要等人工审核（通常数小时到 1 天）
