# Modrinth 发布清单

> 正文（Description）单独放在 [`modrinth-description.md`](modrinth-description.md)，中英双语，直接整份粘贴即可。

## 一、创建项目（Create a project）

| 字段 | 填写 |
| --- | --- |
| Project name | `Star Fall`（不要用 `curios-star-fall`，项目名里别塞前置模组名/类别） |
| Slug | 自动生成 `star-fall`，可手动改成 `star-fall` |
| Logo | 512×512 的 `star-fall-logo-512.png`（最近邻放大的像素图，别用 16×16 直接上传，会被插值糊掉） |
| Summary（**必须英文**，≤256 字符） | `Grow your Curios accessory slots over time: right-click Star Fall, then gain a configurable number of native slots every in-game day.`（131 字符） |
| Class | `Mod` |
| Main category | `Equipment` |
| Additional categories | `Game Mechanics`、`Adventure` |
| Allow Comments | ☑ 勾选 |
| Unlisted | ☐ 不勾 |

## 二、项目设置（Settings）

| 字段 | 填写 |
| --- | --- |
| Environment → Client | `required` |
| Environment → Server | `required` |
| Loaders | `Forge` |
| Game versions | `1.20.1` |
| License | `All Rights Reserved` |
| Links → Source | `https://github.com/X1ngyvn/curios-star-fall` |
| Links → Issues | `https://github.com/X1ngyvn/curios-star-fall/issues` |
| Dependencies | `Curios` → **Required**，version `>=5.7.0` |

## 三、Description

编辑器右上角把 `WYSIWYG` 切换成 **Markdown**（或点 `</>`），然后把
[`modrinth-description.md`](modrinth-description.md) 的**全部内容**粘进去。

结构：英文正文（What it does / How it works / Native slots / Configuration / Commands / Compatibility）→ 中文说明 → 仓库链接。

## 四、版本（Create a version）

| 字段 | 填写 |
| --- | --- |
| Name | `1.3.0` |
| Version number | `1.3.0` |
| Release channel | `Release` |
| Game versions | `1.20.1` |
| Loaders | `Forge` |
| Dependencies | `Curios API` → `required`，`>=5.7.0` |
| File | `curios-star-fall-1.3.0.jar` |

Changelog：

```markdown
- Renamed to Star Fall / 星陨饰品栏, mod id is now `curios_starfall`
- Item id is now `curios_starfall:star_fall`; config moved to `config/curios-star-fall-common.toml`
- Tested against Curios 5.14.1 (still compatible with 5.12.1)
- New challenge advancement "Crescendo", unlocked by activating Star Fall
- Slot grant message is now `渐强:饰品栏+1(累计1)`
- Native slot base sizes are no longer overridden — Curios defaults are kept
- Legacy save data migrates automatically
```

## 五、截图（Gallery）

建议顺序：

1. Curios 界面：激活前后栏位对比（最能说明卖点）
2. 合成表：4 下界之星 + 龙蛋
3. 聊天栏提示 `渐强:饰品栏+1(累计1)`
4. 成就「渐强」解锁弹窗
5. 配置文件截图

## 六、发布前检查

- [ ] Summary 是英文（平台硬性要求），中文放在 Description 里
- [ ] Logo 不是纯色/渐变，也不是游戏原图
- [ ] Dependencies 挂了 Curios，否则玩家会在缺前置时崩溃
- [ ] 上传的 jar 与本地跑通的版本一致（比对 md5）
- [ ] 版本号 / 文件名 / mods.toml 三者一致（都是 1.3.0）
