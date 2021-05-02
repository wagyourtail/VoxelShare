package xyz.wagyourtail.voxelshare.server;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.events.server.PlayerJoinEvent;
import xyz.wagyourtail.voxelshare.events.server.PlayerLeaveEvent;
import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketPingS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketPlayerLeaveS2C;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class VoxelShareServer implements DedicatedServerModInitializer {
    public static final Map<UUID, AbstractServerPacketListener> serverPacketListners = new LinkedHashMap<>();

    @Override
    public void onInitializeServer() {
        registerEvents();
        ServerTickEvents.END_SERVER_TICK.register(this::onTick);
    }

    public static void logServerMessage(String message) {
        VoxelShare.LOGGER.info("[VoxelShareServer] " + message);
    }

    public void registerEvents() {
        ServerSidePacketRegistry.INSTANCE.register(VoxelShare.packetId, this::onClientPacket);
        PlayerJoinEvent.EVENT.register(this::onPlayerJoin);
        PlayerLeaveEvent.EVENT.register(this::onPlayerLeave);
    }

    protected void onPlayerJoin(PlayerEntity player, MinecraftServer mc) {
        serverPacketListners.computeIfAbsent(player.getUuid(), uuid -> new DedicatedServerPacketListener(uuid, mc)).player.sendPacket(mc, new PacketPingS2C());
    }

    protected void onPlayerLeave(PlayerEntity player, MinecraftServer mc) {
        logServerMessage("Player " + player.getUuid() + " left.");
        serverPacketListners.remove(player.getUuid());
        Packet leave = new PacketPlayerLeaveS2C(player.getUuid());
        for (AbstractServerPacketListener pkt : serverPacketListners.values()) {
            pkt.player.sendPacket(mc, leave);
        }
    }

    protected void onTick(MinecraftServer nms) {

        for (AbstractServerPacketListener player : serverPacketListners.values()) {
            player.player.tick(nms);
        }
    }

    protected void onClientPacket(PacketContext context, PacketByteBuf buffer) {
        ByteBuffer buff = buffer.nioBuffer();

        serverPacketListners.computeIfAbsent(context.getPlayer().getUuid(), uuid -> new DedicatedServerPacketListener(uuid, context.getPlayer().getServer()))
            .onPacket(PacketOpcodes.getByOpcode(buff.get()), buff);
    }
}
