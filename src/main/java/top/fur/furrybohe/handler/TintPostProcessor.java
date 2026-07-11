package top.fur.furrybohe.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;
import top.fur.furrybohe.config.repo_configs.ModInfo;

public class TintPostProcessor extends PostProcessor {

    public static final TintPostProcessor INSTANCE = new TintPostProcessor();

    @Override
    public ResourceLocation getPostChainLocation() {
        return new ResourceLocation(ModInfo.MODID, "estrus");
    }

    @Override
    public void beforeProcess(PoseStack poseStack) {
        // 着色器处理前调用
    }

    @Override
    public void afterProcess() {
        // 确保重置渲染目标
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }
}