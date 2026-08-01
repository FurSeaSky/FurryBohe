package top.fur.furrybohe.register;

import net.minecraft.resources.ResourceLocation;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.base.BaseAffix;
import top.fur.furrybohe.data.affixs.FailCreateAffix;

import java.util.HashMap;
import java.util.Map;

/**
 * 词条注册表 - 管理所有词条实例
 */
public class RegisterAffixs {
    private static final Map<ResourceLocation, BaseAffix> AFFIXES = new HashMap<>();

    // ========== 注册所有词条 ==========
    public static void registerAll() {
        register(new FailCreateAffix());
    }

    private static void register(BaseAffix affix) {
        AFFIXES.put(Furry_bohe.rl(affix.getId()), affix);
    }

    public static BaseAffix get(ResourceLocation id) {
        return AFFIXES.get(id);
    }

    public static BaseAffix get(String id) {
        return get(Furry_bohe.rl(id));
    }

    public static Map<ResourceLocation, BaseAffix> getAll() {
        return AFFIXES;
    }

}