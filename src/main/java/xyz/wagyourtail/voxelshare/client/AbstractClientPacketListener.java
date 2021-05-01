package xyz.wagyourtail.voxelshare.client;

import xyz.wagyourtail.voxelshare.BasePacketListener;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;
import xyz.wagyourtail.voxelshare.packets.s2c.*;

import java.nio.ByteBuffer;
import java.util.List;

public abstract class AbstractClientPacketListener extends BasePacketListener {
    public final Server server;

    public AbstractClientPacketListener(Server server) {
        this.server = server;
    }

    public abstract void onPing();

    public abstract void onPositionPacket(PacketPositionS2C position);

    public void onPositionsPacket(PacketPositionsS2C positions) {
        positions.children.forEach(this::onPositionPacket);
    }

    public abstract void onRegionData(PacketRegionS2C region);

    public abstract void onRequestRegion(PacketRequestRegionS2C requestRegion);

    @SuppressWarnings({"rawtypes","unchecked"})
    public void onRequestRegions(PacketRequestRegionsS2C requestRegions) {
        checkServer(requestRegions.server);
        ((List<PacketRequestRegionS2C>)(List)requestRegions.children).forEach(this::onRequestRegion);
    }

    public abstract void onHaveRegion(PacketHaveRegionS2C haveRegion);

    @SuppressWarnings({"rawtypes","unchecked"})
    public void onHaveRegions(PacketHaveRegionsS2C haveRegions) {
        checkServer(haveRegions.server);
        ((List<PacketHaveRegionS2C>)(List)haveRegions.children).forEach(this::onHaveRegion);
    }

    public abstract void onWaypoint(PacketWaypointS2C waypoint);

    @SuppressWarnings({"rawtypes","unchecked"})
    public void onWaypoints(PacketWaypointsS2C waypoints) {
        checkServer(waypoints.server);
        ((List<PacketWaypointS2C>)(List) waypoints.children).forEach(this::onWaypoint);
    }

    public abstract void onDeleteWaypoint(PacketDeleteWaypointS2C deleteWaypoint);

    public abstract void onMoveWaypoint(PacketMoveWaypointS2C moveWaypoint);

    public void checkServer(String server) {
        if (!this.server.isDedicated() && !server.equals(this.server.getServerName())) {
            throw new IllegalStateException("wrong server id for packet");
        }
    }

    public abstract void onFrequency(PacketFrequencyS2C frequency);

    @Override
    public void onPacket(ByteBuffer buff) throws UnsupportedOperationException {
        switch (PacketOpcodes.getByOpcode(buff.get())) {
            case PING:
                onPing();
                break;
            case Frequency:

                break;
            case Player:
                throwWrongWay();
                break;
            case Position:
                onPositionPacket(new PacketPositionS2C(buff));
                break;
            case Positions:
                onPositionsPacket(new PacketPositionsS2C(buff));
                break;
            case RegionData:
                onRegionData(new PacketRegionS2C(buff));
                break;
            case RequestRegion:
                onRequestRegion(new PacketRequestRegionS2C(buff));
                break;
            case RequestRegions:
                onRequestRegions(new PacketRequestRegionsS2C(buff));
                break;
            case HaveRegion:
                onHaveRegion(new PacketHaveRegionS2C(buff));
                break;
            case HaveRegions:
                onHaveRegions(new PacketHaveRegionsS2C(buff));
                break;
            case Waypoint:
                onWaypoint(new PacketWaypointS2C(buff));
                break;
            case Waypoints:
                onWaypoints(new PacketWaypointsS2C(buff));
                break;
            case DeleteWaypoint:
                onDeleteWaypoint(new PacketDeleteWaypointS2C(buff));
                break;
            case MoveWaypoint:
                onMoveWaypoint(new PacketMoveWaypointS2C(buff));
                break;
            case UNKNOWN:
            default:
                throw new UnsupportedOperationException("Unsupported Opcode");
        }
    }

}
