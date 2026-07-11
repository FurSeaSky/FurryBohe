package top.fur.furrybohe.register;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.effect.BleedingEffect;
import top.fur.furrybohe.effect.EstrusEffect;

public class RegisterEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ModInfo.MODID);

    public static final RegistryObject<MobEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);
    public static final RegistryObject<MobEffect> ESTRUS = EFFECTS.register("estrus", EstrusEffect::new);
}
