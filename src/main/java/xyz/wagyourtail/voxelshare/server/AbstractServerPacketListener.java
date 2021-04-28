package xyz.wagyourtail.voxelshare.server;

import xyz.wagyourtail.voxelshare.BasePacketListener;
import xyz.wagyourtail.voxelshare.client.DedicatedServer;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;
import xyz.wagyourtail.voxelshare.packets.c2s.*;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * per player instance tracked by connection
 */
public abstract class AbstractServerPacketListener extends BasePacketListener {
    public UUID player;

    public AbstractServerPacketListener(UUID player) {
        this.player = player;
    }

    public abstract void onPing();

    public abstract void onPlayer(PacketSetPlayerC2S setPlayer);

    public abstract void onPosition(PacketPositionC2S position);

    public abstract void onRegion(PacketRegionC2S region);

    public abstract void onRequestRegion(PacketRequestRegionC2S requestRegion);

    public void onRequestRegions(PacketRequestRegionsC2S requestRegions) {
        requestRegions.children.forEach(this::onRequestRegion);
    }

    public abstract void onHaveRegion(PacketHaveRegionC2S haveRegion);

    public void onHaveRegions(PacketHaveRegionsC2S haveRegions) {
        haveRegions.children.forEach(this::onHaveRegion);
    }

    public abstract void onWaypoint(PacketWaypointC2S waypoint);

    public void onWaypoints(PacketWaypointsC2S waypoints) {
        waypoints.children.forEach(this::onWaypoint);
    }

    public abstract void onDeleteWaypoint(PacketDeleteWaypointC2S deleteWaypoint);

    public abstract void onmoveWaypoint(PacketMoveWaypointC2S moveWaypoint);

    public abstract void onFrequency(PacketFrequencyC2S frequency);


    public void checkServer(String server) {
        if (!((Object)this instanceof DedicatedServer) && !server.equals(this.getServerName())) {
            throw new IllegalStateException("wrong server id for packet");
        }
    }

    public abstract String getServerName();

    @Override
    public void onPacket(ByteBuffer buff) throws UnsupportedOperationException {
        switch (PacketOpcodes.getByOpcode(buff.get())) {
            case PING:
                this.onPing();
                break;
            case Frequency:
                onFrequency(new PacketFrequencyC2S(buff));
                break;
            case Player:
                onPlayer(new PacketSetPlayerC2S(buff));
                break;
            case Position:
                onPosition(new PacketPositionC2S(buff));
                break;
            case Positions:
                throwWrongWay();
                break;
            case RegionData:
                onRegion(new PacketRegionC2S(buff));
                break;
            case RequestRegion:
                onRequestRegion(new PacketRequestRegionC2S(buff));
                break;
            case RequestRegions:
                onRequestRegions(new PacketRequestRegionsC2S(buff));
                break;
            case HaveRegion:
                onHaveRegion(new PacketHaveRegionC2S(buff));
                break;
            case HaveRegions:
                onHaveRegions(new PacketHaveRegionsC2S(buff));
                break;
            case Waypoint:
                onWaypoint(new PacketWaypointC2S(buff));
                break;
            case Waypoints:
                onWaypoints(new PacketWaypointsC2S(buff));
                break;
            case DeleteWaypoint:
                onDeleteWaypoint(new PacketDeleteWaypointC2S(buff));
                break;
            case MoveWaypoint:
                onmoveWaypoint(new PacketMoveWaypointC2S(buff));
                break;
            case UNKNOWN:
            default:
                throw new UnsupportedOperationException("Unsupported Opcode");
        }
    }

}
