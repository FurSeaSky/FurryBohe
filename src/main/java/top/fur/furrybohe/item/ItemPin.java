package top.fur.furrybohe.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.fur.furrybohe.register.RegisterEffects;

import java.util.Random;

public class ItemPin extends Item {
    private static final Random RANDOM = new Random();
    private static final double TRIGGER_CHANCE = 0.2;
    private static final int COOLDOWN_TICKS = 20;  // 1秒冷却

    public ItemPin(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        // 检查冷却
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.pass(itemInHand);
        }

        if (itemInHand.getItem() instanceof ItemPin) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);

            boolean isBleeding = RANDOM.nextDouble() < TRIGGER_CHANCE;

            if (isBleeding) {
                if (!player.hasEffect(RegisterEffects.BLEEDING.get())) {
                    MobEffectInstance effectInstance = new MobEffectInstance(
                            RegisterEffects.BLEEDING.get(),
                            1200,  // 60秒
                            0      // I级
                    );
                    player.addEffect(effectInstance);
                    player.sendSystemMessage(Component.translatable("text.furrybohe.hurtByPin"));
                    return InteractionResultHolder.consume(itemInHand);
                }
            } else {
                player.sendSystemMessage(Component.translatable("text.furrybohe.pinNoEffect"));
                return InteractionResultHolder.pass(itemInHand);
            }
        }

        return InteractionResultHolder.pass(itemInHand);
    }
}