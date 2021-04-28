package xyz.wagyourtail.voxelshare.client;

import java.nio.ByteBuffer;

public interface Server {
    boolean isDedicated();

    void sendPacket(ByteBuffer buff);

    String getServerName();

    void setFrequency(int waypointFrequency, int regionFrequency, int positionFrequency);
}
