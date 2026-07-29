package top.fur.furrybohe.event.TickEventFunctions;

import net.minecraft.advancements.Advancement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.item.ItemCatCollector;

import java.util.List;

public class CatCollectorTickEvent {
    private static final Logger LOGGER = LogManager.getLogger(CatCollectorTickEvent.class);
    private static final double SEARCH_RADIUS = 5.0;
    private static final int CHECK_INTERVAL = 20;
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient()) {
            return;
        }

        Player player = event.player;
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean hasCollector = (mainHand.getItem() instanceof ItemCatCollector) ||
                (offHand.getItem() instanceof ItemCatCollector);

        if (!hasCollector) {
            return;
        }

        if (player.tickCount % CHECK_INTERVAL != 0) {;
            return;
        }

        Level level = player.level();

        AABB searchBox = new AABB(
                player.getX() - SEARCH_RADIUS,
                player.getY() - SEARCH_RADIUS,
                player.getZ() - SEARCH_RADIUS,
                player.getX() + SEARCH_RADIUS,
                player.getY() + SEARCH_RADIUS,
                player.getZ() + SEARCH_RADIUS
        );

        List<Cat> nearbyCats = level.getEntitiesOfClass(Cat.class, searchBox);
        if (nearbyCats.isEmpty()) {
            return;
        }


        new Thread(() -> {
            String threadName = Thread.currentThread().getName();
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            CompoundTag data = player.getPersistentData();
            if (!data.contains("cat_power")) {
                data.putByte("cat_power", (byte) 0);
            }

            byte currentPower = data.getByte("cat_power");
            int addPower = nearbyCats.size();
            int newPower = currentPower + addPower;
            if (newPower >= 100) {
                data.putByte("cat_power", (byte) 0);
                Component message = Component.translatable("text.furrybohe.meow.getsuccess");
                player.sendSystemMessage(message);
                Advancement advancement = ((ServerPlayer) player).server.getAdvancements().getAdvancement(new ResourceLocation(ModInfo.MODID, "meow"));
                if (advancement != null) {
                    AdvancementEvent.AdvancementEarnEvent earnEvent =
                            new AdvancementEvent.AdvancementEarnEvent(player, advancement);
                    ((ServerPlayer)player).getAdvancements().award(advancement, "impossible_trigger");
                }
            } else {
                data.putByte("cat_power", (byte) newPower);
                Component message = Component.translatable("text.furrybohe.collect_cat_power.info", data.getByte("cat_power"));
                player.sendSystemMessage(message);
            }
        }).start();
    }
}