package xyz.wagyourtail.voxelshare.client.endpoints;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import xyz.wagyourtail.voxelmapapi.accessor.IWaypoint;
import xyz.wagyourtail.voxelmapapi.RegionContainer;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.client.VoxelShareClient;
import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.c2s.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class DedicatedServerEndpoint extends AbstractServerEndpoint {
    private int chunkid = 0;

    public DedicatedServerEndpoint(String sName) {
        super(sName);
    }

    @Override
    public void sendPacket(MinecraftClient mc, Packet pkt) {
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
                ClientSidePacketRegistry.INSTANCE.sendToServer(VoxelShare.chunkedPacketId, packet);
            }
        } else {
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBytes(buff);
            ClientSidePacketRegistry.INSTANCE.sendToServer(VoxelShare.packetId, packet);
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
                ((ClientPlayNetworkHandler) ctx).sendPacket(new CustomPayloadC2SPacket(VoxelShare.chunkedPacketId, packet));
            }
        } else {
            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeBytes(buff);
            ((ClientPlayNetworkHandler) ctx).sendPacket(new CustomPayloadC2SPacket(VoxelShare.packetId, packet));
        }
    }

    @Override
    public void doWaypoints(MinecraftClient mc) {
        if (waypointSendTime == 0) {
            List<PacketWaypointC2S> wp = VoxelMapApi.getWaypoints().stream().filter(e -> ((IWaypoint) e).shouldSync()).map(VoxelShareClient::WpToPacket).collect(Collectors.toList());
            sendPacket(mc, new PacketWaypointsC2S(VoxelMapApi.getCurrentServer(), wp));
        } else {
            String server = VoxelMapApi.getCurrentServer();
            List<PacketWaypointC2S> newWp = new LinkedList<>();
            List<PacketDeleteWaypointC2S> deleted = VoxelMapApi.getDeletedWaypoints().stream().map(e -> new PacketDeleteWaypointC2S(server, VoxelShareClient.WpToPacket(e))).collect(Collectors.toList());
            List<PacketEditWaypointC2S> edited = new LinkedList<>();
            VoxelMapApi.getWaypoints().stream().filter(e -> ((IWaypoint) e).getEditTime() > waypointSendTime && ((IWaypoint) e).shouldSync()).forEach(e -> {
                IWaypoint wp = (IWaypoint) e;
                if (wp.getOld() != null) {
                    edited.add(new PacketEditWaypointC2S(server, VoxelShareClient.WpToPacket(wp.getOld()), VoxelShareClient.WpToPacket(e)));
                } else {
                    newWp.add(VoxelShareClient.WpToPacket(e));
                }
            });
            sendPacket(mc, new PacketWaypointsC2S(server, newWp));
            deleted.forEach(e -> sendPacket(mc, e));
            edited.forEach(e -> sendPacket(mc, e));
        }
        waypointSendTime = System.currentTimeMillis();
    }

    @Override
    public void doRegions(MinecraftClient mc) {
        String server = VoxelMapApi.getCurrentServer();
        for (Map.Entry<String, Map<String, Map<String, RegionContainer>>> reg : VoxelMapApi.getRegions().entrySet()) {
            String world = reg.getKey();
            for (Map.Entry<String, Map<String, RegionContainer>> reg2 : reg.getValue().entrySet()) {
                List<PacketHaveRegionC2S> regions = new LinkedList<>();
                for (RegionContainer region : reg2.getValue().values()) {
                    if (regionSendTime < region.getTime()) {
                        regions.add(new PacketHaveRegionC2S(server, world, reg2.getKey(), region.getTime(), region.x, region.z));
                    }
                }
                sendPacket(mc, new PacketHaveRegionsC2S(server, world, reg2.getKey(), regions));
            }
        }
        regionSendTime = System.currentTimeMillis();
    }

    @Override
    public void doPositions(MinecraftClient mc) {
        //server will just get this from entities
    }

}
