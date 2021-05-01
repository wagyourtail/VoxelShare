package xyz.wagyourtail.voxelshare.packets.s2c;

import xyz.wagyourtail.voxelshare.ConfigOptions;
import xyz.wagyourtail.voxelshare.packets.c2s.PacketConfigC2S;

import java.nio.ByteBuffer;

public class PacketConfigS2C extends PacketConfigC2S {

    public PacketConfigS2C(ConfigOptions config) {
        super(config);
    }

    public PacketConfigS2C(boolean sendWaypoint, boolean sendRegion, boolean sendPosition, int waypointFrequency, int regionFrequency, int positionFrequency) {
        super(sendWaypoint, sendRegion, sendPosition, waypointFrequency, regionFrequency, positionFrequency);
    }

    public PacketConfigS2C(ByteBuffer buff) {
        super(buff);
    }

}
