package top.fur.furrybohe.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.handler.TintPostProcessor;

@Mod.EventBusSubscriber(modid = ModInfo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PostProcessHandler.addInstance(TintPostProcessor.INSTANCE);
    }
}