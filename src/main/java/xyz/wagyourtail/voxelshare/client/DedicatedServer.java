package xyz.wagyourtail.voxelshare.client;

import java.nio.ByteBuffer;

public class DedicatedServer implements Server {
    public final String serverName;
    public int waypointFrequency, regionFrequency, positionFrequency;

    public DedicatedServer(String sName) {
        this.serverName = sName;
    }

    public void setFrequency() {

    }

    public String getServerName() {
        return serverName;
    }

    public boolean isDedicated() {
        return true;
    }

    @Override
    public void sendPacket(ByteBuffer buff) {
        //TODO:
    }

    public void setFrequency(int waypointFrequency, int regionFrequency, int positionFrequency) {
        this.waypointFrequency = waypointFrequency;
        this.regionFrequency = regionFrequency;
        this.positionFrequency = positionFrequency;
    }

}
