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
        HEAD_BONES.add("DownBody");
        HEAD_BONES.add("Body");
        HEAD_BONES.add("Arm");
    }
    public TestFursuitHeadRenderer() {
        super(new TestFursuitHeadModel());
    }
    private void hiddenChildBone(CoreGeoBone bone) {
        if (HEAD_BONES.contains(bone.getName())) {
            //System.out.println("setHidden: " + bone.getName());
            bone.setHidden(true);
            hideSubtree(bone);
            return;
        } else {
            //System.out.println("setShow: " + bone.getName());
        }
        for (CoreGeoBone childBone : bone.getChildBones()) {
            hiddenChildBone(childBone);
        }
    }
    private void hideSubtree(CoreGeoBone bone) {
        for (CoreGeoBone child : bone.getChildBones()) {
            //System.out.println("  setHidden(subtree): " + child.getName());
            child.setHidden(true);
            hideSubtree(child);
        }
    }
    @Override
    public void preRender(PoseStack poseStack, TestFursuitHead animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        for (CoreGeoBone bone : model.getBones()) {
            hiddenChildBone(bone);
        }
        super.preRender(poseStack, animatable, model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay,
                red, green, blue, alpha);
    }
}
