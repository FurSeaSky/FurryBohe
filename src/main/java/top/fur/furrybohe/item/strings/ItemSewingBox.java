package top.fur.furrybohe.item.strings;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import top.fur.furrybohe.register.RegisterItems;

import java.util.ArrayList;
import java.util.List;

public class ItemSewingBox extends Item {
    private static final String TAG_COLORS = "StoredColors";
    private static final String TAG_COLOR_COUNT = "ColorCount";
    private static final int MAX_COLORS = 10;

    public ItemSewingBox(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        ItemStack boxStack;
        ItemStack otherHand;
        if (mainHand.getItem() == this && interactionHand == InteractionHand.MAIN_HAND) {
            boxStack = mainHand;
            otherHand = offHand;
        } else if (offHand.getItem() == this && interactionHand == InteractionHand.OFF_HAND) {
            boxStack = offHand;
            otherHand = mainHand;
        } else {
            return InteractionResultHolder.pass(stack);
        }
        if (otherHand.getItem() instanceof ItemStringCoil &&
                !BuiltInRegistries.ITEM.getKey(otherHand.getItem()).getPath().split("_")[0].equals("null")) {
            return handleStoreColor(boxStack, otherHand, player, (ItemStringCoil) otherHand.getItem());
        }
        if (otherHand.getItem() instanceof ItemStringCoil) {
            String color = BuiltInRegistries.ITEM.getKey(otherHand.getItem()).getPath().split("_")[0];
            if ("null".equals(color)) {
                return handleTakeOutColor(boxStack, otherHand, player);
            }
        }
        if (otherHand.isEmpty()) {
            return handleTakeOutColor(boxStack, null, player);
        }
        return InteractionResultHolder.pass(stack);
    }
    private InteractionResultHolder<ItemStack> handleTakeOutColor(ItemStack box, ItemStack emptyCoil, Player player) {
        if (getColorCount(box) == 0) {
            player.displayClientMessage(
                    Component.translatable("message.furrybohe.sewing_box.empty"),
                    true
            );
            return InteractionResultHolder.fail(box);
        }
        String color = removeLastColor(box);
        if (color == null) {
            return InteractionResultHolder.fail(box);
        }
        ItemStack newCoil = createCoilByColor(color);
        if (newCoil.isEmpty()) {
            return InteractionResultHolder.fail(box);
        }
        if (emptyCoil != null && !emptyCoil.isEmpty()) {
            emptyCoil.shrink(1);
        }
        if (!player.getInventory().add(newCoil)) {
            player.drop(newCoil, false);
        }
        player.displayClientMessage(
                Component.translatable("message.furrybohe.sewing_box.taken", color, getColorCount(box), MAX_COLORS),
                true
        );
        return InteractionResultHolder.success(box);
    }
    private InteractionResultHolder<ItemStack> handleStoreColor(ItemStack box, ItemStack coil, Player player, ItemStringCoil coilItem) {
        if (getColorCount(box) >= MAX_COLORS) {
            player.displayClientMessage(
                    Component.translatable("message.furrybohe.sewing_box.full", MAX_COLORS),
                    true
            );
            return InteractionResultHolder.fail(box);
        }
        String path = BuiltInRegistries.ITEM.getKey(coil.getItem()).getPath();
        String color = path.replace("_string_coil", "");
        if ("null".equals(color)) {
            player.displayClientMessage(
                    Component.translatable("message.furrybohe.sewing_box.invalid_coil"),
                    true
            );
            return InteractionResultHolder.fail(box);
        }
        if (hasColor(box, color)) {
            player.displayClientMessage(
                    Component.translatable("message.furrybohe.sewing_box.duplicate", color),
                    true
            );
            return InteractionResultHolder.fail(box);
        }
        addColor(box, color);
        int newDamage = coil.getDamageValue() + 1;
        if (newDamage >= coil.getMaxDamage()) {
            RegistryObject<Item> nullCoil = RegisterItems.STRING_COIL_MAP.get("null");
            ItemStack emptyCoil = nullCoil != null
                    ? new ItemStack(nullCoil.get(), 1)
                    : ItemStack.EMPTY;
            if (!emptyCoil.isEmpty()) {
                player.getInventory().setItem(player.getInventory().selected, emptyCoil);
            }
        } else {
            coil.setDamageValue(newDamage);
        }

        player.displayClientMessage(
                Component.translatable("message.furrybohe.sewing_box.stored", color, getColorCount(box), MAX_COLORS),
                true
        );
        return InteractionResultHolder.success(box);
    }
    private InteractionResultHolder<ItemStack> handleTakeOutColor(ItemStack box, Player player) {
        if (getColorCount(box) == 0) {
            player.displayClientMessage(
                    Component.translatable("message.furrybohe.sewing_box.empty"),
                    true
            );
            return InteractionResultHolder.fail(box);
        }
        String color = removeLastColor(box);
        if (color == null) {
            return InteractionResultHolder.fail(box);
        }
        ItemStack newCoil = createCoilByColor(color);
        if (newCoil.isEmpty()) {
            return InteractionResultHolder.fail(box);
        }
        if (!player.getInventory().add(newCoil)) {
            player.setItemSlot(EquipmentSlot.MAINHAND, newCoil);
        }
        player.displayClientMessage(
                Component.translatable("message.furrybohe.sewing_box.taken", color, getColorCount(box), MAX_COLORS),
                true
        );
        return InteractionResultHolder.success(box);
    }

    private List<String> getColorList(ItemStack stack) {
        List<String> colors = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag == null) return colors;

        ListTag listTag = tag.getList(TAG_COLORS, 8); // 8 = StringTag
        for (int i = 0; i < listTag.size(); i++) {
            colors.add(listTag.getString(i));
        }
        return colors;
    }
    private int getColorCount(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return tag.getInt(TAG_COLOR_COUNT);
    }
    private boolean hasColor(ItemStack stack, String color) {
        return getColorList(stack).contains(color);
    }
    private void addColor(ItemStack stack, String color) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag listTag = tag.getList(TAG_COLORS, 8);
        listTag.add(StringTag.valueOf(color));
        tag.put(TAG_COLORS, listTag);
        tag.putInt(TAG_COLOR_COUNT, listTag.size());
    }
    private String removeLastColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;
        ListTag listTag = tag.getList(TAG_COLORS, 8);
        if (listTag.isEmpty()) return null;
        String lastColor = listTag.getString(listTag.size() - 1);
        listTag.remove(listTag.size() - 1);
        tag.put(TAG_COLORS, listTag);
        tag.putInt(TAG_COLOR_COUNT, listTag.size());

        return lastColor;
    }
    private ItemStack createCoilByColor(String color) {
        RegistryObject<Item> coil = RegisterItems.STRING_COIL_MAP.get(color);
        return coil != null ? new ItemStack(coil.get(), 1) : ItemStack.EMPTY;
    }
    @Override
    public Component getName(ItemStack pStack) {
        int count = getColorCount(pStack);
        if (count > 0) {
            return Component.translatable("item.furrybohe.sewing_box.with_colors", count, MAX_COLORS);
        }
        return super.getName(pStack);
    }
    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        List<String> colors = getColorList(pStack);
        if (!colors.isEmpty()) {
            pTooltip.add(Component.translatable("item.furrybohe.sewing_box.tooltip", String.join(", ", colors)));
        }
    }
}