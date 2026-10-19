package cn.blockforge.generated.curiosapicuriosa08a2;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

/**
 * 栏位发放与同步。
 *
 * <p>发放方式与 Curios 原生栏位完全一致：直接给原生槽位类型（curio/ring/...）叠加
 * {@code addPermanentSlotModifier} 永久修饰符，因此渲染、tooltip、掉落规则、属性结算、
 * NBT 持久化、客户端同步全部沿用 Curios 自身逻辑。
 *
 * <p>本模组自建的通用栏位 {@link #UNIVERSAL_SLOT} 基础容量为 0，只能通过
 * {@code /dailyslot convert} 从原生栏位手动转换获得。
 */
public final class DailySlotHandler {

    /** 原生栏位类型（与 Curios 自带 data/curios/curios/slots/*.json 对应）。 */
    public static final List<String> NATIVE_SLOTS =
            List.of("head", "necklace", "ring", "belt", "body", "charm", "hands", "bracelet", "curio");

    /** 本模组的通用栏位（可放任意物品）。 */
    public static final String UNIVERSAL_SLOT = "daily_curio";

    /** 本模组会维护的全部栏位类型。 */
    public static final List<String> ALL_SLOTS;

    static {
        List<String> all = new ArrayList<>(NATIVE_SLOTS);
        all.add(UNIVERSAL_SLOT);
        ALL_SLOTS = List.copyOf(all);
    }

    private static final String MODIFIER_NAME = GeneratedMod.MOD_ID + ".slot";
    private static final long TICKS_PER_DAY = 24000L;

    // 全部数据集中放在玩家持久化数据的一个子标签里
    private static final String KEY_LAST_DAY = "lastDay";
    private static final String KEY_GRANTED = "granted";
    private static final String KEY_DONE = "done";
    /** 是否已用「星陨」激活；未激活不发放任何栏位。 */
    private static final String KEY_UNLOCKED = "unlocked";

    /** 历史 modId，仅用于把老存档数据迁移到新命名空间（顺序即优先级）。 */
    private static final String[] LEGACY_MOD_IDS = {"curios_xingyun", "curios_api_curios_a_08a2"};
    private static final String LEGACY_MOD_ID = LEGACY_MOD_IDS[LEGACY_MOD_IDS.length - 1];

    // v1.1.0 及更早版本的扁平键，仅用于迁移
    private static final String OLD_EARNED = LEGACY_MOD_ID + ":earned_slots";
    private static final String OLD_LAST_DAY = LEGACY_MOD_ID + ":last_granted_day";
    private static final String OLD_SYNC = LEGACY_MOD_ID + ":needs_slot_sync";

    // ------------------------------------------------------------------
    // 事件
    // ------------------------------------------------------------------

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.player;
        if (player.f_19797_ % 20 != 0) {
            return;
        }
        MinecraftServer server = player.f_8924_;
        if (server == null) {
            return;
        }

        long day = server.m_129783_().m_46468_() / TICKS_PER_DAY;
        CompoundTag root = player.getPersistentData();
        CompoundTag data = dataOf(root);
        migrate(root, data);

        if (!data.m_128471_(KEY_UNLOCKED)) {
            return;
        }

        if (!data.m_128441_(KEY_LAST_DAY)) {
            data.m_128356_(KEY_LAST_DAY, day);
        }

        CompoundTag granted = data.m_128469_(KEY_GRANTED);
        CompoundTag done = data.m_128469_(KEY_DONE);
        Map<String, Integer> delta = new LinkedHashMap<>();

        // 1) 循环发放：每 intervalDays 天 +slotsPerGrant 个 defaultSlot
        int interval = DailySlotConfig.intervalDays();
        int perGrant = DailySlotConfig.slotsPerGrant();
        long lastDay = data.m_128454_(KEY_LAST_DAY);
        if (interval > 0 && perGrant > 0 && day - lastDay >= interval) {
            long periods = (day - lastDay) / interval;
            long gain = periods * (long) perGrant;
            // 只推进到已结算的整间隔，保留不足一个间隔的进度
            data.m_128356_(KEY_LAST_DAY, lastDay + periods * (long) interval);
            addGranted(granted, DailySlotConfig.defaultSlot(), (int) Math.min(gain, 100000), delta);
        }

        // 2) 里程碑：世界天数到达 day 时一次性发放
        for (DailySlotConfig.Rule rule : DailySlotConfig.rules()) {
            if (day >= rule.day && !done.m_128471_(rule.key())) {
                done.m_128379_(rule.key(), true);
                addGranted(granted, rule.slot, rule.count, delta);
            }
        }

        if (delta.isEmpty()) {
            return;
        }

        data.m_128365_(KEY_GRANTED, granted);
        data.m_128365_(KEY_DONE, done);
        syncAll(player, granted);

        if (!delta.isEmpty() && DailySlotConfig.announce()) {
            announce(player, delta, granted);
        }
    }

    /** 重生 / 换维度时把数据带到新实体。 */
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        CompoundTag source = event.getOriginal().getPersistentData().m_128469_(GeneratedMod.MOD_ID);
        if (source.m_128456_()) {
            return;
        }
        CompoundTag target = dataOf(event.getEntity().getPersistentData());
        if (source.m_128441_(KEY_LAST_DAY)) {
            target.m_128356_(KEY_LAST_DAY, source.m_128454_(KEY_LAST_DAY));
        }
        if (source.m_128471_(KEY_UNLOCKED)) {
            target.m_128379_(KEY_UNLOCKED, true);
        }
        if (source.m_128441_(KEY_GRANTED)) {
            target.m_128365_(KEY_GRANTED, source.m_128469_(KEY_GRANTED).m_6426_());
        }
        if (source.m_128441_(KEY_DONE)) {
            target.m_128365_(KEY_DONE, source.m_128469_(KEY_DONE).m_6426_());
        }
    }

    /** 登录时重新应用一次修饰符（Curios 能力重建后栏位不会丢）。 */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer) event.getEntity();
        CompoundTag root = player.getPersistentData();
        CompoundTag data = dataOf(root);
        migrate(root, data);
        if (data.m_128471_(KEY_UNLOCKED)) {
            syncAll(player, data.m_128469_(KEY_GRANTED));
        }
    }

    // ------------------------------------------------------------------
    // 数据读写
    // ------------------------------------------------------------------

    private static CompoundTag dataOf(CompoundTag root) {
        CompoundTag data = root.m_128469_(GeneratedMod.MOD_ID);
        // getCompound 在不存在时返回新标签，这里放回去确保后续修改能保存
        root.m_128365_(GeneratedMod.MOD_ID, data);
        return data;
    }

    private static void migrate(CompoundTag root, CompoundTag data) {
        // 旧 modId（curios_xingyun / curios_api_curios_a_08a2）留下的整块数据
        String legacyId = null;
        for (String candidate : LEGACY_MOD_IDS) {
            if (data.m_128456_() && root.m_128441_(candidate)) {
                legacyId = candidate;
                break;
            }
        }
        if (legacyId != null) {
            CompoundTag legacy = root.m_128469_(legacyId);
            if (legacy.m_128471_(KEY_UNLOCKED)) {
                data.m_128379_(KEY_UNLOCKED, true);
            }
            if (legacy.m_128441_(KEY_LAST_DAY)) {
                data.m_128356_(KEY_LAST_DAY, legacy.m_128454_(KEY_LAST_DAY));
            }
            if (legacy.m_128441_(KEY_GRANTED)) {
                data.m_128365_(KEY_GRANTED, legacy.m_128469_(KEY_GRANTED).m_6426_());
            }
            if (legacy.m_128441_(KEY_DONE)) {
                data.m_128365_(KEY_DONE, legacy.m_128469_(KEY_DONE).m_6426_());
            }
            root.m_128473_(legacyId);
        }

        if (root.m_128441_(OLD_EARNED)) {
            // 老版本玩家早就在用这个功能，直接视为已激活
            data.m_128379_(KEY_UNLOCKED, true);
            if (!data.m_128441_(KEY_GRANTED)) {
                CompoundTag granted = new CompoundTag();
                int earned = root.m_128451_(OLD_EARNED);
                if (earned > 0) {
                    granted.m_128405_(UNIVERSAL_SLOT, earned);
                }
                data.m_128365_(KEY_GRANTED, granted);
                if (root.m_128441_(OLD_LAST_DAY)) {
                    data.m_128356_(KEY_LAST_DAY, root.m_128454_(OLD_LAST_DAY));
                }
            }
            root.m_128473_(OLD_EARNED);
            root.m_128473_(OLD_LAST_DAY);
            root.m_128473_(OLD_SYNC);
        }

        // 已经拿到过栏位的一律视为已激活（例如管理员手动补发过）
        if (!data.m_128471_(KEY_UNLOCKED) && hasAnyGranted(data.m_128469_(KEY_GRANTED))) {
            data.m_128379_(KEY_UNLOCKED, true);
        }
    }

    private static boolean hasAnyGranted(CompoundTag granted) {
        for (String key : granted.m_128431_()) {
            if (granted.m_128451_(key) > 0) {
                return true;
            }
        }
        return false;
    }

    /** 记录发放数量（受每类上限约束），成功增加时写入 delta。 */
    private static void addGranted(CompoundTag granted, String slot, int amount, Map<String, Integer> delta) {
        if (slot == null || slot.isBlank() || amount <= 0) {
            return;
        }
        int cap = DailySlotConfig.maxSlotsPerType();
        int current = granted.m_128451_(slot);
        int target = current + amount;
        if (cap > 0) {
            target = Math.min(target, cap);
        }
        int added = target - current;
        if (added <= 0) {
            return;
        }
        granted.m_128405_(slot, target);
        delta.merge(slot, added, Integer::sum);
    }

    // ------------------------------------------------------------------
    // 同步到 Curios
    // ------------------------------------------------------------------

    /**
     * 把各栏位的发放数量重新写入 Curios 永久修饰符。
     *
     * @return 至少应用了一个栏位类型时为 true
     */
    public static boolean syncAll(ServerPlayer player, CompoundTag granted) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        if (!optional.isPresent()) {
            return false;
        }
        AtomicBoolean applied = new AtomicBoolean(false);
        optional.ifPresent(handler -> {
            for (String slot : ALL_SLOTS) {
                if (!handler.getCurios().containsKey(slot)) {
                    continue;
                }
                UUID id = modifierId(player, slot);
                handler.removeSlotModifier(slot, id);
                int count = granted.m_128451_(slot);
                if (count > 0) {
                    handler.addPermanentSlotModifier(
                            slot, id, MODIFIER_NAME, count, AttributeModifier.Operation.ADDITION);
                }
                applied.set(true);
            }
        });
        return applied.get();
    }

    private static UUID modifierId(ServerPlayer player, String slot) {
        String key = GeneratedMod.MOD_ID + ":slot:" + slot + ":" + player.m_20148_();
        return UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
    }

    // ------------------------------------------------------------------
    // 激活（使用「星陨」）
    // ------------------------------------------------------------------

    public static boolean isUnlocked(ServerPlayer player) {
        return dataOf(player.getPersistentData()).m_128471_(KEY_UNLOCKED);
    }

    /**
     * 激活每日饰品栏：从当前游戏日开始计时，已经过去的里程碑不补发。
     *
     * @return true 表示本次激活成功（此前未激活）
     */
    public static boolean activate(ServerPlayer player) {
        CompoundTag data = dataOf(player.getPersistentData());
        if (data.m_128471_(KEY_UNLOCKED)) {
            return false;
        }
        long day = 0L;
        MinecraftServer server = player.f_8924_;
        if (server != null) {
            day = server.m_129783_().m_46468_() / TICKS_PER_DAY;
        }
        data.m_128379_(KEY_UNLOCKED, true);
        data.m_128356_(KEY_LAST_DAY, day);

        CompoundTag done = data.m_128469_(KEY_DONE);
        for (DailySlotConfig.Rule rule : DailySlotConfig.rules()) {
            if (rule.day <= day) {
                done.m_128379_(rule.key(), true);
            }
        }
        data.m_128365_(KEY_DONE, done);

        syncAll(player, data.m_128469_(KEY_GRANTED));
        awardActivation(player);
        return true;
    }

    /** 解锁史诗成就「渐强」。 */
    private static void awardActivation(ServerPlayer player) {
        MinecraftServer server = player.f_8924_;
        if (server == null) {
            return;
        }
        Advancement advancement = server.m_129889_()
                .m_136041_(new ResourceLocation(GeneratedMod.MOD_ID, "star_fall_activate"));
        if (advancement != null) {
            player.m_8960_().m_135988_(advancement, "activate");
        }
    }

    // ------------------------------------------------------------------
    // 供命令使用
    // ------------------------------------------------------------------

    public static CompoundTag grantedOf(ServerPlayer player) {
        return dataOf(player.getPersistentData()).m_128469_(KEY_GRANTED);
    }

    public static int grantedOf(ServerPlayer player, String slot) {
        return grantedOf(player).m_128451_(slot);
    }

    public static void setGranted(ServerPlayer player, String slot, int count) {
        CompoundTag root = player.getPersistentData();
        CompoundTag data = dataOf(root);
        CompoundTag granted = data.m_128469_(KEY_GRANTED);
        if (count <= 0) {
            granted.m_128473_(slot);
        } else {
            granted.m_128405_(slot, Math.min(count, 4096));
        }
        data.m_128365_(KEY_GRANTED, granted);
        syncAll(player, granted);
    }

    /** 玩家当前实际可用的栏位数量（Curios 侧真实值，含原版基础数量）。 */
    public static int actualSlots(ServerPlayer player, String slot) {
        LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(player);
        if (!optional.isPresent()) {
            return 0;
        }
        return optional
                .map(handler -> handler.getStacksHandler(slot)
                        .map(stacks -> stacks.getStacks().getSlots())
                        .orElse(0))
                .orElse(0);
    }

    /** 把 count 个 from 类型栏位转换为通用栏位。返回实际转换数量。 */
    public static int convertToUniversal(ServerPlayer player, String from, int count) {
        if (UNIVERSAL_SLOT.equals(from) || !ALL_SLOTS.contains(from) || count <= 0) {
            return 0;
        }
        CompoundTag granted = grantedOf(player);
        int available = granted.m_128451_(from);
        int moved = Math.min(available, count);
        if (moved <= 0) {
            return 0;
        }
        granted.m_128405_(from, available - moved);
        granted.m_128405_(UNIVERSAL_SLOT, granted.m_128451_(UNIVERSAL_SLOT) + moved);
        dataOf(player.getPersistentData()).m_128365_(KEY_GRANTED, granted);
        syncAll(player, granted);
        return moved;
    }

    public static void resetAll(ServerPlayer player) {
        CompoundTag data = dataOf(player.getPersistentData());
        CompoundTag granted = data.m_128469_(KEY_GRANTED);
        for (String key : new ArrayList<>(granted.m_128431_())) {
            granted.m_128473_(key);
        }
        data.m_128365_(KEY_GRANTED, granted);
        syncAll(player, granted);
    }

    // ------------------------------------------------------------------
    // 提示消息
    // ------------------------------------------------------------------

    private static void announce(ServerPlayer player, Map<String, Integer> delta, CompoundTag granted) {
        MutableComponent body = Component.m_237113_("");
        boolean first = true;
        for (Map.Entry<String, Integer> entry : delta.entrySet()) {
            if (!first) {
                body.m_130946_("、");
            }
            first = false;
            body.m_7220_(Component.m_237110_(
                    GeneratedMod.MOD_ID + ".message.granted.part",
                    slotName(entry.getKey()),
                    entry.getValue(),
                    granted.m_128451_(entry.getKey())));
        }
        player.m_5661_(Component.m_237110_(GeneratedMod.MOD_ID + ".message.granted", body), false);
    }

    public static Component slotName(String slot) {
        return Component.m_237115_("curios.identifier." + slot);
    }
}
