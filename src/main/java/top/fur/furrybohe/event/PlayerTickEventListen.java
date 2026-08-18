package top.fur.furrybohe.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.fur.furrybohe.capability.FurArmorCapability;
import top.fur.furrybohe.capability.FurArmorCapabilityProvider;
import top.fur.furrybohe.capability.PlayerCapabilityProvider;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.event.TickEventFunctions.CatCollectorTickEvent;

@Mod.EventBusSubscriber(modid = ModInfo.MODID)
public class PlayerTickEventListen {
    private static void ClientHandler(TickEvent.PlayerTickEvent event){

    }
    private static void ServerHandler(TickEvent.PlayerTickEvent event){
        CatCollectorTickEvent.onPlayerTick( event);
        FurArmorUpdate.Update(event);
    }
    private static void AllHandler(TickEvent.PlayerTickEvent event){

    }
    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        Level level = event.player.level();
        AllHandler(event);
        if (level.isClientSide()) {
            ClientHandler(event);
        }else{
            ServerHandler(event);
        }
    }
}
