package xyz.wagyourtail.voxelshare.server;

import java.util.UUID;

public class Client {
    public final UUID player;
    public int waypointFrequency, regionFrequency, positionFrequency;

    public Client(UUID player) {
        this.player = player;
    }

    public void setFrequency(int waypointFrequency, int regionFrequency, int positionFrequency) {
        this.waypointFrequency = waypointFrequency;
        this.regionFrequency = regionFrequency;
        this.positionFrequency = positionFrequency;
    }
}
