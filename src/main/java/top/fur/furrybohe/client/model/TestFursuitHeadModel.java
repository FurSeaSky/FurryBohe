package top.fur.furrybohe.client.model;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import top.fur.furrybohe.armor.head.TestFursuitHead;
import top.fur.furrybohe.config.repo_configs.ModInfo;

public class TestFursuitHeadModel extends GeoModel<TestFursuitHead> {
    private static final ResourceLocation MODEL =
            new ResourceLocation(ModInfo.MODID, "geo/armor/test_fursuit_geo.geo.json");

    // 默认贴图
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(ModInfo.MODID, "geo/armor/test_fursuit_geo.png");
    @Override
    public ResourceLocation getModelResource(TestFursuitHead animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(TestFursuitHead animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(TestFursuitHead animatable) {
        return null;
    }
}
