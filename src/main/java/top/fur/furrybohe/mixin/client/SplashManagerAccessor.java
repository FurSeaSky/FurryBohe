package top.fur.furrybohe.mixin.client;

import net.minecraft.client.resources.SplashManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SplashManager.class)
public interface SplashManagerAccessor {
    @Accessor("splashes")
    List<String> getSplashes();
}
