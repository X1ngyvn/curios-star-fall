package cn.blockforge.generated.curiosapicuriosa08a2;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * 「星陨」。
 *
 * <p>右键使用后消耗 1 个，为该玩家解锁「每日饰品栏」功能；未激活前不会发放任何栏位。
 * 已经激活过的玩家再次使用不会被消耗。
 */
public class StarFallItem extends Item {

    public StarFallItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> m_7203_(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.m_21120_(hand);
        if (!level.m_5776_() && player instanceof ServerPlayer serverPlayer) {
            if (DailySlotHandler.activate(serverPlayer)) {
                stack.m_41774_(1);
                serverPlayer.m_5661_(
                        Component.m_237110_(GeneratedMod.MOD_ID + ".message.activated"), false);
            } else {
                serverPlayer.m_5661_(
                        Component.m_237110_(GeneratedMod.MOD_ID + ".message.already_activated"), false);
            }
        }
        return InteractionResultHolder.m_19096_(stack);
    }

    @Override
    public void m_7373_(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.m_237115_(GeneratedMod.MOD_ID + ".tooltip.star_fall")
                .m_130940_(ChatFormatting.GRAY));
        tooltip.add(Component.m_237115_(GeneratedMod.MOD_ID + ".tooltip.star_fall2")
                .m_130940_(ChatFormatting.DARK_GRAY));
    }
}
