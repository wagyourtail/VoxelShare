package xyz.wagyourtail.voxelshareserver.server.world;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import xyz.wagyourtail.voxelshareserver.PlayerData;

public class WorldData {
    public final String worldName;
    public Set<PlayerData> players = new LinkedHashSet<>();
    
    public WorldData(String name) {
        this.worldName = name;
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
