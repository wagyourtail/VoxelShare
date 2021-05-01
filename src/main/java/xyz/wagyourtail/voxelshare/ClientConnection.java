
package xyz.wagyourtail.voxelshare;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mamiyaotaru.voxelmap.interfaces.AbstractVoxelMap;
import com.mamiyaotaru.voxelmap.interfaces.IWaypointManager;
import com.mamiyaotaru.voxelmap.persistent.CachedRegion;
import com.mamiyaotaru.voxelmap.persistent.CompressibleMapData;
import com.mamiyaotaru.voxelmap.util.BlockStateParser;
import com.mamiyaotaru.voxelmap.util.DimensionContainer;
import com.mamiyaotaru.voxelmap.util.TextUtils;
import com.mamiyaotaru.voxelmap.util.Waypoint;
import com.mojang.util.UUIDTypeAdapter;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.dimension.DimensionType;
import xyz.wagyourtail.voxelshare.interfaces.region.ICachedRegion;
import xyz.wagyourtail.voxelshare.interfaces.region.IIPersistentMap;
import xyz.wagyourtail.voxelshare.interfaces.waypoint.IWaypoint;

/**
 * @author wagyourtail
 *
 */
public class ClientConnection extends WebSocketAdapter {
    private WebSocket ws;
    private final MinecraftClient mc = MinecraftClient.getInstance();
    public String setUUID = null;

    public ClientConnection(String address) throws IOException {
        ws = new WebSocketFactory().createSocket(address).addListener(this);
    }

    @Override
    public void onConnected(WebSocket ws, Map<String, List<String>> headers) throws Exception {
    }

    @Override
    public void onDisconnected(WebSocket ws, WebSocketFrame serverFrame, WebSocketFrame clientFrame, boolean isServer) {
        VoxelShare.INSTANCE.c = null;
    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException exception) {
        onError(websocket, exception);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException ex) {
        ex.printStackTrace();
        VoxelShare.INSTANCE.c = null;
    }

    @Override
    public void onFrame(WebSocket ws, WebSocketFrame frame) {
        if (frame.isBinaryFrame()) {
            ByteBuffer buff = ByteBuffer.wrap(frame.getPayload());
            byte opCode = buff.get();
            switch (opCodes.values()[opCode]) {
                case Pos:
                    recvPlayerPos(buff);
                    break;
                case Waypoints:
                    recvWaypoints(buff);
                    break;
                case RegionData:
                    recvRegion(buff);
                    break;
                case DeleteWaypoint:
                    recvDeleteWaypoint(buff);
                    break;
                case MoveWaypoint:
                default:
                    System.out.println("unknown/bad bytedata");
            }
        } else if (frame.isTextFrame()) {

        }
    }

    /**
     * Data Structure:
     * 
     * [Byte: opcode, Long: lest, Long: most]
     * 
     * @param uuid
     */
    public void setPlayer(String uid) {
        UUID uuid = UUIDTypeAdapter.fromString(uid);
        ByteBuffer buff = ByteBuffer.allocate(Long.BYTES * 2 + 1);
        buff.put((byte) opCodes.Player.ordinal());
        buff.putLong(uuid.getLeastSignificantBits());
        buff.putLong(uuid.getMostSignificantBits());
        ws.sendBinary(buff.array());
        setUUID = uid;
    }

    /**
     * Data Structure:
     * [Byte: opCode, int: serverLength, byte[?] server, int worldLength, byte[?] world, int x, int z]
     * 
     * @param x
     * @param z
     * @throws UnsupportedEncodingException 
     */
    public void sendPos(int x, int z) throws UnsupportedEncodingException {
        byte[] server = VoxelShare.getServer().getBytes(StandardCharsets.UTF_8);
        byte[] world = VoxelShare.getWorld().getBytes(StandardCharsets.UTF_8);
        if (server != null && world != null) {
            ByteBuffer buff = ByteBuffer.allocate(world.length + server.length + Integer.BYTES * 4 + 1);
            buff.put((byte) opCodes.Pos.ordinal());
            buff.putInt(server.length);
            buff.put(server);
            buff.putInt(world.length);
            buff.put(world);
            buff.putInt(x);
            buff.putInt(z);
            ws.sendBinary(buff.array());
        }

    }


    /**
     * Data Structure:
     * [Byte: opCode, int: serverLength, byte[?] server, int worldLength, byte[?] world, Long: updateTime, int: x, int: z, byte[0x10000 * 18]: data,
     * byte[?] keys]
     * 
     * @param cacheReg
     * @throws UnsupportedEncodingException 
     */
    public void sendRegion(CachedRegion cacheReg) throws UnsupportedEncodingException {
        CompressibleMapData data = cacheReg.getMapData();
        BiMap<BlockState, Integer> states = data.getStateToInt();
        byte[] region = data.getData();
        long time = cacheReg.getMostRecentChange();
        StringBuffer sbuff = new StringBuffer();
        if (states != null) {
            Iterator<Map.Entry<BlockState, Integer>> iterator = states.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<BlockState, Integer> current = iterator.next();
                sbuff.append(String.format("%d %s\n", current.getValue(), current.getKey().toString()));
            }
        }
        byte[] blocks = sbuff.toString().getBytes(StandardCharsets.UTF_8);

        byte[] server = ((ICachedRegion) cacheReg).getServer().getBytes(StandardCharsets.UTF_8);
        byte[] world = ((ICachedRegion) cacheReg).getWorld().getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer.allocate(1 + server.length + world.length + Long.BYTES + Integer.BYTES * 4 + region.length + blocks.length);
        buff.put((byte) opCodes.RegionData.ordinal());
        buff.putInt(server.length);
        buff.put(server);
        buff.putInt(world.length);
        buff.put(world);
        buff.putLong(time);
        buff.putInt(cacheReg.getX());
        buff.putInt(cacheReg.getZ());
        buff.put(region);
        buff.put(blocks);
        ws.sendBinary(buff.array());
    }

    /**
     * Data Structure:
     * [Byte: opCode, int serverLength, byte[?] server, pointData[]: points]
     * 
     * pointData Structure:
     * [int:nameLength, String:name, int: x, int: y, int: z, byte: enabled, int:
     * red, int: green, int: blue, int: suffixLength, String: suffix, int:
     * worldLength, String: world, int: dimensionsLength, String: dimensions]
     * 
     * @param waypoints
     * @throws UnsupportedEncodingException 
     */
    public void sendWaypoints(List<Waypoint> waypoints, String serverName) throws UnsupportedEncodingException {
        byte[] server = serverName.getBytes(StandardCharsets.UTF_8);
        List<byte[]> buffList = new LinkedList<>();
        for (Waypoint pt : waypoints) {
            buffList.add(((IWaypoint)pt).toBytes());
        }
        int bytes = 1;
        for (byte[] buff : buffList) {
            bytes += buff.length;
        }
        ByteBuffer buff = ByteBuffer.allocate(bytes + server.length + Integer.BYTES);
        buff.put((byte) opCodes.Waypoints.ordinal());
        buff.putInt(server.length);
        buff.put(server);
        for (byte[] b : buffList) {
            buff.put(b);
        }
        ws.sendBinary(buff.array());
    }
    
    /**
     * Data Structure:
     * [Byte: opCode, int serverLength, byte[?] server, pointData: point]
     * 
     * pointData Structure:
     * [int:nameLength, String:name, int: x, int: y, int: z, byte: enabled, int:
     * red, int: green, int: blue, int: suffixLength, String: suffix, int:
     * worldLength, String: world, int: dimensionsLength, String: dimensions]
     * 
     * @param buff
     */
    public void recvDeleteWaypoint(ByteBuffer buff) {
        IWaypointManager man = AbstractVoxelMap.getInstance().getWaypointManager();
        Set<String> knownSubWorlds = man.getKnownSubworldNames();
        byte[] server = new byte[buff.getInt()];
        buff.get(server);
        byte[] name = new byte[buff.getInt()];
        buff.get(name);
        int x = buff.getInt();
        int y = buff.getInt();
        int z = buff.getInt();
        boolean enabled = buff.get() != 0;
        float red = buff.getInt() / 255F;
        float green = buff.getInt() / 255F;
        float blue = buff.getInt() / 255F;
        byte[] suffix = new byte[buff.getInt()];
        buff.get(suffix);
        byte[] world = new byte[buff.getInt()];
        buff.get(world);
        byte[] dimensions = new byte[buff.getInt()];
        buff.get(dimensions);
        Set<DimensionContainer> dimSet = new TreeSet<>();
        for (String dim : new String(dimensions, StandardCharsets.UTF_8).split("#")) {
            dimSet.add(AbstractVoxelMap.getInstance().getDimensionManager().getDimensionContainerByIdentifier(dim));
        }
        if (dimSet.size() == 0) {
            dimSet.add(AbstractVoxelMap.getInstance().getDimensionManager()
                .getDimensionContainerByResourceLocation(DimensionType.OVERWORLD_REGISTRY_KEY.getValue()));
        }
        if (!new String(world, StandardCharsets.UTF_8).equals("")) {
            knownSubWorlds.add(TextUtils.descrubName(new String(world, StandardCharsets.UTF_8)));
        }
        Waypoint pt = new Waypoint(new String(name, StandardCharsets.UTF_8), x, y, z, enabled, red, green, blue, new String(suffix, StandardCharsets.UTF_8),
            new String(world, StandardCharsets.UTF_8), (TreeSet<DimensionContainer>) dimSet);
        
        for (Waypoint point : man.getWaypoints()) {
            if (point.x == pt.x && point.y == pt.y && point.z == pt.z) {
                man.deleteWaypoint(point);
                break;
            }
        }
    }
    
    public void sendDeleteWaypoint(Waypoint pt, String serverName) {
        byte[] server = serverName.getBytes(StandardCharsets.UTF_8);
       
        String dimensions = "";
        for (DimensionContainer dim : pt.dimensions) {
            dimensions += dim.getStorageName() + "#";
        }
        if (dimensions.equals("")) {
            dimensions = AbstractVoxelMap.getInstance().getDimensionManager()
                .getDimensionContainerByResourceLocation(DimensionType.OVERWORLD_REGISTRY_KEY.getValue())
                .getStorageName();
        }
        byte[] name = TextUtils.scrubName(pt.name).getBytes(StandardCharsets.UTF_8);
        byte enabled = (byte) (pt.enabled ? 1 : 0);
        byte[] suffix = pt.imageSuffix.getBytes(StandardCharsets.UTF_8);
        byte[] world = (TextUtils.scrubName(pt.world)).getBytes(StandardCharsets.UTF_8);
        byte[] dim = dimensions.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buff = ByteBuffer
            .allocate(server.length + name.length + suffix.length + world.length + dim.length + Integer.BYTES * 11 + 2);
        

        buff.put((byte) opCodes.Waypoints.ordinal());
        buff.putInt(server.length);
        buff.put(server);
        
        buff.putInt(name.length);
        buff.put(name);
        buff.putInt(pt.x);
        buff.putInt(pt.y);
        buff.putInt(pt.z);
        buff.put(enabled);
        buff.putInt((int) (pt.red * 255));
        buff.putInt((int) (pt.green * 255));
        buff.putInt((int) (pt.blue * 255));
        buff.putInt(suffix.length);
        buff.put(suffix);
        buff.putInt(world.length);
        buff.put(world);
        buff.putInt(dim.length);
        buff.put(dim);
        
        ws.sendBinary(buff.array());
    }

    /**
     * Data Structure:
     * [Byte: opCode, int serverLength, byte[?] server, int worldLength, byte[?] world, int x, int z]
     * 
     * @param x
     * @param z
     * @throws UnsupportedEncodingException 
     */
    public void requestRegion(int x, int z) throws UnsupportedEncodingException {
        byte[] server = VoxelShare.getServer().getBytes(StandardCharsets.UTF_8);
        byte[] world = VoxelShare.getWorld().getBytes(StandardCharsets.UTF_8);
        if (server != null && world != null) {
            ByteBuffer buff = ByteBuffer.allocate(server.length + world.length + Integer.BYTES * 4 + 1);
            buff.put((byte) opCodes.RequestRegion.ordinal());
            buff.putInt(server.length);
            buff.put(server);
            buff.putInt(world.length);
            buff.put(world);
            buff.putInt(x);
            buff.putInt(z);
            ws.sendBinary(buff.array());
        }
    }

    /**
     * Data Structure:
     * [Byte: opCode, Int: serverNameLength, byte[?]: serverName, Int: worldNameLength, byte[?]: worldName, Long: updateTime, int: x, int: z, byte[0x10000 * 18]: data,
     * byte[?] keys]
     * 
     * @param buff
     */
    public void recvRegion(ByteBuffer buff) {
        AbstractVoxelMap avm = AbstractVoxelMap.getInstance();
        ClientWorld world = mc.world;
        String serverName = VoxelShare.getServer();
        String worldName = VoxelShare.getWorld();
        byte[] sName = new byte[buff.getInt()];
        buff.get(sName);
        byte[] wName = new byte[buff.getInt()];
        buff.get(wName);
        if (!serverName.equals(new String(sName, StandardCharsets.UTF_8)) || !worldName.equals(new String(wName, StandardCharsets.UTF_8))) {
            return;
        }
        long remoteTime = buff.getLong();
        int x = buff.getInt();
        int z = buff.getInt();
        CachedRegion cacheReg = ((IIPersistentMap) avm.getPersistentMap()).getRegion(x, z);
        if (cacheReg == null) cacheReg = new CachedRegion(avm.getPersistentMap(), String.format("%d,%d", x, z), world, avm.getWaypointManager().getCurrentWorldName(), avm.getWaypointManager().getCurrentSubworldDescriptor(false), x, z);
        CompressibleMapData data = cacheReg.getMapData();
        synchronized (data) {
            BiMap<BlockState, Integer> states = data.getStateToInt();
            byte[] region = data.getData();
            long localTime = cacheReg.getMostRecentChange();
            int regionLength = data.getWidth() * data.getHeight();
            byte[] blockData = new byte[regionLength * 18];
            buff.get(blockData);
    
            String blockStates = new String(buff.slice().array(), StandardCharsets.UTF_8);
            BiMap<BlockState, Integer> blockStateKeys = HashBiMap.create();
            for (String state : blockStates.split("\n")) {
                BlockStateParser.parseLine(state, blockStateKeys);
            }
    
            Map<Integer, Integer> remappings = new HashMap<>();
    
            Map<String, BlockState> blockStateString = new LinkedHashMap<>();
            for (BlockState state : states.keySet()) {
                blockStateString.put(state.toString(), state);
            }
    
            int i = states.size();
            for (Map.Entry<BlockState, Integer> state : blockStateKeys.entrySet()) {
                String keyStr = state.getKey().toString();
                if (blockStateString.containsKey(keyStr)) {
                    int from = state.getValue();
                    int to = states.get(state.getKey());
                    remappings.put(from, to);
                } else {
                    remappings.put(state.getValue(), i);
                    states.put(state.getKey(), i++);
                }
            }
    
            for (i = 0; i < 0x10000; ++i) {
                if (remoteTime < localTime && region[i] != 0) continue;
                if (blockData[i] != 0) {
                    int index = (int) blockData[i + 0x10000] << 8 | blockData[i + 0x20000];
                    if (remappings.containsKey(index)) {
                        index = remappings.get(index);
                        blockData[i + 0x10000] = (byte) (index >> 8 & 0xFF);
                        blockData[i + 0x20000] = (byte) (index & 0xFF);
                    }
                    int n = i;
                    while (n < 0x10000 * 18) {
                        region[n] = blockData[n];
                        n += 0x10000;
                    }
                }
            }
            cacheReg.refresh(false);
        }
    }

    /**
     * Data Structure:
     * [Byte: opcode, Int: serverNameLength, byte[?]: serverName, Int: worldNameLength, byte[?]: worldName, Long: lest, Long: most, Int: x, Int: z]
     * 
     * @param buff
     */
    public void recvPlayerPos(ByteBuffer buff) {
        String serverName = VoxelShare.getServer();
        String worldName = VoxelShare.getWorld();
        byte[] sName = new byte[buff.getInt()];
        buff.get(sName);
        byte[] wName = new byte[buff.getInt()];
        buff.get(wName);
        if (!serverName.equals(new String(sName, StandardCharsets.UTF_8)) || !worldName.equals(new String(wName, StandardCharsets.UTF_8))) {
            return;
        }
        long least = buff.getLong();
        long most = buff.getLong();
        UUID uuid = new UUID(most, least);
        int x = buff.getInt();
        int z = buff.getInt();
        PlayerData.updateEntry(uuid, x, z);
    }

    /**
     * Data Structure:
     * [Byte: opCode, int: serverLength, byte[?]: server, pointData[]: points]
     * 
     * @param buff
     */
    public void recvWaypoints(ByteBuffer buff) {
        IWaypointManager man = AbstractVoxelMap.getInstance().getWaypointManager();
        byte[] server = new byte[buff.getInt()];
        buff.get(server);
        while (buff.hasRemaining()) {
            Waypoint pt = IWaypoint.fromBytes(man, buff);
            for (Waypoint point : man.getWaypoints()) {
                if (point.x == pt.x && point.y == pt.y && point.z == pt.z) {
                    man.deleteWaypoint(point);
                    break;
                }
            }
            man.addWaypoint(pt);
        }
    }

    public void connect() {
        ws.connectAsynchronously();
    }

    public void disconnect() {
        ws.disconnect();
    }

    public WebSocket getWebSocket() {
        return ws;
    }

    public enum opCodes {
        Player, Pos, RegionData, RequestRegion, Waypoints, DeleteWaypoint, MoveWaypoint
    }

}
