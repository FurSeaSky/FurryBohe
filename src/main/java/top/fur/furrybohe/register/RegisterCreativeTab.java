package top.fur.furrybohe.register;


import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.creative_tabs.FurryBoheItemsCreativeTab;

public class RegisterCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModInfo.MODID);

    public static final RegistryObject<CreativeModeTab> ITEMS_TAB = CREATIVE_MODE_TABS.register("items_tab", FurryBoheItemsCreativeTab::create);
}
