#version 150

uniform sampler2D Sampler0;
uniform float time;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    // 采样字体纹理
    vec4 mask = texture(Sampler0, texCoord0);

    // 如果透明，丢弃（不绘制）
    if (mask.a < 0.05) {
        discard;
    }

    // 动态颜色（彩虹效果）
    float hue = time * 0.1 + texCoord0.x * 2.0 + texCoord0.y * 1.5;
    float r = 0.5 + 0.5 * sin(hue * 2.0 + 0.0);
    float g = 0.5 + 0.5 * sin(hue * 2.0 + 2.0);
    float b = 0.5 + 0.5 * sin(hue * 2.0 + 4.0);

    // 脉动发光
    float pulse = 0.8 + 0.2 * sin(time * 1.5 + texCoord0.x * 20.0);
    vec3 color = vec3(r, g, b) * pulse;

    // 输出
    fragColor = vec4(color, mask.a);
}