package xyz.wagyourtail.voxelshare.server;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import xyz.wagyourtail.voxelshare.Endpoint;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketPositionS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketPositionsS2C;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DedicatedClientEndpoint extends Endpoint<MinecraftServer> {
    public final UUID player;
    public int x, z;
    public long waypointSendTime = 0;
    public long regionSendTime = 0;
    private int chunkid = 0;

    public DedicatedClientEndpoint(UUID player) {
        this.player = player;
    }

    public void setPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public void doWaypoints(MinecraftServer mc) {
        //TODO
    }

    @Override
    public void doRegions(MinecraftServer mc) {
        //TODO
    }

    @Override
    public void doPositions(MinecraftServer mc) {
        List<PacketPositionS2C> positions = new LinkedList<>();
        String world = mc.getPlayerManager().getPlayer(player).world.getRegistryKey().getValue().getPath();
        for (ServerPlayerEntity spe : mc.getPlayerManager().getPlayerList()) {
            UUID pid;
            if ((pid = spe.getUuid()).equals(player)) continue;
            if (!spe.world.getRegistryKey().getValue().getPath().equals(world)) continue;
            BlockPos pos = spe.getBlockPos();
            positions.add(new PacketPositionS2C("", "", spe.world.getRegistryKey().getValue().getPath(), pid, pos.getX(), pos.getZ()));
        }
        if (positions.size() == 1) {
            sendPacket(mc, positions.get(0));
        } else if (positions.size() > 1) {
            sendPacket(mc, new PacketPositionsS2C("", "", world, positions));
        }
    }

    @Override
    public void sendPacket(MinecraftServer mc, Packet pkt) {
        ByteBuffer buff = (ByteBuffer) pkt.writePacket().rewind();
        if (buff.capacity() > 30000) {
            byte[] arr = new byte[30000];
            int len;
            int id = ++chunkid;
            int count = ((buff.capacity() + 29999) / 30000);
            int index = 0;
            for (int i = 0; i < buff.capacity(); i += Math.min(30000, buff.remaining())) {
                len = Math.min(30000, buff.capacity() - i);
                //TODO: this isn't working...
                buff.get(arr, i, len);
                PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeInt(i);
                packet.writeInt(index);
                packet.writeInt(count);
                packet.writeBytes(arr);
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(mc.getPlayerManager().getPlayer(player), VoxelShare.chunkedPacketId, packet);
            }
        } else {
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBytes(buff);
            ServerSidePacketRegistry.INSTANCE.sendToPlayer(mc.getPlayerManager().getPlayer(player), VoxelShare.packetId, packet);
        }

    }

    @Override
    public void sendPacket(PacketContext ctx, Packet pkt) {
        ByteBuffer buff = (ByteBuffer) pkt.writePacket().rewind();
        if (buff.capacity() > 30000) {
            byte[] arr = new byte[30000];
            int len;
            int id = ++chunkid;
            int count = ((buff.capacity() + 29999) / 30000);
            int index = 0;
            for (int i = 0; i < buff.capacity(); i += Math.min(30000, buff.remaining())) {
                len = Math.min(30000, buff.capacity() - i);
                buff.get(arr, i, len);
                PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeInt(i);
                packet.writeInt(index);
                packet.writeInt(count);
                packet.writeBytes(arr);
                ((ServerPlayNetworkHandler) ctx).sendPacket(new CustomPayloadS2CPacket(VoxelShare.chunkedPacketId, packet));
            }
        } else {
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBytes(buff);
            ((ServerPlayNetworkHandler) ctx).sendPacket(new CustomPayloadS2CPacket(VoxelShare.packetId, packet));
        }
    }

}
