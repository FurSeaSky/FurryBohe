package top.fur.furrybohe.client.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import top.fur.furrybohe.config.repo_configs.ModInfo;

@Mod.EventBusSubscriber(modid = ModInfo.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FursuitKeyBindings {

    public static final String KEY_CATEGORY = "key.category.furrybohe";
    public static final String KEY_OPEN_WARDROBE = "key.furrybohe.open_wardrobe";

    public static final Lazy<KeyMapping> OPEN_WARDROBE = Lazy.of(() ->
            new KeyMapping(
                    KEY_OPEN_WARDROBE,
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_O,
                    KEY_CATEGORY
            )
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(OPEN_WARDROBE.get());
    }
}