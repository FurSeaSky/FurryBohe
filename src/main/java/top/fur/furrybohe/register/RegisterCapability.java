package top.fur.furrybohe.register;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.fur.furrybohe.capability.FurArmorCapabilityProvider;
import top.fur.furrybohe.capability.PlayerCapability;
import top.fur.furrybohe.capability.PlayerCapabilityProvider;

public class RegisterCapability {
    public static boolean shouldMountCapability(Item item) {
        return item instanceof ArmorItem;
    }

    @SubscribeEvent
    public void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof Player) {
            event.addCapability(PlayerCapabilityProvider.ID, new PlayerCapabilityProvider((Player) entity));
        }
    }

    @SubscribeEvent
    public void attachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        Item item = stack.getItem();
        if (shouldMountCapability(item)) {
            event.addCapability(FurArmorCapabilityProvider.ID, new FurArmorCapabilityProvider(stack));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerCapability oldData = PlayerCapabilityProvider.get(event.getOriginal());
        PlayerCapability newData = PlayerCapabilityProvider.get(event.getEntity());
        newData.deserializeNBT(oldData.serializeNBT());
    }
}
