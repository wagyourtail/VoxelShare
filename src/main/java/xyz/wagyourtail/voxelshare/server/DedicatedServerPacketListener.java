package xyz.wagyourtail.voxelshare.server;

import xyz.wagyourtail.voxelshare.packets.c2s.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DedicatedServerPacketListener extends AbstractServerPacketListener {
    public static Map<UUID, DedicatedServerPacketListener> listeners = new HashMap<>();

    public DedicatedServerPacketListener(UUID player) {
        super(player);
    }

    @Override
    public void onPing() {
    }

    @Override
    public void onPlayer(PacketSetPlayerC2S setPlayer) {
        //ignore we'll just get it from the normal packet handler when we create the listener.
    }

    @Override
    public void onPosition(PacketPositionC2S position) {

    }

    @Override
    public void onRegion(PacketRegionC2S region) {

    }

    @Override
    public void onRequestRegion(PacketRequestRegionC2S requestRegion) {

    }

    @Override
    public void onHaveRegion(PacketHaveRegionC2S haveRegion) {

    }

    @Override
    public void onWaypoint(PacketWaypointC2S waypoint) {

    }

    @Override
    public void onDeleteWaypoint(PacketDeleteWaypointC2S deleteWaypoint) {

    }

    @Override
    public void onmoveWaypoint(PacketMoveWaypointC2S moveWaypoint) {

    }

    @Override
    public void onFrequency(PacketFrequencyC2S frequency) {

    }

    @Override
    public String getServerName() {
        return null;
    }

}
