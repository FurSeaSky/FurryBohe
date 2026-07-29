package top.fur.furrybohe.datagen.advancements;

import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import top.fur.furrybohe.base.BaseAdvancementClass;
import top.fur.furrybohe.register.RegisterItems;

public class Meow extends BaseAdvancementClass {
    public Meow() {
        super("meow", new ItemStack(RegisterItems.ITEM_CAT_COLLECTOR.get()), FrameType.CHALLENGE, true, true, false, new ResourceLocation("minecraft:textures/gui/advancements/backgrounds/stone.png") );
    }

    @Override
    protected void buildCriteriaAndRewards() {
        builder.addCriterion("impossible_trigger", new ImpossibleTrigger.TriggerInstance());
    }
}