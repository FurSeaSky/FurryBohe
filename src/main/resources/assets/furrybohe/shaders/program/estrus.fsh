#version 150

uniform sampler2D DiffuseSampler;
uniform float uAlpha;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // 从游戏画面采样
    vec4 original = texture(DiffuseSampler, texCoord);

    // 粉色 (R=1.0, G=0.41, B=0.71)
    vec3 pinkColor = vec3(1.0, 0.41, 0.71);

    // 混合：原始颜色 + 粉色叠加（而不是相乘）
    // 相乘会把暗色变得更暗，可能导致全黑
    vec3 blended = mix(original.rgb, pinkColor, uAlpha * 0.5);

    // 保证输出有颜色
    fragColor = vec4(blended, 1.0);
}