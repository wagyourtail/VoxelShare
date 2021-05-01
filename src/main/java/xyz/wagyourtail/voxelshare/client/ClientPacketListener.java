package xyz.wagyourtail.voxelshare.client;

import com.mamiyaotaru.voxelmap.util.Waypoint;
import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.voxelmapapi.IWaypoint;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.client.endpoints.AbstractServerEndpoint;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketPingC2S;
import xyz.wagyourtail.voxelshare.packets.s2c.*;

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

    }

    @Override
    public void onRegionData(PacketRegionS2C region) {

    }

    @Override
    public void onRequestRegion(PacketRequestRegionS2C requestRegion) {

    }

    @Override
    public void onHaveRegion(PacketHaveRegionS2C haveRegion) {

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

}
