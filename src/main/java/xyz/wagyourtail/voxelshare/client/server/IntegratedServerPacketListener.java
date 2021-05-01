package xyz.wagyourtail.voxelshare.client.server;

import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.minecraft.server.MinecraftServer;
import xyz.wagyourtail.voxelmapapi.IWaypoint;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.client.VoxelShareClient;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketDeleteWaypointC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketEditWaypointC2S;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketWaypointC2S;
import xyz.wagyourtail.voxelshare.server.DedicatedServerPacketListener;

import java.util.UUID;

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


}
