package top.fur.furrybohe.network;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import top.fur.furrybohe.Furry_bohe;
import top.fur.furrybohe.network.packet.SyncPlayerCapabilityPacket;

public class NetworkHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            Furry_bohe.rl("mod_channel"),
            () -> "0",
            "0"::equals,
            "0"::equals
    );

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SyncPlayerCapabilityPacket.class,
                SyncPlayerCapabilityPacket::encode,
                SyncPlayerCapabilityPacket::decode,
                SyncPlayerCapabilityPacket::handle);
    }
}
