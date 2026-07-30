package top.fur.furrybohe.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import top.fur.furrybohe.capability.PlayerCapabilityProvider;

import java.util.function.Supplier;

public record SyncPlayerCapabilityPacket(int entityId, CompoundTag data) {
    public static void encode(SyncPlayerCapabilityPacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.entityId);
        buf.writeNbt(pkt.data);
    }

    public static SyncPlayerCapabilityPacket decode(FriendlyByteBuf buf) {
        return new SyncPlayerCapabilityPacket(buf.readInt(), buf.readNbt());
    }
    public static void handle(SyncPlayerCapabilityPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Entity entity = null;
            if (mc.level != null) {
                entity = mc.level.getEntity(pkt.entityId);
            }
            if (entity == null) return;
            if (entity instanceof Player player) {
                PlayerCapabilityProvider.get(player).deserializeNBT(pkt.data);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
