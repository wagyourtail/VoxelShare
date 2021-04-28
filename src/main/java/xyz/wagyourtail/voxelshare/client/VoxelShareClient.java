package xyz.wagyourtail.voxelshare.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.client.api.VoxelMapApi;
import xyz.wagyourtail.voxelshare.server.AbstractServerPacketListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class VoxelShareClient implements ClientModInitializer {
    //TODO: ad-hoc server stuff with 2 port (full duplex) TCP hole punching
    public static AbstractClientPacketListener clientPacketListener;
    public static Map<UUID, AbstractServerPacketListener> serverPacketListners = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(VoxelShare.packetId, this::onServerPacket);
        ServerSidePacketRegistry.INSTANCE.register(VoxelShare.packetId, this::onClientPacket);
    }

    public void onServerPacket(PacketContext context, PacketByteBuf buffer) {
        if (clientPacketListener == null) {
            clientPacketListener = new ClientPacketListener(new DedicatedServer(VoxelMapApi.INSTANCE.getCurrentServer()));
        } else if (!clientPacketListener.server.getServerName().equals(VoxelMapApi.INSTANCE.getCurrentServer()) && clientPacketListener.server.isDedicated()) {
            clientPacketListener = new ClientPacketListener(new DedicatedServer(VoxelMapApi.INSTANCE.getCurrentServer()));
        }
        clientPacketListener.onPacket(buffer.nioBuffer());
    }

    public void onClientPacket(PacketContext context, PacketByteBuf buffer) {
        serverPacketListners.computeIfAbsent(context.getPlayer().getUuid(), IntegratedServerPacketListener::new)
            .onPacket(buffer.nioBuffer());
    }
}
