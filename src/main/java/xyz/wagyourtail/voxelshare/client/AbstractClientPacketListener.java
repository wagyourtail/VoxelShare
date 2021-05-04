package xyz.wagyourtail.voxelshare.client;

import net.minecraft.client.MinecraftClient;
import xyz.wagyourtail.voxelshare.BasePacketListener;
import xyz.wagyourtail.voxelmapapi.VoxelMapApi;
import xyz.wagyourtail.voxelshare.client.endpoints.AbstractServerEndpoint;
import xyz.wagyourtail.voxelshare.client.endpoints.DedicatedServerEndpoint;
import xyz.wagyourtail.voxelshare.packets.PacketOpcodes;
import xyz.wagyourtail.voxelshare.packets.s2c.*;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AbstractClientPacketListener extends BasePacketListener {
    public final AbstractServerEndpoint server;
    public final MinecraftClient mc;
    public final Map<UUID, PacketPositionS2C> positions = new LinkedHashMap<>();

    public AbstractClientPacketListener(AbstractServerEndpoint server, MinecraftClient mc) {
        this.server = server;
        this.mc = mc;
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

    public abstract void onHaveRegions(PacketHaveRegionsS2C haveRegions);

    public abstract void onWaypoint(PacketWaypointS2C waypoint);

    @SuppressWarnings({"rawtypes","unchecked"})
    public void onWaypoints(PacketWaypointsS2C waypoints) {
        checkServer(waypoints.server);
        ((List<PacketWaypointS2C>)(List) waypoints.children).forEach(this::onWaypoint);
    }

    public abstract void onDeleteWaypoint(PacketDeleteWaypointS2C deleteWaypoint);

    public abstract void onMoveWaypoint(PacketEditWaypointS2C moveWaypoint);

    public void checkServer(String server) {
        if (!(this.server instanceof DedicatedServerEndpoint) && !server.equals(this.server.getServerName())) {
            throw new IllegalStateException("wrong server id for packet");
        }
    }

    public void checkWorld(String world) {
        if (!world.equals(VoxelMapApi.getCurrentWorld())) {
            throw new IllegalStateException("wrong world id for packet found: " + world + ", expected: " + VoxelMapApi.getCurrentWorld());
        }
    }

    public void checkDimension(String dimension) {
        if (!dimension.equals(mc.world.getRegistryKey().getValue().getPath())) {
            throw new IllegalStateException("wrong dimension id for packet found: " + dimension + ", expected: " + mc.world.getRegistryKey().getValue().getPath());
        }
    }

    public abstract void onFrequency(PacketConfigS2C frequency);

    public abstract void onWorld(PacketWorldS2C world);

    public abstract void onPlayerLeave(PacketPlayerLeaveS2C player);

    @Override
    public void onPacket(PacketOpcodes opcode, ByteBuffer buff) throws UnsupportedOperationException {
        switch (opcode) {
            case PING:
                onPing();
                break;
            case World:
                onWorld(new PacketWorldS2C(buff));
                break;
            case ConfigSync:
                onFrequency(new PacketConfigS2C(buff));
                break;
            case Player:
                throwWrongWay();
                break;
            case PlayerLeave:
                onPlayerLeave(new PacketPlayerLeaveS2C(buff));
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
            case EditWaypoint:
                onMoveWaypoint(new PacketEditWaypointS2C(buff));
                break;
            case Error:
            default:
                throw new UnsupportedOperationException("Unsupported Opcode");
        }
    }

}
