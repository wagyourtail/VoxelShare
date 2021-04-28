package xyz.wagyourtail.voxelshare.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import xyz.wagyourtail.voxelshare.VoxelShare;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VoxelShareServer implements DedicatedServerModInitializer {
    public static Map<UUID, AbstractServerPacketListener> serverPacketListners = new HashMap<>();

    @Override
    public void onInitializeServer() {
        ServerSidePacketRegistry.INSTANCE.register(VoxelShare.packetId, this::onClientPacket);
    }

    public void onClientPacket(PacketContext context, PacketByteBuf buffer) {
        serverPacketListners.computeIfAbsent(context.getPlayer().getUuid(), DedicatedServerPacketListener::new)
            .onPacket(buffer.nioBuffer());
    }
}
