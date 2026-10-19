package cn.blockforge.generated.curiosapicuriosa08a2;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 每日饰品栏（Curios 版）主入口。
 *
 * <p>v1.3.0 变化：
 * <ul>
 *   <li>发放目标为 Curios 原生栏位类型（curio / ring / necklace / ...），默认每天 +1 个原生 curio 栏位；</li>
 *   <li>支持 milestones 配置「第 a 天 +x 个、第 b 天 +y 个」的里程碑；</li>
 *   <li>功能默认关闭，必须使用物品「星陨」激活后才开始发放；</li>
 *   <li>本模组自建的通用栏位（daily_curio）保留，用 /dailyslot convert 手动从原生栏位转换。</li>
 * </ul>
 *
 * <p><b>注意</b>：本文件直接以 SRG 名（m_/f_ 开头）调用 Minecraft 成员，
 * 这是 1.20.1 Forge 生产环境 jar 的命名方式，编译时需要 client-srg.jar 参与 classpath。
 */
@Mod(GeneratedMod.MOD_ID)
public final class GeneratedMod {

    public static final String MOD_ID = "curios_starfall";

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    /** 星陨 / Star Fall：右键激活每日饰品栏。 */
    public static final RegistryObject<Item> STAR_FALL = ITEMS.register(
            "star_fall",
            () -> new StarFallItem(new Item.Properties().m_41497_(Rarity.EPIC)));

    public GeneratedMod() {
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON, DailySlotConfig.SPEC, "curios-star-fall-common.toml");

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        modBus.register(new ModItems());

        MinecraftForge.EVENT_BUS.register(new DailySlotHandler());
        MinecraftForge.EVENT_BUS.register(new DailySlotCommand());
    }
}
