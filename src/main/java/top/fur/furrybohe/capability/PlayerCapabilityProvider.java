package top.fur.furrybohe.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.network.NetworkHandler;
import top.fur.furrybohe.network.packet.SyncPlayerCapabilityPacket;

public class PlayerCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation ID = Furry_bohe.rl("player_data");
    public static final Capability<PlayerCapability> capability = CapabilityManager.get(new CapabilityToken<>(){});
    private final PlayerCapability instance;
    private final LazyOptional<PlayerCapability> lazyOptional;

    public PlayerCapabilityProvider(Player player) {
        this.instance = new PlayerCapability(player);
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

    public static PlayerCapability get(Player player) {
        return player.getCapability(capability).orElseThrow(() -> new IllegalStateException("PlayerCapability not found!"));
    }

    public static void sync(Player player) {
        NetworkHandler.CHANNEL.sendToServer(new SyncPlayerCapabilityPacket(player.getId(), get(player).serializeNBT()));
    }
}
