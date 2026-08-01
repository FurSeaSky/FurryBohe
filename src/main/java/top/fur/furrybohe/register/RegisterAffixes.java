package top.fur.furrybohe.register;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.affixes.BaseAffix;

import java.util.HashMap;

public class RegisterAffixes {
    public static HashMap<ResourceLocation, BaseAffix> AFFIXES = new HashMap<>();

    // 优秀制作
    public static BaseAffix MasterWork = registerAffix(new BaseAffix(Furry_bohe.rl("master_work"), 3));

    public static BaseAffix registerAffix(@NotNull BaseAffix affix) {
        ResourceLocation id = affix.getId();
        AFFIXES.put(id, affix);
        return affix;
    }

    public static void register() {
        // 空函数 保证能在确定时间加载这个class
        // 目前用处不大 或许之后的注册可能会带有副作用时必须用这种方法来在确定时间加载
    }
}
