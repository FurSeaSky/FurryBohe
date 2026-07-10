package top.fur.furrybohe.event;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.fur.furrybohe.config.repo_configs.ModInfo;
import top.fur.furrybohe.register.RegisterEffects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = ModInfo.MODID)
public class LivingTickEvent {
    private static void BleedingEffectFunction(LivingEntity entity) {
        MobEffectInstance effectInstance = entity.getEffect(RegisterEffects.BLEEDING.get());
        if(effectInstance == null) return;
        int level = effectInstance.getAmplifier() + 1;
        float damagePerTick = (1.0f*level)/1.2f/20.0f;
        entity.setHealth(entity.getHealth() - damagePerTick);
        if(entity.getHealth()<=2.0f){
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION,100,2));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,100,2));
        }
    }
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity.hasEffect(RegisterEffects.BLEEDING.get())) BleedingEffectFunction(entity);
    }
}
