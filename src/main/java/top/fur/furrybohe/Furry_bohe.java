package top.fur.furrybohe;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.register.*;

@Mod(ModInfo.MODID)
public class Furry_bohe {
    public Furry_bohe(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        RegisterBlockEntitys.BLOCK_ENTITIES.register(modEventBus);
        RegisterBlocks.BLOCKS.register(modEventBus);
        RegisterCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        RegisterItems.ITEMS.register(modEventBus);
        RegisterEffects.EFFECTS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event){

    }
    @Mod.EventBusSubscriber(modid = ModInfo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }
    }
}
