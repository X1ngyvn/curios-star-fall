package cn.blockforge.generated.curiosapicuriosa08a2;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/** 物品注册相关（创造模式物品栏归类）。挂在 mod 事件总线上。 */
public final class ModItems {

    private static final ResourceLocation INGREDIENTS_TAB = new ResourceLocation("minecraft", "ingredients");

    @SubscribeEvent
    public void onBuildCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().m_135782_().equals(INGREDIENTS_TAB)) {
            event.accept(GeneratedMod.STAR_FALL);
        }
    }
}
