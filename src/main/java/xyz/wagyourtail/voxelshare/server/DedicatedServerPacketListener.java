package xyz.wagyourtail.voxelshare.server;

import net.minecraft.server.MinecraftServer;
import xyz.wagyourtail.voxelshare.VoxelShare;
import xyz.wagyourtail.voxelshare.packets.c2s.*;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketConfigS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketRequestRegionS2C;
import xyz.wagyourtail.voxelshare.packets.s2c.PacketWorldS2C;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class DedicatedServerPacketListener extends AbstractServerPacketListener {
    public DedicatedServerPacketListener(UUID player, MinecraftServer server) {
        super(new DedicatedClientEndpoint(player), server);
    }

    public DedicatedServerPacketListener(DedicatedClientEndpoint player, MinecraftServer server) {
        super(player, server);
    }

    @Override
    public void onPing() {
        //keepalive from client.
    }

    @Override
    public void onPlayer(PacketSetPlayerC2S setPlayer) {
        //ignore we'll just get it from the normal packet handler when we create the listener.
    }

    @Override
    public void onPosition(PacketPositionC2S position) {
        //ignore we'll just get this from their entity on dedicated
    }

    @Override
    public void onRegion(PacketRegionC2S region) {
        //TODO
    }

    @Override
    public void onRequestRegion(PacketRequestRegionC2S requestRegion) {
        //TODO
    }

    @Override
    public void onHaveRegion(PacketHaveRegionC2S haveRegion) {
        //TODO
    }

    @Override
    public void onHaveRegions(PacketHaveRegionsC2S haveRegions) {

    }

    @Override
    public void onWaypoint(PacketWaypointC2S waypoint) {
        //TODO
    }

    @Override
    public void onDeleteWaypoint(PacketDeleteWaypointC2S deleteWaypoint) {
        //TODO
    }

    @Override
    public void onmoveWaypoint(PacketEditWaypointC2S moveWaypoint) {
        //TODO
    }

    @Override
    public void onFrequency(PacketConfigC2S frequency) {
        player.setConfig(frequency.sendWaypoint, frequency.sendRegion, frequency.sendPosition, frequency.waypointFrequency, frequency.regionFrequency, frequency.positionFrequency);
        player.sendPacket(server, new PacketConfigS2C(VoxelShare.config));
    }

    @Override
    public void onWorld(PacketWorldC2S world) {
        String actualWorld = "";
        if (!world.world.equals(actualWorld)) {
            VoxelShareServer.logServerMessage("Setting " + player.player + " world to " + actualWorld);
            player.sendPacket(server, new PacketWorldS2C(world.server, actualWorld));
        } else {
            VoxelShareServer.logServerMessage("world name from " + player.player + "already correct.");
        }
    }

    @Override
    public String getServerName() {
        return null;
    }

}
