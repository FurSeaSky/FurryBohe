package top.fur.furrybohe.creative_tabs;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import top.fur.furrybohe.register.RegisterItems;

public class FurryBoheArchivesCreativeTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.furrybohe.archives"))
                .icon(() -> new ItemStack(RegisterItems.ITEM_FURRY_WORK_STATION.get()))
                .withSearchBar()
                .alignedRight()
                .displayItems((parameters, output) -> {
                    output.accept(RegisterItems.ITEM_CAT_COLLECTOR.get());
                    output.accept(RegisterItems.ITEM_FURRY_WORK_STATION.get());
                    output.accept(RegisterItems.ITEM_RESIN_COLLECTOR.get());
                    output.accept(RegisterItems.ITEM_CRYSTAL_WORK_BLOCK.get());
                })
                .build();
    }
}
