package top.fur.furrybohe.armor.head;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.fur.furrybohe.client.renderer.TestFursuitHeadRenderer;

import java.util.function.Consumer;

public class TestFursuitHead extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public TestFursuitHead(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }
    public static void setSpecies(ItemStack stack, String species) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("species", species);
    }
    public static String getSpecies(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("species")) {
            return tag.getString("species");
        }
        return "wolf";
    }
    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity,
                                                          ItemStack itemStack,
                                                          EquipmentSlot equipmentSlot,
                                                          HumanoidModel<?> original) {
                // 延迟初始化你的渲染器
                if (this.renderer == null) {
                    this.renderer = new TestFursuitHeadRenderer();
                }
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }
}
