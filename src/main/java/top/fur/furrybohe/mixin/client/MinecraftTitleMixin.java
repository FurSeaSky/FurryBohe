package top.fur.furrybohe.mixin.client;

import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.resources.SplashManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(SplashManager.class)
public class MinecraftTitleMixin {
    @Inject(method = "apply", at = @At("RETURN"))
    private void onApply(List<String> data, ResourceManager manager, ProfilerFiller profiler, CallbackInfo ci) {
        List<String> splashes = ((SplashManagerAccessor) this).getSplashes();
        splashes.clear();
        splashes.add("FurryBohe!!!");
        splashes.add("兽薄荷NB");
        splashes.add("mov rax,rbx\nmov [0],rax->#PF");
        splashes.add("三角洲私募");
        splashes.add("CQ CQ CQ DE BH6REB K.");
    }
}
