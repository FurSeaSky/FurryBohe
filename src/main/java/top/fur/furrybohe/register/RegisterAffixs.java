package top.fur.furrybohe.register;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.base.BaseAffix;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.data.affixs.FailCreateAffix;

import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * 词条注册表 - 管理所有词条实例
 */
public class RegisterAffixs {
    private static final Map<ResourceLocation, BaseAffix> AFFIXES = new HashMap<>();
    public static final Map<String,BaseAffix> AFFIXES_CLASS = new HashMap<>();
    static {
        FailCreateAffix failCreateAffix = new FailCreateAffix();
        AFFIXES_CLASS.put(new ResourceLocation(ModInfo.MODID,failCreateAffix.getId()).toString(),failCreateAffix);
    }

    // ========== 注册所有词条 ==========
    public static void registerAll() {
        for (Map.Entry<String, BaseAffix> entry : AFFIXES_CLASS.entrySet()) {
            String id = entry.getKey();
            BaseAffix affix = entry.getValue();

            System.out.println("[INFO] FURRYBOHE: Register Affix: " + id);

            AFFIXES.put(new ResourceLocation(id), affix);
            affix.registerEventHandlers(MinecraftForge.EVENT_BUS);
        }
    }
    public static BaseAffix get(ResourceLocation id) {
        return AFFIXES_CLASS.get(id.toString());
    }

    public static BaseAffix get(String id) {
        return AFFIXES_CLASS.containsKey(new ResourceLocation(ModInfo.MODID,id).toString()) ? AFFIXES.get(new ResourceLocation(ModInfo.MODID,id)) : null;
    }

    public static Map<ResourceLocation, BaseAffix> getAll() {
        return AFFIXES;
    }

}