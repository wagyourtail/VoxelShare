package xyz.wagyourtail.voxelshare.client;

import xyz.wagyourtail.voxelshare.packets.s2c.*;

public class ClientPacketListener extends AbstractClientPacketListener {
    public ClientPacketListener(Server server) {
        super(server);
    }

    @Override
    public void onPing() {

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

    }

    @Override
    public void onDeleteWaypoint(PacketDeleteWaypointS2C deleteWaypoint) {

    }

    @Override
    public void onMoveWaypoint(PacketMoveWaypointS2C moveWaypoint) {

    }

    @Override
    public void onFrequency(PacketFrequencyS2C frequency) {

    }

}
