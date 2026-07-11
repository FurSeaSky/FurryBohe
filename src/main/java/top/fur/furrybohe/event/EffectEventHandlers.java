package top.fur.furrybohe.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.handler.ShaderHandler;
import top.fur.furrybohe.register.RegisterEffects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ModInfo.MODID)
public class EffectEventHandlers {

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect() == RegisterEffects.ESTRUS.get()) {
            LivingEntity entity = event.getEntity();
            if (entity instanceof Player player && entity.level().isClientSide()) {
                ShaderHandler.setActive(false);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (event.getEffectInstance() != null &&
                event.getEffectInstance().getEffect() == RegisterEffects.ESTRUS.get()) {
            LivingEntity entity = event.getEntity();
            if (entity instanceof Player player && entity.level().isClientSide()) {
                ShaderHandler.setActive(false);
            }
        }
    }
}