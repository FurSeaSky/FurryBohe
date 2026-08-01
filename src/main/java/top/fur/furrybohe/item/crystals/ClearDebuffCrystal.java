package top.fur.furrybohe.item.crystals;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import top.fur.furrybohe.base.BaseCrystal;

public class ClearDebuffCrystal extends BaseCrystal {

    public ClearDebuffCrystal() {
        super(new Properties(),CrystalLevel.NONE,CrystalPart.NONE,CrystalBuff.POSITIVE,"clear_debuff_crystal", Component.translatable("text.furrybohe.clear_debuff_crystal.description.info").toString());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var playerMainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND);
        var playerOffHandItem = player.getItemInHand(InteractionHand.OFF_HAND);
        if(playerOffHandItem.isEmpty() && playerMainHandItem.getItem() instanceof BaseCrystal) {
            player.sendSystemMessage(Component.translatable("text.furrybohe.crystal.use.failed"));
            return InteractionResultHolder.fail(playerMainHandItem);
        }
        player.sendSystemMessage(Component.translatable("text.furrybohe.clear_debuff_crystal.used"));
        player.getInventory().items.set(player.getInventory().selected, ItemStack.EMPTY);
        return InteractionResultHolder.success(playerMainHandItem);
    }
}
