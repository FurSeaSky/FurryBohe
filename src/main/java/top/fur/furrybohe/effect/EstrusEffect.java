package top.fur.furrybohe.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import top.fur.furrybohe.handler.ShaderHandler;

public class EstrusEffect extends MobEffect {

    public EstrusEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFF69B4);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide()) {
            if (!entity.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                int level = amplifier + 1;
                entity.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SPEED,
                        40,
                        level,
                        false,
                        true
                ));
            }
        }

        if (entity instanceof Player player && entity.level().isClientSide()) {
            ShaderHandler.setActive(true);
        }
    }

}