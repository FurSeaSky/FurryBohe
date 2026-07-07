package top.fur.furrybohe.item.strings;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Objects;

import static top.fur.furrybohe.item.strings.ItemStrings.STRINGS_COLORS;

public class ItemStringCoil extends Item {
    public static final String[] STRING_COILS_COLORS = {
            "null_string_coil",           // 空的
            "white_string_coil",          // 白色
            "orange_string_coil",         // 橙色
            "magenta_string_coil",        // 品红色
            "light_blue_string_coil",     // 淡蓝色
            "yellow_string_coil",         // 黄色
            "lime_string_coil",           // 黄绿色
            "pink_string_coil",           // 粉红色
            "gray_string_coil",           // 灰色
            "light_gray_string_coil",     // 淡灰色
            "cyan_string_coil",           // 青色
            "purple_string_coil",         // 紫色
            "blue_string_coil",           // 蓝色
            "brown_string_coil",          // 棕色
            "green_string_coil",          // 绿色
            "red_string_coil",            // 红色
            "black_string_coil"           // 黑色
    };

    public ItemStringCoil(Properties properties) {
        super(properties.durability(100));
    }
    @Override
    public boolean isValidRepairItem(ItemStack pStack, ItemStack pRepairCandidate){
        String source = BuiltInRegistries.ITEM.getKey(pStack.getItem()).getPath();
        String target = BuiltInRegistries.ITEM.getKey(pRepairCandidate.getItem()).getPath();
        return Arrays.asList(STRING_COILS_COLORS).contains(source) && Arrays.asList(STRINGS_COLORS).contains(target) && Objects.equals(source.split("_")[0], target.split("_")[0]);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        return super.use(pLevel, pPlayer, pHand);
    }
}
