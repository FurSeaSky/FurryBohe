package top.fur.furrybohe.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.Furry_bohe;

public class FurArmorCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation ID = Furry_bohe.rl("armor_data");
    public static final Capability<FurArmorCapability> capability = CapabilityManager.get(new CapabilityToken<>(){});
    private final FurArmorCapability instance;
    private final LazyOptional<FurArmorCapability> lazyOptional;

    public FurArmorCapabilityProvider(ItemStack armorItemStack) {
        this.instance = new FurArmorCapability(armorItemStack);
        this.lazyOptional = LazyOptional.of(() -> instance);
    }

    @Override
    public @NotNull <U> LazyOptional<U> getCapability(@NotNull Capability<U> cap, @Nullable Direction side) {
        return cap == capability ? lazyOptional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        instance.deserializeNBT(nbt);
    }

    public static @Nullable FurArmorCapability get(ItemStack stack) {
        return stack.getCapability(capability).orElse(null);
    }
}
