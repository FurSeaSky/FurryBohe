package top.fur.furrybohe.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import top.fur.furrybohe.armor.head.TestFursuitHead;
import top.fur.furrybohe.client.model.TestFursuitHeadModel;

import java.util.HashSet;
import java.util.Set;

public class TestFursuitHeadRenderer extends GeoArmorRenderer<TestFursuitHead> {
    private static final Set<String> HEAD_BONES = new HashSet<>();
    static {
        //HEAD_BONES.add("Root");
        //HEAD_BONES.add("AllBody");
        //HEAD_BONES.add("UpBody");
        HEAD_BONES.add("AllHead");
        HEAD_BONES.add("Head");
        HEAD_BONES.add("Face");
        HEAD_BONES.add("RightLash");
        HEAD_BONES.add("LeftLash");
        HEAD_BONES.add("AllHead2");
        HEAD_BONES.add("Ear");
        HEAD_BONES.add("LeftEar");
        HEAD_BONES.add("RightEar");
        //HEAD_BONES.add("LeftFur");
        //HEAD_BONES.add("RightFur");
    }
    public TestFursuitHeadRenderer() {
        super(new TestFursuitHeadModel());
    }

    @Override
    public void preRender(PoseStack poseStack, TestFursuitHead animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        for (CoreGeoBone bone : model.getBones()) {
            String name = bone.getName();
            // 如果是头部骨骼则显示，否则隐藏
            bone.setHidden(!HEAD_BONES.contains(name));
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
    }
}
