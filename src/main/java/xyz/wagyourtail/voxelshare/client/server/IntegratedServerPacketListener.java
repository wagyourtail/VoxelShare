package xyz.wagyourtail.voxelshare.client.server;

import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.minecraft.server.MinecraftServer;
import xyz.wagyourtail.voxelmapapi.RegionContainer;
import xyz.wagyourtail.voxelmapapi.accessor.IWaypoint;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.RegionHelper;
import xyz.wagyourtail.voxelshare.client.VoxelShareClient;
import xyz.wagyourtail.voxelshare.packets.c2s.*;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketHaveRegionS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketRegionS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketRequestRegionS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketRequestRegionsS2C;
import xyz.wagyourtail.voxelshare.server.DedicatedServerPacketListener;

import java.util.*;

public class IntegratedServerPacketListener extends DedicatedServerPacketListener {
    public IntegratedServerPacketListener(UUID player, MinecraftServer server) {
        super(new IntegratedClientEndpoint(player), server);
    }

    @Override
    public void onWaypoint(PacketWaypointC2S waypoint) {
        Waypoint point = VoxelShareClient.PacketToWp(waypoint);
        ((IWaypoint) point).setSync(true);
        VoxelMapApi.addWaypoint(point);
    }

    @Override
    public void onmoveWaypoint(PacketEditWaypointC2S moveWaypoint) {
        Waypoint from = VoxelShareClient.PacketToWp(moveWaypoint.from);
        Waypoint to = VoxelShareClient.PacketToWp(moveWaypoint.to);

        VoxelMapApi.removeWaypoint(from);
        ((IWaypoint) to).setSync(true);
        VoxelMapApi.addWaypoint(to);
    }

    @Override
    public void onDeleteWaypoint(PacketDeleteWaypointC2S deleteWaypoint) {
        Waypoint point = VoxelShareClient.PacketToWp(deleteWaypoint.waypoint);
        VoxelMapApi.removeWaypoint(point);
    }

    @Override
    public void onHaveRegion(PacketHaveRegionC2S haveRegion) {
        RegionContainer cont = VoxelMapApi.getOrCreateRegion(haveRegion.world, haveRegion.dimension, haveRegion.x, haveRegion.z);
        if (cont.getTime() < haveRegion.updateTime) {
            player.sendPacket(server, new PacketRequestRegionS2C(haveRegion.server, haveRegion.world, haveRegion.dimension, haveRegion.x, haveRegion.z));
        }
    }

    @Override
    public void onHaveRegions(PacketHaveRegionsC2S haveRegions) {
        List<PacketRequestRegionS2C> requests = new LinkedList<>();
        for (PacketHaveRegionC2S haveRegion : haveRegions.children) {
            RegionContainer cont = VoxelMapApi.getOrCreateRegion(haveRegion.world, haveRegion.dimension, haveRegion.x, haveRegion.z);
            if (cont.getTime() < haveRegion.updateTime) {
                requests.add(new PacketRequestRegionS2C(haveRegion.server, haveRegion.world, haveRegion.dimension, haveRegion.x, haveRegion.z));
            }
        }
        player.sendPacket(server, new PacketRequestRegionsS2C(haveRegions.server, haveRegions.world, haveRegions.dimension, requests));
    }

    @Override
    public void onRegion(PacketRegionC2S region) {
        new Thread(() -> {
            Map<Integer, String> keys = new HashMap<>();
            for (String s : region.keys.split("\r?\n")) {
                String[] parts = s.split("\\s", 2);
                keys.put(Integer.parseInt(parts[0]), parts[1]);
            }
            RegionHelper.RegionData reg = new RegionHelper.RegionData(region.data, keys, region.updateTime);
            VoxelMapApi.addNewRegionData(region.world, region.dimension, region.x, region.z, reg);
        }).start();
    }

    @Override
    public void onRequestRegion(PacketRequestRegionC2S requestRegion) {
        new Thread(() -> {
            RegionContainer cont = VoxelMapApi.getOrCreateRegion(requestRegion.world, requestRegion.dimension, requestRegion.x, requestRegion.z);
            RegionHelper.RegionData data = cont.getData();
            StringBuilder key = new StringBuilder();
            for (Map.Entry<Integer, String> item : data.key.entrySet()) {
                key.append(item.getKey()).append(" ").append(item.getValue()).append("\r\n");
            }
            PacketRegionS2C region = new PacketRegionS2C(requestRegion.server, requestRegion.world, requestRegion.dimension, data.editTime, requestRegion.x, requestRegion.z, data.data, key.toString());
            player.sendPacket(server, region);
        }).start();
    }

}
