package top.fur.furrybohe.creative_tabs;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import top.fur.furrybohe.register.RegisterItems;

public class FurryBoheItemsCreativeTab {

    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.furrybohe.item"))
                .icon(() -> new ItemStack(RegisterItems.ITEM_FOOD_FURRYBOHE.get()))
                .withSearchBar()
                .alignedRight()
                .displayItems((parameters, output) -> {
                    RegisterItems.furItemList.forEach(reg -> output.accept(reg.get()));
                    RegisterItems.stringCoilList.forEach(reg -> output.accept(reg.get()));
                    RegisterItems.stringItemList.forEach(reg -> output.accept(reg.get()));
                    output.accept(RegisterItems.ITEM_PIN.get());
                    output.accept(RegisterItems.ITEM_SEWING_BOX.get());
                    output.accept(RegisterItems.ITEM_FOOD_FURRYBOHE.get());
                    output.accept(RegisterItems.ITEM_BOTTLE_RESIN.get());
                })
                .build();
    }
}