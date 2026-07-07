package top.fur.furrybohe.event;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.item.strings.ItemStringCoil;

@Mod.EventBusSubscriber(modid = ModInfo.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemStringRepairHandler {

    @SubscribeEvent
    public static void onAnvilRepair(AnvilRepairEvent event) {
        ItemStack output = event.getOutput();
        if (!(output.getItem() instanceof ItemStringCoil)) {
            return;
        }
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        int repairAmount = 10;
        int currentDamage = output.getDamageValue();
        int newDamage = Math.max(0, currentDamage - repairAmount);
        output.setDamageValue(newDamage);
    }
}