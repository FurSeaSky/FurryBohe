package top.fur.furrybohe.mixin.client;

import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BakedGlyph.class)
public interface BakedGlyphAccessor {
    @Accessor("u0")
    float getU0();

    @Accessor("v0")
    float getV0();

    @Accessor("u1")
    float getU1();

    @Accessor("v1")
    float getV1();
}