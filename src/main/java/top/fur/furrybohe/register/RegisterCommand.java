package top.fur.furrybohe.register;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.fur.furrybohe.command.ModCommands;
import top.fur.furrybohe.command.tests.AlwaysTPCommand;
import top.fur.furrybohe.config.repo_configs.ModInfo;

@Mod.EventBusSubscriber(modid = ModInfo.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RegisterCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
        AlwaysTPCommand.register(event.getDispatcher());
    }
}
