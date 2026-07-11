package top.fur.furrybohe.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import top.fur.furrybohe.register.RegisterEffects;

public class BleedingEffect extends MobEffect {
    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0xCC0000);
    }
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier){
        MobEffectInstance effectInstance = entity.getEffect(RegisterEffects.BLEEDING.get());
        if(effectInstance == null) return;
        if(entity instanceof Player player && player.isCreative()) return;
        int level = effectInstance.getAmplifier() + 1;
        float damagePerTick = (1.0f*level)/1.2f/20.0f;
        entity.hurt(entity.damageSources().magic(), damagePerTick);
        if (entity.getHealth() <= 2.0f && !entity.hasEffect(MobEffects.CONFUSION)) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
        }
    }
}
