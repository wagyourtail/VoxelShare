package xyz.wagyourtail.voxelshareserver.server;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import xyz.wagyourtail.voxelshareserver.PlayerData;
import xyz.wagyourtail.voxelshareserver.VoxelShareServer;
import xyz.wagyourtail.voxelshareserver.server.world.WorldData;

public class ServerData {
    public static final Map<String, ServerData> servers = new HashMap<>();
    
    public final String serverName;
    public final Set<PlayerData> players = new LinkedHashSet<>();
    public final Map<String, WorldData> worlds = new HashMap<>();
    public final Set<Waypoint> waypoints = new LinkedHashSet<>();
    
    public ServerData(String server) {
        this.serverName = server;
        servers.put(server, this);
    }
    
    public WorldData getOrAddWorld(String worldName) {
        WorldData world = null;
        synchronized (worlds) {
            world = worlds.get(worldName);
            if (world == null) {
                world = new WorldData(worldName);
                worlds.put(worldName, world);
            }
        }
        return world;
    }
    
    public Waypoint addOrAmendWaypoint(Waypoint point) {
        synchronized (waypoints) {
            waypoints.remove(point);
            waypoints.add(point);
        }
        
        return point;
    }
    
    public Waypoint addOrMoveWaypoint(Waypoint from, Waypoint to) {
        synchronized (waypoints) {
            waypoints.remove(from);
            waypoints.add(to);
        }
        
        return to;
    }
    
    public Waypoint deleteWaypoint(Waypoint point) {
        synchronized (waypoints) {
            waypoints.remove(point);
        }
        
        return point;
    }
    
    public byte[] createWaypointsPacket() {
        synchronized (waypoints) {
            List<byte[]> waypoints = this.waypoints.stream().map(e -> e.toBytes()).collect(Collectors.toList());
            byte[] server = serverName.getBytes(StandardCharsets.UTF_8);
            int bytes = 1 + Integer.BYTES + server.length;
            for (byte[] wp : waypoints) {
                bytes += wp.length;
            }
            
            ByteBuffer buff = ByteBuffer.allocate(bytes);
            buff.put((byte) VoxelShareServer.opCodes.Waypoints.ordinal());
            buff.putInt(server.length);
            buff.put(server);
            for (byte[] wp : waypoints) {
                buff.put(wp);
            }
            return buff.array();
        }
    }
    
    public byte[] createDeleteWaypointPacket(Waypoint p) {
        byte[] server = serverName.getBytes(StandardCharsets.UTF_8);
        byte[] wp = p.toBytes();
        ByteBuffer buff = ByteBuffer.allocate(1 + Integer.BYTES + server.length + wp.length);
        buff.put((byte) VoxelShareServer.opCodes.DeleteWaypoint.ordinal());
        buff.putInt(server.length);
        buff.put(server);
        buff.put(wp);
        
        return buff.array();
    }

    public void sendPacketExclude(PlayerData exclude, byte[] packet) {
        synchronized (players) {
            Iterator<PlayerData> players = this.players.stream().filter(e -> e != exclude).iterator();
            while (players.hasNext()) {   
                players.next().send(packet);
            };
        }
    }
}
