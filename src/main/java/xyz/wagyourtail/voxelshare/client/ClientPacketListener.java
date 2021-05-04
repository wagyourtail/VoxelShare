package xyz.wagyourtail.voxelshare.client;

import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.voxelmapapi.RegionContainer;
import xyz.wagyourtail.voxelmapapi.accessor.IWaypoint;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.RegionHelper;
import xyz.wagyourtail.voxelshare.client.endpoints.AbstractServerEndpoint;
import xyz.wagyourtail.voxelshare.packets.c2s.*;
import xyz.wagyourtail.voxelshare.packets.s2c.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClientPacketListener extends AbstractClientPacketListener {
    public ClientPacketListener(AbstractServerEndpoint server, MinecraftClient mc) {
        super(server, mc);
    }

    @Override
    public void onPing() {
        server.sendPacket(mc, new PacketPingC2S());
    }

    @Override
    public void onPositionPacket(PacketPositionS2C position) {
        checkWorld(position.world);
        checkDimension(position.dimension);
        synchronized (positions) {
            positions.put(position.player, position);
        }
    }

    @Override
    public void onRegionData(PacketRegionS2C region) {
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
    public void onRequestRegion(PacketRequestRegionS2C requestRegion) {
        new Thread(() -> {
            RegionContainer cont = VoxelMapApi.getOrCreateRegion(requestRegion.world, requestRegion.dimension, requestRegion.x, requestRegion.z);
            RegionHelper.RegionData data = cont.getData();
            StringBuilder key = new StringBuilder();
            for (Map.Entry<Integer, String> item : data.key.entrySet()) {
                key.append(item.getKey()).append(" ").append(item.getValue()).append("\r\n");
            }
            PacketRegionC2S region = new PacketRegionC2S(requestRegion.server, requestRegion.world, requestRegion.dimension, data.editTime, requestRegion.x, requestRegion.z, data.data, key.toString());
            server.sendPacket(mc, region);
        }).start();
    }

    @Override
    public void onHaveRegion(PacketHaveRegionS2C haveRegion) {
        RegionContainer cont = VoxelMapApi.getOrCreateRegion(haveRegion.world, haveRegion.dimension, haveRegion.x, haveRegion.z);
        if (cont.getTime() < haveRegion.updateTime) {
            server.sendPacket(mc, new PacketRequestRegionC2S(haveRegion.server, haveRegion.world, haveRegion.dimension, haveRegion.x, haveRegion.z));
        }
    }

    @Override
    public void onHaveRegions(PacketHaveRegionsS2C haveRegions) {
        List<PacketRequestRegionC2S> requests = new LinkedList<>();
        for (PacketHaveRegionC2S haveRegion : haveRegions.children) {
            RegionContainer cont = VoxelMapApi.getOrCreateRegion(haveRegion.world, haveRegion.dimension, haveRegion.x, haveRegion.z);
            if (cont.getTime() < haveRegion.updateTime) {
                requests.add(new PacketRequestRegionC2S(haveRegion.server, haveRegion.world, haveRegion.dimension, haveRegion.x, haveRegion.z));
            }
        }
        server.sendPacket(mc, new PacketRequestRegionsC2S(haveRegions.server, haveRegions.world, haveRegions.dimension, requests));
    }

    @Override
    public void onWaypoint(PacketWaypointS2C waypoint) {
        Waypoint point = VoxelShareClient.PacketToWp(waypoint);
        ((IWaypoint) point).setSync(true);
        VoxelMapApi.addWaypoint(point);
    }

    @Override
    public void onDeleteWaypoint(PacketDeleteWaypointS2C deleteWaypoint) {
        Waypoint point = VoxelShareClient.PacketToWp(deleteWaypoint.waypoint);
        VoxelMapApi.removeWaypoint(point);
    }

    @Override
    public void onMoveWaypoint(PacketEditWaypointS2C moveWaypoint) {
        Waypoint from = VoxelShareClient.PacketToWp(moveWaypoint.from);
        Waypoint to = VoxelShareClient.PacketToWp(moveWaypoint.to);

        VoxelMapApi.removeWaypoint(from);
        ((IWaypoint) to).setSync(true);
        VoxelMapApi.addWaypoint(to);
    }

    @Override
    public void onFrequency(PacketConfigS2C frequency) {
        server.setConfig(frequency.sendWaypoint, frequency.sendRegion, frequency.sendPosition, frequency.waypointFrequency, frequency.regionFrequency, frequency.positionFrequency);
    }

    @Override
    public void onWorld(PacketWorldS2C world) {
        VoxelMapApi.setCurrentWorld(world.world);
        VoxelShareClient.logToChat("World name set to " + world.world);
    }

    @Override
    public void onPlayerLeave(PacketPlayerLeaveS2C player) {
        synchronized (positions) {
            positions.remove(player.player);
        }
    }

}
