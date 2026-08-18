package top.fur.furrybohe.event;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import top.fur.furrybohe.capability.FurArmorCapability;
import top.fur.furrybohe.register.RegisterCapability;

public class FurArmorUpdate {
    public static void Update(TickEvent.PlayerTickEvent event){
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;

        // 遍历盔甲栏
        for (ItemStack armorStack : player.getArmorSlots()) {
            if (armorStack.isEmpty()) continue;

            // 获取 Capability
            armorStack.getCapability(RegisterCapability.FUR_ARMOR_CAPABILITY)
                    .ifPresent(cap -> {
                        // 调用 tick 更新
                        cap.tickAffixes(player);
                    });
        }

    }
}
