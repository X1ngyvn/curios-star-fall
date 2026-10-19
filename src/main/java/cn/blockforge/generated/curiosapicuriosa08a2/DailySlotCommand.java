package cn.blockforge.generated.curiosapicuriosa08a2;

import java.util.Collection;
import java.util.List;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 游戏内命令。
 *
 * <pre>
 * /dailyslot                                  查看配置与自己拥有的栏位
 * /dailyslot set &lt;天数&gt; &lt;数量&gt; [栏位类型]     修改“每 X 天 +Y 个 Z 栏位”（默认 curio）
 * /dailyslot rule add &lt;天数&gt; &lt;栏位&gt; &lt;数量&gt;   追加一条里程碑规则
 * /dailyslot rule remove &lt;天数&gt; &lt;栏位&gt;        删除一条里程碑规则
 * /dailyslot rule list                        列出所有里程碑规则
 * /dailyslot grant &lt;目标&gt; &lt;栏位&gt; &lt;数量&gt;       立即补发（调试 / 补偿）
 * /dailyslot convert &lt;目标&gt; &lt;原栏位&gt; &lt;数量&gt;   把原生栏位转成本模组的通用栏位
 * /dailyslot sync &lt;目标&gt;                     重新同步栏位数量
 * /dailyslot reset &lt;目标&gt;                    清空所有发放的栏位
 * </pre>
 */
public final class DailySlotCommand {

    private static final String P = GeneratedMod.MOD_ID + ".";

    private static final SuggestionProvider<CommandSourceStack> ALL_SLOT_SUGGESTIONS = (ctx, builder) -> {
        for (String id : DailySlotHandler.ALL_SLOTS) {
            builder.suggest(id);
        }
        return builder.buildFuture();
    };

    private static final SuggestionProvider<CommandSourceStack> NATIVE_SLOT_SUGGESTIONS = (ctx, builder) -> {
        for (String id : DailySlotHandler.NATIVE_SLOTS) {
            builder.suggest(id);
        }
        return builder.buildFuture();
    };

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.m_82127_("dailyslot")
                        .executes(ctx -> status(ctx.getSource()))
                        .then(Commands.m_82127_("status")
                                .executes(ctx -> status(ctx.getSource())))

                        .then(Commands.m_82127_("set")
                                .requires(src -> src.m_6761_(2))
                                .then(Commands.m_82129_("days", IntegerArgumentType.integer(0, 100000))
                                        .then(Commands.m_82129_("slots", IntegerArgumentType.integer(0, 64))
                                                .executes(ctx -> setRule(ctx, null))
                                                .then(Commands.m_82129_("slot", StringArgumentType.word())
                                                        .suggests(ALL_SLOT_SUGGESTIONS)
                                                        .executes(ctx -> setRule(
                                                                ctx, StringArgumentType.getString(ctx, "slot")))))))

                        .then(Commands.m_82127_("rule")
                                .requires(src -> src.m_6761_(2))
                                .then(Commands.m_82127_("add")
                                        .then(Commands.m_82129_("day", IntegerArgumentType.integer(0, 1000000))
                                                .then(Commands.m_82129_("slot", StringArgumentType.word())
                                                        .suggests(ALL_SLOT_SUGGESTIONS)
                                                        .then(Commands.m_82129_("count", IntegerArgumentType.integer(1, 256))
                                                                .executes(DailySlotCommand::ruleAdd)))))
                                .then(Commands.m_82127_("remove")
                                        .then(Commands.m_82129_("day", IntegerArgumentType.integer(0, 1000000))
                                                .then(Commands.m_82129_("slot", StringArgumentType.word())
                                                        .suggests(ALL_SLOT_SUGGESTIONS)
                                                        .executes(DailySlotCommand::ruleRemove))))
                                .then(Commands.m_82127_("list").executes(DailySlotCommand::ruleList)))

                        .then(Commands.m_82127_("grant")
                                .requires(src -> src.m_6761_(2))
                                .then(Commands.m_82129_("targets", EntityArgument.m_91470_())
                                        .then(Commands.m_82129_("slot", StringArgumentType.word())
                                                .suggests(ALL_SLOT_SUGGESTIONS)
                                                .then(Commands.m_82129_("count", IntegerArgumentType.integer(1, 256))
                                                        .executes(DailySlotCommand::grant)))))

                        .then(Commands.m_82127_("convert")
                                .requires(src -> src.m_6761_(2))
                                .then(Commands.m_82129_("targets", EntityArgument.m_91470_())
                                        .then(Commands.m_82129_("from", StringArgumentType.word())
                                                .suggests(NATIVE_SLOT_SUGGESTIONS)
                                                .then(Commands.m_82129_("count", IntegerArgumentType.integer(1, 256))
                                                        .executes(DailySlotCommand::convert)))))

                        .then(Commands.m_82127_("activate")
                                .requires(src -> src.m_6761_(2))
                                .then(Commands.m_82129_("targets", EntityArgument.m_91470_())
                                        .executes(DailySlotCommand::activate)))

                        .then(Commands.m_82127_("sync")
                                .requires(src -> src.m_6761_(2))
                                .then(Commands.m_82129_("targets", EntityArgument.m_91470_())
                                        .executes(DailySlotCommand::sync)))

                        .then(Commands.m_82127_("reset")
                                .requires(src -> src.m_6761_(2))
                                .then(Commands.m_82129_("targets", EntityArgument.m_91470_())
                                        .executes(DailySlotCommand::reset))));
    }

    // ------------------------------------------------------------------

    private static int status(CommandSourceStack source) {
        int interval = DailySlotConfig.intervalDays();
        int perGrant = DailySlotConfig.slotsPerGrant();
        int cap = DailySlotConfig.maxSlotsPerType();
        int rules = DailySlotConfig.rules().size();

        Component capText = cap > 0
                ? Component.m_237113_(String.valueOf(cap))
                : Component.m_237110_(P + "command.unlimited");

        source.m_81352_(Component.m_237110_
                (P + "command.status.config", interval, perGrant,
                        DailySlotHandler.slotName(DailySlotConfig.defaultSlot()), capText, rules));

        ServerPlayer player = source.m_230896_();
        if (player == null) {
            return interval;
        }

        source.m_81352_(Component.m_237110_(P + (DailySlotHandler.isUnlocked(player)
                ? "command.status.active"
                : "command.status.inactive")));

        CompoundTag granted = DailySlotHandler.grantedOf(player);
        MutableComponent list = Component.m_237113_("");
        boolean first = true;
        for (String slot : DailySlotHandler.ALL_SLOTS) {
            int count = granted.m_128451_(slot);
            if (count <= 0) {
                continue;
            }
            if (!first) {
                list.m_130946_("、");
            }
            first = false;
            list.m_7220_(Component.m_237110_(P + "command.status.item",
                    DailySlotHandler.slotName(slot), count, DailySlotHandler.actualSlots(player, slot)));
        }
        Component body = first ? Component.m_237110_(P + "command.status.none") : list;
        source.m_81352_(Component.m_237110_(P + "command.status.mine", body));
        return granted.m_128451_(DailySlotHandler.UNIVERSAL_SLOT) + 1;
    }

    private static int setRule(CommandContext<CommandSourceStack> ctx, String slot) {
        CommandSourceStack source = ctx.getSource();
        int days = IntegerArgumentType.getInteger(ctx, "days");
        int amount = IntegerArgumentType.getInteger(ctx, "slots");
        String target = slot == null ? DailySlotConfig.defaultSlot() : slot;

        if (!DailySlotHandler.ALL_SLOTS.contains(target)) {
            source.m_81352_(Component.m_237110_(P + "command.invalid_slot", target));
            return 0;
        }
        if (!DailySlotConfig.applyDailyRule(days, amount, target)) {
            source.m_81352_(Component.m_237110_(P + "command.not_loaded"));
            return 0;
        }
        source.m_81352_(Component.m_237110_(P + "command.set.success",
                days, amount, DailySlotHandler.slotName(target)));
        return 1;
    }

    private static int ruleAdd(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        int day = IntegerArgumentType.getInteger(ctx, "day");
        String slot = StringArgumentType.getString(ctx, "slot");
        int count = IntegerArgumentType.getInteger(ctx, "count");

        if (!DailySlotHandler.ALL_SLOTS.contains(slot)) {
            source.m_81352_(Component.m_237110_(P + "command.invalid_slot", slot));
            return 0;
        }
        if (!DailySlotConfig.addMilestone(day, slot, count)) {
            source.m_81352_(Component.m_237110_(P + "command.not_loaded"));
            return 0;
        }
        source.m_81352_(Component.m_237110_(P + "command.rule.added",
                day, DailySlotHandler.slotName(slot), count));
        return 1;
    }

    private static int ruleRemove(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        int day = IntegerArgumentType.getInteger(ctx, "day");
        String slot = StringArgumentType.getString(ctx, "slot");

        if (!DailySlotConfig.removeMilestone(day, slot)) {
            source.m_81352_(Component.m_237110_(P + "command.rule.missing", day,
                    DailySlotHandler.slotName(slot)));
            return 0;
        }
        source.m_81352_(Component.m_237110_(P + "command.rule.removed", day,
                DailySlotHandler.slotName(slot)));
        return 1;
    }

    private static int ruleList(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        List<DailySlotConfig.Rule> rules = DailySlotConfig.rules();
        if (rules.isEmpty()) {
            source.m_81352_(Component.m_237110_(P + "command.rule.none"));
            return 0;
        }
        for (DailySlotConfig.Rule rule : rules) {
            source.m_81352_(Component.m_237110_(P + "command.rule.line",
                    rule.day, DailySlotHandler.slotName(rule.slot), rule.count));
        }
        return rules.size();
    }

    private static int grant(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Collection<ServerPlayer> players = EntityArgument.m_91477_(ctx, "targets");
        String slot = StringArgumentType.getString(ctx, "slot");
        int count = IntegerArgumentType.getInteger(ctx, "count");

        if (!DailySlotHandler.ALL_SLOTS.contains(slot)) {
            source.m_81352_(Component.m_237110_(P + "command.invalid_slot", slot));
            return 0;
        }
        for (ServerPlayer player : players) {
            int total = DailySlotHandler.grantedOf(player, slot) + count;
            DailySlotHandler.setGranted(player, slot, total);
            source.m_81352_(Component.m_237110_(P + "command.grant.success",
                    player.m_7755_(), DailySlotHandler.slotName(slot), count, total));
        }
        return players.size();
    }

    private static int convert(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Collection<ServerPlayer> players = EntityArgument.m_91477_(ctx, "targets");
        String from = StringArgumentType.getString(ctx, "from");
        int count = IntegerArgumentType.getInteger(ctx, "count");

        if (DailySlotHandler.UNIVERSAL_SLOT.equals(from) || !DailySlotHandler.NATIVE_SLOTS.contains(from)) {
            source.m_81352_(Component.m_237110_(P + "command.invalid_slot", from));
            return 0;
        }
        int total = 0;
        for (ServerPlayer player : players) {
            int moved = DailySlotHandler.convertToUniversal(player, from, count);
            if (moved <= 0) {
                source.m_81352_(Component.m_237110_(P + "command.convert.failed",
                        player.m_7755_(), DailySlotHandler.slotName(from)));
                continue;
            }
            total += moved;
            source.m_81352_(Component.m_237110_(P + "command.convert.success",
                    player.m_7755_(), moved, DailySlotHandler.slotName(from),
                    DailySlotHandler.grantedOf(player, DailySlotHandler.UNIVERSAL_SLOT)));
        }
        return total;
    }

    private static int activate(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Collection<ServerPlayer> players = EntityArgument.m_91477_(ctx, "targets");
        for (ServerPlayer player : players) {
            boolean activated = DailySlotHandler.activate(player);
            source.m_81352_(Component.m_237110_(
                    activated ? P + "command.activate.success" : P + "command.activate.already",
                    player.m_7755_()));
        }
        return players.size();
    }

    private static int sync(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Collection<ServerPlayer> players = EntityArgument.m_91477_(ctx, "targets");
        for (ServerPlayer player : players) {
            CompoundTag granted = DailySlotHandler.grantedOf(player);
            boolean ok = DailySlotHandler.syncAll(player, granted);
            source.m_81352_(Component.m_237110_(
                    ok ? P + "command.sync.success" : P + "command.sync.failed", player.m_7755_()));
        }
        return players.size();
    }

    private static int reset(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSourceStack source = ctx.getSource();
        Collection<ServerPlayer> players = EntityArgument.m_91477_(ctx, "targets");
        for (ServerPlayer player : players) {
            DailySlotHandler.resetAll(player);
            source.m_81352_(Component.m_237110_(P + "command.reset.success", player.m_7755_()));
        }
        return players.size();
    }
}
