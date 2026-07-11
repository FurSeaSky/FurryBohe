package top.fur.furrybohe.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import top.fur.furrybohe.register.RegisterEffects;


public class ItemFoodFurryBohe extends Item {
    public static final FoodProperties ESTRUS_FOOD = new FoodProperties.Builder()
            .alwaysEat()
            .fast()
            .nutrition(2)
            .saturationMod(0.5F)
            .effect(() -> new MobEffectInstance(RegisterEffects.ESTRUS.get(), 1200, 0), 1.0F)
            .build();
    public ItemFoodFurryBohe(Properties builder) {
        super(builder.food(ESTRUS_FOOD));
    }
}
