package xyz.wagyourtail.voxelshareserver;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.java_websocket.WebSocket;

import xyz.wagyourtail.voxelshareserver.server.ServerData;
import xyz.wagyourtail.voxelshareserver.server.Waypoint;
import xyz.wagyourtail.voxelshareserver.server.world.WorldData;

public class PlayerData {
    private final WebSocket conn;
    private UUID uuid = null;
    private ServerData server = null;
    private WorldData world = null;
    @SuppressWarnings("unused")
    private int x = 0, z = 0;

    public final BlockingQueue<ByteBuffer> dataQueue = new LinkedBlockingQueue<>();
    public final Thread thread;

    public PlayerData(WebSocket conn) {
        this.conn = conn;
        this.thread = new Thread(() -> {
            try {
                while (conn.isOpen()) {
                    ByteBuffer data = dataQueue.take();
                    synchronized (dataQueue) {
                        switch (VoxelShareServer.opCodes.values()[data.get()]) {
                            case Player:
                                setUuid(data);
                                break;
                            case Pos:
                                setPos(data);
                                break;
                            case RegionData:
                                break;
                            case RequestRegion:
                                break;
                            case Waypoints:
                                recvWaypoints(data);
                                break;
                            case DeleteWaypoint:
                                recvDelWaypoint(data);
                                break;
                            case MoveWaypoint:
                                recvMoveWaypoint(data);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (InterruptedException e) {} finally {
                this.setServer(null);
                VoxelShareServer.allClients.remove(conn);
                System.out.println(String.format("%d clients connected.", VoxelShareServer.allClients.size()));
            }

        });
        this.thread.start();

    }

    private void recvWaypoints(ByteBuffer buff) {
        byte[] server = new byte[buff.getInt()];
        buff.get(server);

        String serverString = new String(server, StandardCharsets.UTF_8);
        if (this.server == null || !serverString.equals(this.server.serverName)) setServer(serverString);

        while (buff.hasRemaining()) {
            this.server.addOrAmendWaypoint(Waypoint.fromBytes(buff));
        }

        buff.rewind();
        this.server.sendPacketExclude(this, buff.array());
    }

    private void recvDelWaypoint(ByteBuffer buff) {
        byte[] server = new byte[buff.getInt()];
        buff.get(server);

        String serverString = new String(server, StandardCharsets.UTF_8);
        if (this.server == null || !serverString.equals(this.server.serverName)) setServer(serverString);

        this.server.deleteWaypoint(Waypoint.fromBytes(buff));

        buff.rewind();
        this.server.sendPacketExclude(this, buff.array());
    }

    private void recvMoveWaypoint(ByteBuffer buff) {
        byte[] server = new byte[buff.getInt()];
        buff.get(server);

        String serverString = new String(server, StandardCharsets.UTF_8);
        if (this.server == null || !serverString.equals(this.server.serverName)) setServer(serverString);

        Waypoint from = Waypoint.fromBytes(buff);
        Waypoint to = Waypoint.fromBytes(buff);
        this.server.addOrMoveWaypoint(from, to);

        buff.rewind();
        this.server.sendPacketExclude(this, buff.array());
    }

    private void setPos(ByteBuffer buff) {
        byte[] server = new byte[buff.getInt()];
        buff.get(server);
        byte[] world = new byte[buff.getInt()];
        buff.get(world);
        String serverString = new String(server, StandardCharsets.UTF_8);
        if (this.server == null || !serverString.equals(this.server.serverName)) setServer(serverString);
        String worldString = new String(world, StandardCharsets.UTF_8);
        if (this.world == null || !worldString.equals(this.world.worldName)) setWorld(worldString);
        x = buff.getInt();
        z = buff.getInt();

        buff.rewind();
        this.world.sendPacketExclude(this, buff.array());
    }

    private void setUuid(ByteBuffer buff) {
        long least = buff.getLong();
        long most = buff.getLong();
        uuid = new UUID(most, least);
        Thread.currentThread().setName(uuid.toString());
    }

    private void setServer(String serverName) {
        setWorld(null);
        if (this.server != null) {
            synchronized (ServerData.servers) {
                server.players.remove(this);
            }
        }
        if (serverName != null) {
            synchronized (ServerData.servers) {
                ServerData serverC = ServerData.servers.get(serverName);
                if (serverC == null) serverC = new ServerData(serverName);
                server = serverC;
            }
            synchronized (server.players) {
                server.players.add(this);
            }
        } else {
            server = null;
        }
    }

    private void setWorld(String worldName) {
        if (server != null) {
            if (world != null) {
                synchronized (world.players) {
                    world.players.remove(this);
                }
            }
            if (worldName != null) {
                world = server.getOrAddWorld(worldName);
                synchronized (world.players) {
                    world.players.add(this);
                }
            } else {
                world = null;
            }
        }
    }

    public void send(byte[] bytes) {
        conn.send(bytes);
    }
}
