package xyz.wagyourtail.voxelshare.server;

import net.minecraft.server.MinecraftServer;
import xyz.wagyourtail.voxelshare.BasePacketListener;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;
import xyz.wagyourtail.voxelshare.packets.c2s.*;

import java.nio.ByteBuffer;

/**
 * per player instance tracked by connection
 */
public abstract class AbstractServerPacketListener extends BasePacketListener {
    public DedicatedClientEndpoint player;
    public final MinecraftServer server;

    public AbstractServerPacketListener(DedicatedClientEndpoint player, MinecraftServer server) {
        this.player = player;
        this.server = server;
    }

    public abstract void onPing();

    public abstract void onPlayer(PacketSetPlayerC2S setPlayer);

    public void onPosition(PacketPositionC2S position) {
        this.player.setPos(position.x, position.z);
    }

    public abstract void onRegion(PacketRegionC2S region);

    public abstract void onRequestRegion(PacketRequestRegionC2S requestRegion);

    public void onRequestRegions(PacketRequestRegionsC2S requestRegions) {
        requestRegions.children.forEach(this::onRequestRegion);
    }

    public abstract void onHaveRegion(PacketHaveRegionC2S haveRegion);

    public abstract void onHaveRegions(PacketHaveRegionsC2S haveRegions);

    public abstract void onWaypoint(PacketWaypointC2S waypoint);

    public void onWaypoints(PacketWaypointsC2S waypoints) {
        waypoints.children.forEach(this::onWaypoint);
    }

    public abstract void onDeleteWaypoint(PacketDeleteWaypointC2S deleteWaypoint);

    public abstract void onmoveWaypoint(PacketEditWaypointC2S moveWaypoint);

    public abstract void onFrequency(PacketConfigC2S frequency);

    public abstract void onWorld(PacketWorldC2S world);

    public void checkServer(String server) {
        if (!(this instanceof DedicatedServerPacketListener) && !server.equals(this.getServerName())) {
            throw new IllegalStateException("wrong server id for packet");
        }
    }

    public abstract String getServerName();

    @Override
    public void onPacket(PacketOpcodes opcode, ByteBuffer buff) throws UnsupportedOperationException {
        switch (opcode) {
            case PING:
                this.onPing();
                break;
            case World:
                this.onWorld(new PacketWorldC2S(buff));
                break;
            case ConfigSync:
                onFrequency(new PacketConfigC2S(buff));
                break;
            case Player:
                onPlayer(new PacketSetPlayerC2S(buff));
                break;
            case PlayerLeave:
            case Positions:
                throwWrongWay();
                break;
            case Position:
                onPosition(new PacketPositionC2S(buff));
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
            case EditWaypoint:
                onmoveWaypoint(new PacketEditWaypointC2S(buff));
                break;
            case Error:
            default:
                throw new UnsupportedOperationException("Unsupported Opcode");
        }
    }

}
