package top.fur.furrybohe.datagen.advancements;

import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import top.fur.furrybohe.base.BaseAdvancementClass;
import top.fur.furrybohe.register.RegisterItems;

public class AllStart extends BaseAdvancementClass {
    public AllStart() {
        super("all_start", new ItemStack(RegisterItems.ITEM_FOOD_FURRYBOHE.get()), FrameType.TASK, true, true, false, new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/stone.png") );
    }

    @Override
    protected void buildCriteriaAndRewards() {
        builder.addCriterion("impossible_trigger", new ImpossibleTrigger.TriggerInstance());
    }
}
