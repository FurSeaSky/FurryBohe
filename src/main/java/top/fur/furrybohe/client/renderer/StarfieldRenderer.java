package top.fur.furrybohe.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class StarfieldRenderer {
    private static final int STAR_COUNT = 300;
    private static final List<Star> stars = new ArrayList<>();
    private static final RandomSource random = RandomSource.create();
    private static boolean initialized = false;

    private static class Star {
        float x, y;        // 位置 (0-1 归一化)
        float size;        // 大小 (像素)
        float speed;       // 移动速度
        float brightness;  // 亮度 (0-1)
        float phase;       // 闪烁相位

        Star(float x, float y, float size, float speed, float brightness, float phase) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.brightness = brightness;
            this.phase = phase;
        }
    }

    public static void init() {
        if (!initialized) {
            for (int i = 0; i < STAR_COUNT; i++) {
                stars.add(new Star(
                        random.nextFloat(),           // x
                        random.nextFloat(),           // y
                        0.5f + random.nextFloat() * 2.0f, // size
                        0.2f + random.nextFloat() * 0.8f, // speed
                        0.3f + random.nextFloat() * 0.7f, // brightness
                        random.nextFloat() * 6.2832f  // phase
                ));
            }
            initialized = true;
        }
    }

    public static void render(GuiGraphics guiGraphics, int screenWidth, int screenHeight, long timeMillis) {
        // 1. 先绘制纯黑背景
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        guiGraphics.fill(0, 0, screenWidth, screenHeight, 0xFF000000);

        // 2. 绘制星空
        float delta = (timeMillis % 60000) / 60000.0f;
        float timeSec = timeMillis / 1000.0f;

        // 获取缓冲构建器
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(()->GameRenderer.getPositionColorShader());

        Matrix4f matrix = guiGraphics.pose().last().pose();

        // 开始批量绘制星星
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Star star : stars) {
            // 星星缓慢飘动（绕圈运动）
            float angle = timeSec * star.speed * 0.05f + star.phase;
            float radius = 0.02f + star.speed * 0.03f;

            float xOffset = (float) Math.sin(angle) * radius;
            float yOffset = (float) Math.cos(angle * 0.7f) * radius;

            float x = (star.x + xOffset) % 1.0f;
            float y = (star.y + yOffset) % 1.0f;

            // 转换为屏幕坐标
            float screenX = x * screenWidth;
            float screenY = y * screenHeight;

            // 闪烁效果
            float twinkle = 0.6f + 0.4f * (float) Math.sin(
                    timeSec * star.speed * 1.5f + star.phase * 20.0f
            );
            float brightness = star.brightness * twinkle;

            // 星星大小（带脉动）
            float pulse = 1.0f + 0.3f * (float) Math.sin(timeSec * star.speed + star.phase * 5);
            float size = star.size * pulse;

            // 颜色：白色，带轻微蓝色
            int r = (int) (200 + 55 * brightness);
            int g = (int) (210 + 45 * brightness);
            int b = (int) (230 + 25 * brightness);
            int a = (int) (150 + 105 * brightness);

            // 绘制星星（小方块，带发光渐变）
            float halfSize = size / 2;

            // 核心亮部
            bufferBuilder.vertex(matrix, screenX - halfSize, screenY - halfSize, 0)
                    .color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, screenX + halfSize, screenY - halfSize, 0)
                    .color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, screenX + halfSize, screenY + halfSize, 0)
                    .color(r, g, b, a).endVertex();
            bufferBuilder.vertex(matrix, screenX - halfSize, screenY + halfSize, 0)
                    .color(r, g, b, a).endVertex();

            // 外发光层（更大的半透明方块）
            float glowSize = size * 3;
            int glowAlpha = (int) (a * 0.2f);
            bufferBuilder.vertex(matrix, screenX - glowSize, screenY - glowSize, 0)
                    .color(r, g, b, glowAlpha).endVertex();
            bufferBuilder.vertex(matrix, screenX + glowSize, screenY - glowSize, 0)
                    .color(r, g, b, glowAlpha).endVertex();
            bufferBuilder.vertex(matrix, screenX + glowSize, screenY + glowSize, 0)
                    .color(r, g, b, glowAlpha).endVertex();
            bufferBuilder.vertex(matrix, screenX - glowSize, screenY + glowSize, 0)
                    .color(r, g, b, glowAlpha).endVertex();
        }

        // 提交绘制
        BufferUploader.drawWithShader(bufferBuilder.end());
        RenderSystem.disableBlend();
    }
}