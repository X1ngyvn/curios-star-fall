package cn.blockforge.generated.curiosapicuriosa08a2;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置定义。
 *
 * <p>文件位置：{@code config/curios-star-fall-common.toml}
 *
 * <p>结构：
 * <pre>
 * [general]
 *   intervalDays = 1        # 每多少游戏日发放一次（0 = 关闭每日发放）
 *   slotsPerGrant = 1       # 每次发几个
 *   defaultSlot = "curio"   # 每日发放用哪种原生栏位
 *   maxSlotsPerType = 32    # 每种栏位最多发放多少个（0 = 不限制）
 *   announce = true
 * milestones = ["day=7;slot=ring;count=1", "day=30;slot=necklace;count=2"]
 * </pre>
 *
 * <p>milestones 里每条规则表示「世界天数 >= day 时，一次性给 slot 类型的栏位 count 个」，
 * 只发一次；离线跨过天数会补发。slot 可用值见 {@link DailySlotHandler#NATIVE_SLOTS}。
 */
public final class DailySlotConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedMod.MOD_ID);

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue INTERVAL_DAYS;
    public static final ForgeConfigSpec.IntValue SLOTS_PER_GRANT;
    public static final ForgeConfigSpec.ConfigValue<String> DEFAULT_SLOT;
    public static final ForgeConfigSpec.IntValue MAX_PER_TYPE;
    public static final ForgeConfigSpec.BooleanValue ANNOUNCE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> MILESTONES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment(
                "每日饰品栏 / Daily Accessory Slots",
                "每 intervalDays 个游戏日，自动为玩家增加 slotsPerGrant 个 defaultSlot 类型的 Curios 原生栏位。",
                "milestones 可以按天追加「第 N 天 +X 个某种栏位」的一次性奖励。",
                "功能默认未激活：需要先用物品「星陨」右键激活，激活后才开始按天数发放。",
                "游戏内可用 /dailyslot 系列命令实时修改，修改后会写回本文件。");

        builder.push("general");
        INTERVAL_DAYS = builder
                .comment("每多少个游戏日发放一次（24000 tick = 1 游戏日）。1 = 每天发放，0 = 关闭每日发放。")
                .defineInRange("intervalDays", 1, 0, 100000);
        SLOTS_PER_GRANT = builder
                .comment("每次发放的栏位数量。")
                .defineInRange("slotsPerGrant", 1, 0, 64);
        DEFAULT_SLOT = builder
                .comment("每日发放使用的栏位类型。默认 curio（原生通用位）。",
                        "可用：head/necklace/ring/belt/body/charm/hands/bracelet/curio，",
                        "或本模组的通用位 daily_curio（一般不建议，通用位建议用 /dailyslot convert 手动转换）。")
                .define("defaultSlot", "curio");
        MAX_PER_TYPE = builder
                .comment("每种栏位最多发放多少个（不含原版自带的基础数量）。0 = 不限制。")
                .defineInRange("maxSlotsPerType", 32, 0, 256);
        ANNOUNCE = builder
                .comment("发放栏位时是否在聊天栏提示玩家。")
                .define("announce", true);
        builder.pop();

        MILESTONES = builder
                .comment("里程碑规则，默认空列表（留空=只按上面的每日规则发放，不会额外给任何栏位）。",
                        "需要时自行追加，格式：\"day=天数;slot=栏位类型;count=数量\"（分号分隔，顺序任意）。",
                        "写法举例：day=7;slot=ring;count=1 → 世界天数到第 7 天时额外 +1 个戒指栏位（只发一次）。",
                        "离线期间跨过的天数会补发；同一天同类型的规则以最后一条为准。")
                .defineList("milestones", List.of(), value -> value instanceof String);

        SPEC = builder.build();
    }

    private DailySlotConfig() {
    }

    // ------------------------------------------------------------------
    // 读取（全部带兜底，配置没加载时用默认值）
    // ------------------------------------------------------------------

    public static int intervalDays() {
        return readInt(INTERVAL_DAYS, 0, 100000, 1);
    }

    public static int slotsPerGrant() {
        return readInt(SLOTS_PER_GRANT, 0, 64, 1);
    }

    public static String defaultSlot() {
        try {
            String raw = DEFAULT_SLOT.get();
            return raw == null || raw.isBlank() ? "curio" : raw.trim();
        } catch (Throwable ignored) {
            return "curio";
        }
    }

    public static int maxSlotsPerType() {
        return readInt(MAX_PER_TYPE, 0, 256, 32);
    }

    public static boolean announce() {
        try {
            return ANNOUNCE.get();
        } catch (Throwable ignored) {
            return true;
        }
    }

    // ------------------------------------------------------------------
    // 里程碑规则
    // ------------------------------------------------------------------

    public static final class Rule {
        public final long day;
        public final String slot;
        public final int count;

        Rule(long day, String slot, int count) {
            this.day = day;
            this.slot = slot;
            this.count = count;
        }

        /** 已发放记录的键：同一天同一类型视为同一条规则。 */
        public String key() {
            return day + ":" + slot;
        }

        public String toConfigLine() {
            return "day=" + day + ";slot=" + slot + ";count=" + count;
        }
    }

    private static List<String> cachedRaw = List.of();
    private static List<Rule> cachedRules = List.of();

    public static List<Rule> rules() {
        List<String> raw;
        try {
            raw = new ArrayList<>(MILESTONES.get());
        } catch (Throwable ignored) {
            return List.of();
        }
        if (!raw.equals(cachedRaw)) {
            List<Rule> parsed = new ArrayList<>();
            for (String line : raw) {
                Rule rule = parseRule(line);
                if (rule != null) {
                    parsed.add(rule);
                }
            }
            cachedRaw = raw;
            cachedRules = List.copyOf(parsed);
        }
        return cachedRules;
    }

    public static Rule parseRule(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        Long day = null;
        String slot = null;
        Integer count = null;
        for (String token : line.split(";")) {
            String[] kv = token.split("=", 2);
            if (kv.length != 2) {
                continue;
            }
            String key = kv[0].trim().toLowerCase();
            String value = kv[1].trim();
            try {
                switch (key) {
                    case "day", "days" -> day = Long.parseLong(value);
                    case "slot", "type" -> slot = value;
                    case "count", "amount", "num" -> count = Integer.parseInt(value);
                    default -> {
                    }
                }
            } catch (NumberFormatException ex) {
                LOGGER.warn("[{}] 无法解析里程碑规则片段 '{}'（{}）", GeneratedMod.MOD_ID, token, line);
            }
        }
        if (day == null || slot == null || slot.isBlank()) {
            LOGGER.warn("[{}] 里程碑规则缺少 day 或 slot：'{}'", GeneratedMod.MOD_ID, line);
            return null;
        }
        return new Rule(Math.max(0L, day), slot.trim(), Math.max(1, count == null ? 1 : count));
    }

    // ------------------------------------------------------------------
    // 游戏内修改（会写回 toml）
    // ------------------------------------------------------------------

    public static boolean applyDailyRule(int intervalDays, int slotsPerGrant, String slot) {
        if (!SPEC.isLoaded()) {
            return false;
        }
        INTERVAL_DAYS.set(Math.max(0, Math.min(100000, intervalDays)));
        SLOTS_PER_GRANT.set(Math.max(0, Math.min(64, slotsPerGrant)));
        if (slot != null && !slot.isBlank()) {
            DEFAULT_SLOT.set(slot.trim());
        }
        INTERVAL_DAYS.save();
        return true;
    }

    public static boolean addMilestone(long day, String slot, int count) {
        if (!SPEC.isLoaded()) {
            return false;
        }
        List<String> list = new ArrayList<>(rawMilestones());
        String target = day + ":" + slot;
        list.removeIf(line -> {
            Rule existing = parseRule(line);
            return existing != null && existing.key().equals(target);
        });
        list.add(new Rule(Math.max(0L, day), slot, Math.max(1, count)).toConfigLine());
        MILESTONES.set(list);
        MILESTONES.save();
        return true;
    }

    public static boolean removeMilestone(long day, String slot) {
        if (!SPEC.isLoaded()) {
            return false;
        }
        List<String> list = new ArrayList<>(rawMilestones());
        String target = day + ":" + slot;
        boolean removed = list.removeIf(line -> {
            Rule existing = parseRule(line);
            return existing != null && existing.key().equals(target);
        });
        if (removed) {
            MILESTONES.set(list);
            MILESTONES.save();
        }
        return removed;
    }

    private static List<String> rawMilestones() {
        try {
            return new ArrayList<>(MILESTONES.get());
        } catch (Throwable ignored) {
            return new ArrayList<>();
        }
    }

    private static int readInt(ForgeConfigSpec.ConfigValue<Integer> value, int min, int max, int fallback) {
        try {
            return Math.max(min, Math.min(max, value.get()));
        } catch (Throwable ignored) {
            return fallback;
        }
    }
}
