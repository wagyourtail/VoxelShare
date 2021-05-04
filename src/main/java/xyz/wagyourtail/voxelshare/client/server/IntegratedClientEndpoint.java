package xyz.wagyourtail.voxelshare.client.server;

import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import xyz.wagyourtail.voxelmapapi.accessor.IWaypoint;
import xyz.wagyourtail.voxelmapapi.RegionContainer;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.client.VoxelShareClient;
import xyz.wagyourtail.voxelshare.packets.c2s.*;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketHaveRegionS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketHaveRegionsS2C;
import xyz.wagyourtail.voxelshare.server.DedicatedClientEndpoint;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class IntegratedClientEndpoint extends DedicatedClientEndpoint {
    public IntegratedClientEndpoint(UUID player) {
        super(player);
    }

    @Override
    public void doRegions(MinecraftServer mc) {
        assert MinecraftClient.getInstance().player != null;
        if (MinecraftClient.getInstance().player.getUuid().equals(player)) return;

        String server = VoxelMapApi.getCurrentServer();
        for (Map.Entry<String, Map<String, Map<String, RegionContainer>>> reg : VoxelMapApi.getRegions().entrySet()) {
            String world = reg.getKey();
            for (Map.Entry<String, Map<String, RegionContainer>> reg2 : reg.getValue().entrySet()) {
                List<PacketHaveRegionS2C> regions = new LinkedList<>();
                for (RegionContainer region : reg2.getValue().values()) {
                    if (regionSendTime < region.getTime()) {
                        regions.add(new PacketHaveRegionS2C(server, world, reg2.getKey(), region.getTime(), region.x, region.z));
                    }
                }
                sendPacket(mc, new PacketHaveRegionsS2C(server, world, reg2.getKey(), regions));
            }
        }
        regionSendTime = System.currentTimeMillis();
    }

    @Override
    public void doWaypoints(MinecraftServer mc) {
        assert MinecraftClient.getInstance().player != null;
        if (MinecraftClient.getInstance().player.getUuid().equals(player)) return;

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

}
