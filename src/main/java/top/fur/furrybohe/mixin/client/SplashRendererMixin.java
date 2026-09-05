package top.fur.furrybohe.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.fur.furrybohe.mixin.client.FontAccessor;
import top.fur.furrybohe.mixin.client.BakedGlyphAccessor;

import java.io.IOException;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("FurryBohe");

    @Shadow
    @Final
    private String splash;

    @Unique
    private static ShaderInstance cosmicShader;

    @Unique
    private static final ResourceLocation SHADER_LOCATION =
            new ResourceLocation("furrybohe", "shaders/core/cosmic_text");

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(GuiGraphics guiGraphics, int screenWidth, Font font, int color, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        LOGGER.info("[FurryBohe] SplashRendererMixin.onRender() called!");

        // 加载着色器
        if (cosmicShader == null) {
            try {
                ResourceManager manager = mc.getResourceManager();
                cosmicShader = new ShaderInstance(
                        manager,
                        SHADER_LOCATION,
                        DefaultVertexFormat.POSITION_COLOR_TEX
                );
                LOGGER.info("[FurryBohe] Cosmic shader loaded successfully!");
            } catch (IOException e) {
                LOGGER.error("[FurryBohe] Failed to load cosmic shader", e);
                return;
            }
        } else {
            LOGGER.info("[FurryBohe] Cosmic shader already loaded (cached)");
        }

        String text = splash != null ? splash : "Minecraft";
        LOGGER.info("[FurryBohe] Rendering splash text: '{}'", text);

        // 计算位置（原版偏移）
        int x = screenWidth / 2 + 123;
        int y = 69;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(x, y, 0.0F);
        guiGraphics.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(-20.0F));

        float f = 1.8F - Mth.abs(Mth.sin((float) (Util.getMillis() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
        f = f * 100.0F / (float) (font.width(text) + 32);
        guiGraphics.pose().scale(f, f, f);

        Matrix4f matrix = guiGraphics.pose().last().pose();

        // 应用着色器
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(() -> cosmicShader);

        // 设置 time uniform
        float time = (float) (System.currentTimeMillis() / 1000.0);
        cosmicShader.safeGetUniform("time").set(time);
        LOGGER.info("[FurryBohe] time uniform set to: {}", time);

        // 手动渲染文字（使用 MultiBufferSource 自动绑定纹理）
        renderTextManual(font, text, matrix);

        // 恢复 - 使用安全的空值处理
        ShaderInstance defaultShader = GameRenderer.getPositionTexShader();
        if (defaultShader != null) {
            RenderSystem.setShader(() -> defaultShader);
        }
        RenderSystem.disableBlend();

        guiGraphics.pose().popPose();
        LOGGER.info("[FurryBohe] SplashRendererMixin.onRender() finished!");
        ci.cancel();
    }

    @Unique
    private void renderTextManual(Font font, String text, Matrix4f matrix) {
        LOGGER.info("[FurryBohe] renderTextManual() called, text: '{}'", text);

        float xPos = 0.0F;
        float yPos = -8.0F;

        // 通过 Accessor 获取字体集
        net.minecraft.client.gui.font.FontSet fontSet = ((FontAccessor) font).invokeGetFontSet(net.minecraft.network.chat.Style.DEFAULT_FONT);
        if (fontSet == null) {
            LOGGER.warn("[FurryBohe] FontSet is null, cannot render text!");
            return;
        }
        LOGGER.info("[FurryBohe] FontSet retrieved successfully");

        // 获取字体纹理并绑定（使用 MultiBufferSource 方式）
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        // 使用 font.drawInBatch 来渲染（自动绑定纹理）
        // 注意：drawInBatch 内部会重置着色器，但我们在外部已经设置了自定义着色器
        // 所以我们需要在 drawInBatch 之后重新设置着色器

        // 先保存当前着色器
        ShaderInstance currentShader = cosmicShader;

        // 使用 drawInBatch 渲染（这会自动绑定字体纹理）
        font.drawInBatch(text, xPos, yPos, 0xFFFFFFFF, false, matrix, bufferSource, Font.DisplayMode.NORMAL, 0, 15728880);
        bufferSource.endBatch();

        LOGGER.info("[FurryBohe] font.drawInBatch() completed");

        // 重新设置着色器（因为 drawInBatch 内部会重置）
        RenderSystem.setShader(() -> currentShader);

        // 重新设置 time uniform（因为着色器被重置了）
        float time = (float) (System.currentTimeMillis() / 1000.0);
        currentShader.safeGetUniform("time").set(time);

        LOGGER.info("[FurryBohe] renderTextManual() finished");
    }
}