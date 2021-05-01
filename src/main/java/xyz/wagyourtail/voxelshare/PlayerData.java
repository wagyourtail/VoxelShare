package xyz.wagyourtail.voxelshare;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

public class PlayerData {
    private static MinecraftClient mc = MinecraftClient.getInstance();
    public static Map<UUID, PlayerData> players = new LinkedHashMap<>();
    public PlayerListEntry entry;
    public long updateTime;
    public int x;
    public int z;
    
    public PlayerData(PlayerListEntry entry, int x, int z) {
        this.entry = entry;
        setPos(x, z);
    }
    
    public void setPos(int x, int z) {
        this.x = x;
        this.z = z;
    }
    
    public static void updateEntry(UUID uuid, int x, int z) {
        synchronized (players) {
            try {
                if (players.containsKey(uuid)) {
                    players.get(uuid).setPos(x, z);
                } else {
                    for (PlayerListEntry e : mc.getNetworkHandler().getPlayerList()) {
                        if (e.getProfile().getId().equals(uuid)) {
                            players.put(uuid, new PlayerData(e, x, z));
                            break;
                        }
                    }
                }
                players.get(uuid).updateTime = System.currentTimeMillis();
            } catch(NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
