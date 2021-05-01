package xyz.wagyourtail.voxelshare.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import xyz.wagyourtail.voxelshare.Endpoint;
import xyz.wagyourtail.voxelshare.Region;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketPositionS2C;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DedicatedClientEndpoint extends Endpoint<MinecraftServer> {
    public final UUID player;
    public int x, z;
    public List<Region> clientRegions = null;
    public long waypointSendTime = 0;
    public long regionSendTime = 0;

    public DedicatedClientEndpoint(UUID player) {
        this.player = player;
    }

    public void setPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public void doWaypoints(MinecraftServer mc) {

    }

    @Override
    public void doRegions(MinecraftServer mc) {

    }

    @Override
    public void doPositions(MinecraftServer mc) {
        List<PacketPositionS2C> positions = new LinkedList<>();
        for (ServerPlayerEntity spe : mc.getPlayerManager().getPlayerList()) {
            UUID pid;
            if ((pid = spe.getUuid()).equals(player)) continue;
            BlockPos pos = spe.getBlockPos();
            positions.add(new PacketPositionS2C("", "", pid, pos.getX(), pos.getZ()));
        }
        if (positions.size() == 1) {
            sendPacket(mc, positions.get(0));
        }
    }

    @Override
    public void sendPacket(MinecraftServer mc, Packet pkt) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeBytes((ByteBuffer) pkt.writePacket().rewind());
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(mc.getPlayerManager().getPlayer(player), VoxelShare.packetId, packet);
    }

}
