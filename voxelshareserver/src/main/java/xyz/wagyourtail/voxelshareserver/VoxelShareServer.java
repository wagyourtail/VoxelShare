package xyz.wagyourtail.voxelshareserver;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class VoxelShareServer extends WebSocketServer {
    
    public static Map<WebSocket, PlayerData> allClients = new HashMap<>();
    
    
    public VoxelShareServer(int port) {
        super(new InetSocketAddress(port));
    }
    
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        synchronized (allClients) {
            allClients.put(conn, new PlayerData(conn));
            System.out.println(String.format("%d clients connected.", allClients.size()));
        }
    }

    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        synchronized (allClients) {
            PlayerData client = allClients.get(conn);
            synchronized (client.dataQueue) {
                if (client.dataQueue.isEmpty()) {
                    client.thread.interrupt();
                }
            }
        }
    }

    @Override
    public void onMessage( WebSocket conn, ByteBuffer message ) {
        try {
            BlockingQueue<ByteBuffer> queue = allClients.get(conn).dataQueue;
            synchronized (queue) {
                queue.put(message);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    public void onMessage(WebSocket conn, String message) {}
    
    
    @Override
    public void onError(WebSocket conn, Exception ex) {}

    @Override
    public void onStart() {}

    
    public static enum opCodes {
        Player, Pos, RegionData, RequestRegion, Waypoints, DeleteWaypoint, MoveWaypoint
    }
    

    public static void main(String... args) {
        VoxelShareServer s = new VoxelShareServer(8081);
        s.start();
    }

}
