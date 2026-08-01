package top.fur.furrybohe.event;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.fur.furrybohe.capability.FurArmorCapabilityProvider;
import top.fur.furrybohe.config.repo_configs.ModInfo;

@Mod.EventBusSubscriber(modid = ModInfo.MODID,bus =  Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEventHandler {

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();

        if (stack.getItem() instanceof ArmorItem) {
            if (!stack.getCapability(FurArmorCapabilityProvider.capability).isPresent()) {
                event.addCapability(
                        FurArmorCapabilityProvider.ID,
                        new FurArmorCapabilityProvider(stack)
                );
            }
        }
    }
}