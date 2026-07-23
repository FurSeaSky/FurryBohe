package top.fur.furrybohe.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.event.TickEventFunctions.CatCollectorTickEvent;

@Mod.EventBusSubscriber(modid = ModInfo.MODID)
public class PlayerTickEventListen {
    private void ClientHandler(TickEvent.PlayerTickEvent event){

    }
    private void ServerHandler(TickEvent.PlayerTickEvent event){
        CatCollectorTickEvent.onPlayerTick( event);
    }
    @SubscribeEvent
    public void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
        Level level = event.player.level();
        if (level.isClientSide()) {
            ClientHandler(event);
        }else{
            ServerHandler(event);
        }
    }
}
