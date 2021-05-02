package xyz.wagyourtail.voxelshare.client.endpoints;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import xyz.wagyourtail.voxelmapapi.IWaypoint;
import xyz.wagyourtail.voxelmapapi.Region;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.client.VoxelShareClient;
import xyz.wagyourtail.voxelshare.packets.Packet;
import xyz.wagyourtail.voxelshare.packets.c2s.*;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

public class DedicatedServerEndpoint extends AbstractServerEndpoint {


    public DedicatedServerEndpoint(String sName) {
        super(sName);
    }

    @Override
    public void sendPacket(MinecraftClient mc, Packet pkt) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeBytes((ByteBuffer) pkt.writePacket().rewind());
        ClientSidePacketRegistry.INSTANCE.sendToServer(VoxelShare.packetId, packet);
    }

    @Override
    public void sendPacket(PacketContext ctx, Packet pkt) {
        PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
        packet.writeBytes((ByteBuffer) pkt.writePacket().rewind());
        ((ClientPlayNetworkHandler)ctx).sendPacket(new CustomPayloadC2SPacket(VoxelShare.packetId, packet));
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

    //TODO: test
    @Override
    public void doRegions(MinecraftClient mc) {
        String server = VoxelMapApi.getCurrentServer();
        for (Map.Entry<String, Map<String, Set<Region>>> reg : VoxelMapApi.getRegions().entrySet()) {
            String world = reg.getKey();
            for (Map.Entry<String, Set<Region>> reg2 : reg.getValue().entrySet()) {
                List<PacketHaveRegionC2S> regions = new LinkedList<>();
                for (Region region : reg2.getValue()) {
                    regions.add(new PacketHaveRegionC2S(server, world, reg2.getKey(), region.getTime(), region.x, region.z));
                }
                sendPacket(mc, new PacketHaveRegionsC2S(server, world, reg.getKey(), regions));
            }
        }
        regionSendTime = System.currentTimeMillis();
    }

    @Override
    public void doPositions(MinecraftClient mc) {
        //server will just get this from entities
    }

}
